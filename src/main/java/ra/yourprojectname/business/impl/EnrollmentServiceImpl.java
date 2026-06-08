package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.EnrollmentService;
import ra.yourprojectname.dao.impl.CourseDAOImpl;
import ra.yourprojectname.dao.impl.EnrollmentDAOImpl;
import ra.yourprojectname.dao.impl.StudentDAOImpl;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Student;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnrollmentServiceImpl implements EnrollmentService {
    private final CourseDAOImpl courseDAO = new CourseDAOImpl();
    private final EnrollmentDAOImpl enrollmentDAO = new EnrollmentDAOImpl();
    private final StudentDAOImpl studentDAO = new StudentDAOImpl();

    private int computeOffset(int page, int pageSize) {
        return (page - 1) * pageSize;
    }

    @Override
    public Student getStudentByEmail(String email) {
        return studentDAO.getStudentByEmail(email);
    }

    @Override
    public boolean enrollCourse(int studentId, int courseId) {
        if (enrollmentDAO.isEnrolled(studentId, courseId)) {
            System.err.println("Bạn đã đăng ký khóa học này trước đó rồi!");
            return false;
        }
        return enrollmentDAO.insertEnrollment(studentId, courseId);
    }

    @Override
    public List<Course> getCoursesByPage(int page, int pageSize) {
        return courseDAO.getAllCourseByPage(pageSize, computeOffset(page, pageSize));
    }

    @Override
    public int getTotalPages(int pageSize) {
        int totalCourses = courseDAO.countTotalCourses();
        return (int) Math.ceil((double) totalCourses / pageSize);
    }

    @Override
    public List<Course> searchCoursesByPage(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return courseDAO.findCourseByName(keyword.trim(), pageSize, offset);
    }

    @Override
    public int getSearchCoursesTotalPages(String keyword, int pageSize) {
        int total = courseDAO.countCoursesByName(keyword.trim());
        return (int) Math.ceil((double) total / pageSize);
    }

    @Override
    public List<Course> getStudentCoursesByPage(int studentId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return enrollmentDAO.getCoursesByStudentIdWithPagination(studentId, pageSize, offset);
    }

    @Override
    public int getStudentCoursesTotalPages(int studentId, int pageSize) {
        int total = enrollmentDAO.countCoursesByStudentId(studentId);
        return (int) Math.ceil((double) total / pageSize);
    }

    @Override
    public List<Course> getStudentCoursesSortedByPage(int studentId, String column, String direction, int page, int pageSize) {
        return enrollmentDAO.getCoursesByStudentIdSortedWithPagination(studentId, column, direction, pageSize, computeOffset(page, pageSize));
    }

    @Override
    public boolean cancelEnrolledCourse(int studentId, int courseId) {
        return enrollmentDAO.cancelEnrollmentByStudent(studentId, courseId);
    }

    @Override
    public boolean isCourseEnrolledByStudent(int studentId, int courseId) {
        return enrollmentDAO.isEnrolled(studentId, courseId);
    }

    @Override
    public List<Course> getRecommendedCourses(int studentId) {
        List<Course> enrolledCourses = enrollmentDAO.getCoursesByStudentIdWithPagination(studentId, 100, 0);

        if (enrolledCourses == null || enrolledCourses.isEmpty()) {
            return new ArrayList<>();
        }

        List<Course> allCourses = courseDAO.getAllCourseByPage(100, 0);
        if (allCourses == null || allCourses.isEmpty()) {
            return new ArrayList<>();
        }

        // DANH SÁCH TỪ NHIỄU MỞ RỘNG (Stopwords tiếng Việt ngành lập trình)
        Set<String> stopwords = new HashSet<>(java.util.Arrays.asList(
                "học", "trình", "khóa", "ngôn", "ngữ", "cơ", "bản", "nâng", "cao",
                "ứng", "dụng", "cho", "người", "mới", "bắt", "đầu", "phần", "mềm",
                "giải", "thuật", "cấu", "trúc", "với", "và", "tập", "luyện", "đại"
        ));

        Set<Integer> enrolledIds = new HashSet<>();
        java.util.Map<String, Integer> keywordWeights = new java.util.HashMap<>();

        for (Course c : enrolledCourses) {
            enrolledIds.add(c.getId());
            String[] words = c.getName().toLowerCase().split("\\s+");
            for (String w : words) {
                // SỬA: Không chặn độ dài >= 3 nữa để giữ lại từ "c", "go", "js"...
                // Chỉ cần từ đó không nằm trong danh sách từ nhiễu là được!
                if (!w.isEmpty() && !stopwords.contains(w)) {
                    keywordWeights.put(w, keywordWeights.getOrDefault(w, 0) + 1);
                }
            }
        }

        List<CourseWithScore> scoredCourses = new ArrayList<>();

        for (Course c : allCourses) {
            if (!enrolledIds.contains(c.getId())) {
                int finalScore = 0;
                String[] words = c.getName().toLowerCase().split("\\s+");
                for (String w : words) {
                    if (keywordWeights.containsKey(w)) {
                        finalScore += keywordWeights.get(w);
                    }
                }

                // Chỉ giữ lại khóa có điểm tương đồng thực sự
                if (finalScore > 0) {
                    scoredCourses.add(new CourseWithScore(c, finalScore));
                }
            }
        }

        if (scoredCourses.isEmpty()) {
            return new ArrayList<>();
        }

        scoredCourses.sort((a, b) -> Integer.compare(b.score, a.score));

        List<Course> recommendedList = new ArrayList<>();
        int count = 0;
        for (CourseWithScore cs : scoredCourses) {
            recommendedList.add(cs.course);
            count++;
            if (count >= 3) break;
        }

        return recommendedList;
    }

    private static class CourseWithScore {
        Course course; // Đối tượng khóa học gốc
        int score;     // Điểm số tương thích được thuật toán chấm

        // Hàm khởi tạo (Constructor)
        CourseWithScore(Course course, int score) {
            this.course = course;
            this.score = score;
        }
    }
}
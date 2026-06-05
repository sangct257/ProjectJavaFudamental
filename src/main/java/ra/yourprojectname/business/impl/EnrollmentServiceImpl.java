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
        int offset = (page - 1) * pageSize;
        return courseDAO.findAllWithPagination(pageSize, offset);
    }

    @Override
    public int getTotalPages(int pageSize) {
        int totalCourses = courseDAO.countTotalCourses();
        return (int) Math.ceil((double) totalCourses / pageSize);
    }

    // Kết nối mượt mà với hàm `findCourseByNameWithPagination` có sẵn trong CourseDAO của bạn
    @Override
    public List<Course> searchCoursesByPage(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return courseDAO.findCourseByNameWithPagination(keyword.trim(), pageSize, offset);
    }

    // Kết nối mượt mà với hàm `countCoursesByName` có sẵn trong CourseDAO của bạn
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
        int offset = (page - 1) * pageSize;
        return enrollmentDAO.getCoursesByStudentIdSortedWithPagination(studentId, column, direction, pageSize, offset);
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
        List<Course> allCourses = courseDAO.findAllWithPagination(100, 0);
        List<Course> enrolledCourses = enrollmentDAO.getCoursesByStudentIdWithPagination(studentId, 100, 0);

        if (enrolledCourses == null || enrolledCourses.isEmpty()) {
            return allCourses;
        }

        Set<Integer> enrolledIds = new HashSet<>();
        java.util.Map<String, Integer> keywordWeights = new java.util.HashMap<>();

        for (Course c : enrolledCourses) {
            enrolledIds.add(c.getId());
            String[] words = c.getName().toLowerCase().split("\\s+");
            for (String w : words) {
                if (w.length() >= 3 && !w.equals("học") && !w.equals("trình")) {
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
                if (finalScore > 0) {
                    scoredCourses.add(new CourseWithScore(c, finalScore));
                }
            }
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
        Course course;
        int score;
        CourseWithScore(Course course, int score) {
            this.course = course;
            this.score = score;
        }
    }
}
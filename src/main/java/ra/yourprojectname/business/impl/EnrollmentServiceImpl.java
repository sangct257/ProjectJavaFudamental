package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.EnrollmentService;
import ra.yourprojectname.dao.impl.CourseDAOImpl;
import ra.yourprojectname.dao.impl.EnrollmentDAOImpl;
import ra.yourprojectname.dao.impl.StudentDAOImpl;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Enrollment;
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
    public List<Course> getAllCourses(int page, int pageSize) {
        return courseDAO.getAllCourse(pageSize, computeOffset(page, pageSize));
    }

    @Override
    public int getTotalPages(int pageSize) {
        int totalCourses = courseDAO.countTotalCourses();
        return (int) Math.ceil((double) totalCourses / pageSize);
    }

    @Override
    public List<Course> getRecommendedCoursesByEnrolled(int studentId, int limit) {
        List<Course> allCourses = getAllCourses(1, 1000);
        List<Course> enrolledCourses = getCoursesByStudentId(studentId, 1, 1000);
        List<Course> recommendedList = new java.util.ArrayList<>(); //

        if (enrolledCourses == null || enrolledCourses.isEmpty()) {
            return recommendedList;
        }

        // DANH SÁCH TỪ VÔ NGHĨA (STOPWORDS) - Cần lọc bỏ trong Full-text Search
        String[] stopwords = {"khoá", "khóa", "học", "trình", "lập", "cho", "và", "với", "core", "boot", "basic", "advance", "cơ", "bản", "nâng", "cao"};

        for (Course systemCourse : allCourses) {

            // 1. Kiểm tra nếu học viên đăng ký rồi thì TUYỆT ĐỐI không đề xuất lại
            boolean isAlreadyEnrolled = false;
            for (Course enrolled : enrolledCourses) {
                if (enrolled.getId() == systemCourse.getId()) {
                    isAlreadyEnrolled = true;
                    break;
                }
            }

            // 2. Nếu CHƯA ĐĂNG KÝ, tiến hành phân tích từ khóa toàn văn (Full-text)
            if (!isAlreadyEnrolled) {
                String systemCourseName = systemCourse.getName().toLowerCase();

                // Duyệt qua các môn học viên ĐÃ đăng ký
                for (Course enrolled : enrolledCourses) {
                    String enrolledName = enrolled.getName().toLowerCase();

                    // Tách chuỗi thành mảng các từ
                    String[] keywords = enrolledName.split(" ");

                    for (String word : keywords) {
                        word = word.trim();

                        // BƯỚC QUAN TRỌNG: Kiểm tra xem từ này có phải là từ vô nghĩa không
                        boolean isStopword = false;
                        for (String stop : stopwords) {
                            if (word.equals(stop)) {
                                isStopword = true;
                                break;
                            }
                        }

                        // Bỏ qua từ vô nghĩa và từ quá ngắn (<= 1 ký tự, trừ trường hợp đặc biệt như C)
                        if (isStopword || word.length() <= 1) {
                            continue;
                        }

                        // THUẬT TOÁN KHỚP: Tên môn mới phải chứa chính xác từ khóa công nghệ (Ví dụ: "java")
                        if (systemCourseName.contains(word)) {
                            if (!recommendedList.contains(systemCourse)) {
                                recommendedList.add(systemCourse);
                            }
                            break; // Khớp từ khóa chất lượng này rồi thì chuyển sang môn tiếp theo
                        }
                    }
                }
            }

            if (recommendedList.size() >= limit) {
                break;
            }
        }
        return recommendedList;
    }

    @Override
    public List<Course> searchCoursesByName(String keyword, int page, int pageSize) {
        return courseDAO.findCourseByName(keyword.trim(), pageSize, computeOffset(page, pageSize));
    }

    @Override
    public int getSearchCoursesTotalPages(String keyword, int pageSize) {
        int total = courseDAO.countCoursesByName(keyword.trim());
        return (int) Math.ceil((double) total / pageSize);
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
    public List<Course> getCoursesByStudentId(int studentId, int page, int pageSize) {
        return enrollmentDAO.getCoursesByStudentId(studentId, pageSize, computeOffset(page, pageSize));
    }

    @Override
    public int getStudentCoursesTotalPages(int studentId, int pageSize) {
        int total = enrollmentDAO.countCoursesByStudentId(studentId);
        return (int) Math.ceil((double) total / pageSize);
    }

    @Override
    public List<Course> getCoursesByStudentIdSorted(int studentId, String column, String direction, int page, int pageSize) {
        return enrollmentDAO.getCoursesByStudentIdSorted(studentId, column, direction, pageSize, computeOffset(page, pageSize));
    }

    @Override
    public boolean cancelEnrollment(int studentId, int courseId) {
        return enrollmentDAO.cancelEnrollmentByStudent(studentId, courseId);
    }

    @Override
    public boolean isCourseEnrolled(int studentId, int courseId) {
        return enrollmentDAO.isEnrolled(studentId, courseId);
    }

    @Override
    public List<Enrollment> getStudentsByCourseId(int courseId, int page, int pageSize) {
        return enrollmentDAO.getStudentsByCourseId(courseId, pageSize, computeOffset(page, pageSize));
    }

    @Override
    public List<Enrollment> getPendingEnrollments(int page, int pageSize) {
        return enrollmentDAO.getPendingEnrollments(pageSize, computeOffset(page, pageSize));
    }

    @Override
    public boolean approveEnrollment(int studentId, int courseId) {
        return enrollmentDAO.approveEnrollment(studentId, courseId);
    }

    @Override
    public boolean isStudentInCourse(int studentId, int courseId) {
        return enrollmentDAO.isStudentInCourse(studentId, courseId);
    }

    @Override
    public boolean removeStudentFromCourse(int studentId, int courseId) {
        return enrollmentDAO.removeStudentFromCourse(studentId, courseId);
    }
}
package ra.yourprojectname.business;

import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Student;
import java.util.List;

public interface EnrollmentService {
    Student getStudentByEmail(String email);
    boolean enrollCourse(int studentId, int courseId);

    // Quản lý phân trang danh sách khóa học hệ thống
    List<Course> getCoursesByPage(int page, int pageSize);
    int getTotalPages(int pageSize);

    // Tìm kiếm khóa học theo tên (Phân trang)
    List<Course> searchCoursesByPage(String keyword, int page, int pageSize);
    int getSearchCoursesTotalPages(String keyword, int pageSize);

    // Quản lý phân trang danh sách khóa học của học viên
    List<Course> getStudentCoursesByPage(int studentId, int page, int pageSize);
    int getStudentCoursesTotalPages(int studentId, int pageSize);
    List<Course> getStudentCoursesSortedByPage(int studentId, String column, String direction, int page, int pageSize);

    boolean cancelEnrolledCourse(int studentId, int courseId);
    List<Course> getRecommendedCourses(int studentId);

    // Kiểm tra xem học viên cụ thể đã đăng ký khóa học này chưa để đánh dấu trạng thái
    boolean isCourseEnrolledByStudent(int studentId, int courseId);
}
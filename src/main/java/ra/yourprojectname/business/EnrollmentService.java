package ra.yourprojectname.business;

import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Enrollment;
import ra.yourprojectname.model.Student;
import java.util.List;

public interface EnrollmentService {
    // MENU STUDENT
    // Kiểm tra email của học viên
    Student getStudentByEmail(String email);

    // Danh sách khoá học
    List<Course> getAllCourses(int page, int pageSize);
    // Đếm số lượng khoá học
    int getTotalPages(int pageSize);

    // Hàm gợi ý các khoá học đã đăng ký cho học viên
    List<Course> getRecommendedCoursesByEnrolled(int studentId, int limit);

    // Tìm kiếm khoá học theo tên
    List<Course> searchCoursesByName(String keyword, int page, int pageSize);
    // Đếm tổng số khóa học của học viên
    int getSearchCoursesTotalPages(String keyword, int pageSize);

    // Đăng ký khoá học
    boolean enrollCourse(int studentId, int courseId);

    // Danh sách học viên đã đăng ký khoá học
    List<Course> getCoursesByStudentId(int studentId, int page, int pageSize);
    // Đếm số lượng học viên đã đăng ký khoá học
    int getStudentCoursesTotalPages(int studentId, int pageSize);

    // Sắp xếp khóa học theo cột tùy chọn (tên, ngày đăng ký,...) và hướng (ASC/DESC)
    List<Course> getCoursesByStudentIdSorted(int studentId, String column, String direction, int page, int pageSize);

    // Huỷ đăng ký khoá học
    boolean cancelEnrollment(int studentId, int courseId);

    // Kiểm tra học viên đã đăng ký khoá học hay chưa ?
    boolean isCourseEnrolled(int studentId, int courseId);

    // MENU ADMIN
    // Lấy danh sách học viên đã duyệt của một khóa học.
    List<Enrollment> getStudentsByCourseId(int courseId, int page, int pageSize);

    // Lấy danh sách học viên đăng ký nhưng trạng thái đang là chờ duyệt
    List<Enrollment> getPendingEnrollments(int page, int pageSize);

    // Đổi trạng thái đăng ký của học viên từ chờ sang đã duyệt
    boolean approveEnrollment(int studentId, int courseId);

    // Kiểm tra xem học viên có nằm trong lớp đó không (để phục vụ validate trước khi xóa)
    boolean isStudentInCourse(int studentId, int courseId);

    // Xóa bản ghi đăng ký của học viên khỏi khóa học
    boolean removeStudentFromCourse(int studentId, int courseId);
}
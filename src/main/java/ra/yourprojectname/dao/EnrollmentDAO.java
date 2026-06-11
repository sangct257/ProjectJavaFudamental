package ra.yourprojectname.dao;

import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Enrollment;
import ra.yourprojectname.model.Student;

import java.util.List;

public interface EnrollmentDAO {
    // Menu Student
    // Danh sách khoá học theo sinh viên có phân trang
    List<Course> getCoursesByStudentId(int studentId, int limit, int offset);

    // Đếm tổng số khóa học của học viên
    int countCoursesByStudentId(int studentId);

    // Đăng ký khóa học mới
    boolean insertEnrollment(int studentId, int courseId);

    //Sắp xếp khóa học (theo tên/ngày đăng ký - tăng dần/giảm dần)
    List<Course> getCoursesByStudentIdSorted(int studentId, String column, String direction, int limit, int offset);

    // Xoá khoá học theo học viên
    boolean cancelEnrollmentByStudent(int studentId, int courseId);

    // Kiểm tra trùng lặp đăng ký
    boolean isEnrolled(int studentId, int courseId);

    // Menu Admin
    // Lấy danh sách học viên đã được duyệt của một khóa học
    List<Enrollment> getStudentsByCourseId(int courseId, int limit, int offset);

    // Lấy danh sách học viên đang ở trạng thái chờ duyệt (status = false hoặc pending)
    List<Enrollment> getPendingEnrollments(int limit, int offset);

    // Cập nhật trạng thái đăng ký thành Đã duyệt (status = true)
    boolean approveEnrollment(int studentId, int courseId);

    // Kiểm tra học viên có tồn tại trong khóa học không
    boolean isStudentInCourse(int studentId, int courseId);

    // Xóa bản ghi học viên khỏi khóa học
    boolean removeStudentFromCourse(int studentId, int courseId);
}
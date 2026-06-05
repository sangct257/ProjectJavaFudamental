package ra.yourprojectname.dao;

import ra.yourprojectname.model.Course;
import java.util.List;

public interface EnrollmentDAO {
    boolean insertEnrollment(int studentId, int courseId);

    // Các hàm phục vụ phân trang danh sách khóa học của học viên
    List<Course> getCoursesByStudentIdWithPagination(int studentId, int limit, int offset);
    int countCoursesByStudentId(int studentId);
    List<Course> getCoursesByStudentIdSortedWithPagination(int studentId, String column, String direction, int limit, int offset);

    boolean cancelEnrollmentByStudent(int studentId, int courseId);
    boolean isEnrolled(int studentId, int courseId);
}
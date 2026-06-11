package ra.yourprojectname.dao;

import ra.yourprojectname.model.Student;
import java.util.List;

public interface StudentDAO {
    // Tìm học viên theo email để kiểm tra sự tồn tại của email học viên
    Student getStudentByEmail(String email);

    // Đăng nhập email và mật khẩu học viên
    boolean login(String email, String password);

    // Danh sách học viên
    List<Student> getAllStudents(int limit, int offset);

    // Đếm số lượng tổng số học viên
    int countTotalStudents();

    // Thêm mới học viên
    boolean insertStudent(Student student);

    // Tìm học viên theo id để kiểm tra sự tồn tại của id học viên
    Student getStudentById(int id);

    // Chỉnh sửa thông tin học viên (hiển thị menu con cho phép chọn thuộc tính cần sửa)
    boolean updateStudent(Student student);

    // Xóa học viên theo id (Xác nhận trước khi xóa)
    boolean deleteStudent(int id);

    // Tim kiếm học viên theo tên, email hoặc mã id (tìm kiếm tương đối)
    List<Student> findStudentByNameEmailOrId(String keyword, int limit, int offset);

    // Đếm số lượng học viên tìm kiếm theo tên, email hoặc mã id (tìm kiếm tương đối)
    int countSearchStudents(String keyword);

    // Sắp xếp học viên (theo tên/id - tăng dần/giảm dần)
    List<Student> getAllSortedByNameOrById(String column, String direction, int limit, int offset);
}
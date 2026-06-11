package ra.yourprojectname.business;

import ra.yourprojectname.model.Student;
import java.util.List;

public interface StudentService {
    // Đăng nhập với email và password học viên
    boolean login(String email, String password);

    // Danh sách học viên
    List<Student> getAllStudents(int page, int pageSize);

    int getTotalPages(int pageSize);

    // Thêm mới học viên
    boolean insertStudent(Student student);

    // Cập nhập thông tin học viên
    Student getStudentById(int id);

    boolean updateStudent(Student student);

    // Xoá học viên
    boolean deleteStudent(int id);

    // Tìm kiếm theo tên hoặc email hoặc id học viên
    List<Student> findStudentByNameEmailOrId(String keyword, int page, int pageSize);

    int getSearchTotalPages(String keyword, int pageSize);

    // Xắp xếp theo tên hoặc id học viên
    List<Student> getAllSortedByNameOrById(String column, String direction, int page, int pageSize);
}
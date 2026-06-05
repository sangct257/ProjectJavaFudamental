package ra.yourprojectname.business;

import ra.yourprojectname.model.Student;
import java.util.List;

public interface StudentService {
    boolean login(String email, String password);
    List<Student> getALLStudents();

    // Các dịch vụ phân trang mới điều hướng đầy đủ danh sách, tìm kiếm, sắp xếp
    List<Student> getStudentsByPage(int page, int pageSize);
    int getTotalPages(int pageSize);

    List<Student> searchStudentsByPage(String keyword, int page, int pageSize);
    int getSearchTotalPages(String keyword, int pageSize);

    List<Student> getStudentsSortedByPage(String column, String direction, int page, int pageSize);

    boolean insertStudent(Student student);
    boolean updateStudent(Student student);
    boolean deleteStudent(int id);
}
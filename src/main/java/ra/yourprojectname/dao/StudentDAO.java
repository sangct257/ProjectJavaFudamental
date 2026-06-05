package ra.yourprojectname.dao;

import ra.yourprojectname.model.Student;
import java.util.List;

public interface StudentDAO {
    boolean login(String email, String password);
    List<Student> findAll();

    // 1. Phân trang danh sách mặc định
    List<Student> findAllWithPagination(int limit, int offset);
    int countTotalStudents();

    // 2. Phân trang tìm kiếm
    List<Student> findStudentByNameEmailOrIdWithPagination(String keyword, int limit, int offset);
    int countSearchStudents(String keyword);

    // 3. Phân trang sắp xếp
    List<Student> findAllSortedWithPagination(String column, String direction, int limit, int offset);

    boolean insertStudent(Student student);
    boolean updateStudent(Student student);
    boolean deleteStudent(int id);
    Student getStudentByEmail(String email);
}
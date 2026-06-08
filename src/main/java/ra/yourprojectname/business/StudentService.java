package ra.yourprojectname.business;

import ra.yourprojectname.model.Student;
import java.util.List;

public interface StudentService {
    boolean login(String email, String password);
    List<Student> getAllStudents(int page, int pageSize);
    int getTotalPages(int pageSize);

    boolean insertStudent(Student student);
    Student getStudentById(int id);
    boolean updateStudent(Student student);
    boolean deleteStudent(int id);

    List<Student> findStudentByNameEmailOrId(String keyword, int page, int pageSize);
    int getSearchTotalPages(String keyword, int pageSize);

    List<Student> findAllSorted(String column, String direction, int page, int pageSize);
}
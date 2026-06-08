package ra.yourprojectname.dao;

import ra.yourprojectname.model.Student;
import java.util.List;

public interface StudentDAO {
    boolean login(String email, String password);

    List<Student> getAllStudents(int limit, int offset);
    int countTotalStudents();

    boolean insertStudent(Student student);
    Student getStudentById(int id);
    boolean updateStudent(Student student);
    boolean deleteStudent(int id);
    Student getStudentByEmail(String email);

    List<Student> findStudentByNameEmailOrId(String keyword, int limit, int offset);
    int countSearchStudents(String keyword);

    List<Student> findAllSorted(String column, String direction, int limit, int offset);
}
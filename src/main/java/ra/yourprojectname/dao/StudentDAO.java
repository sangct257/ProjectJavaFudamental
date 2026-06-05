package ra.yourprojectname.dao;

import ra.yourprojectname.model.Student;

import java.util.List;

public interface StudentDAO {
    boolean login(String email, String password);
    List<Student> findAll();
    boolean insertStudent(Student student);
    boolean updateStudent(Student student);
    boolean deleteStudent(int id);
    List<Student> findStudentByNameEmailOrId(String keyword);
    List<Student> findAllSorted(String column, String direction);
    Student getStudentByEmail(String email);
}

package ra.yourprojectname.dao;

import ra.yourprojectname.model.Student;

import java.util.List;

public interface StudentDAO {
    boolean login(String email, String password);
    List<String> findAll();
    boolean insertStudent(Student student);
    boolean updateStudent(Student student);
    boolean deleteStudent(int id);
    Student findStudentById(int id);
    Student findStudentByEmail(String email);
}

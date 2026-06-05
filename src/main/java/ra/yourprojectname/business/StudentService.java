package ra.yourprojectname.business;

import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Student;

import java.util.List;

public interface StudentService {
    boolean login(String email, String password);
    List<Student> getALLStudents();
    boolean insertStudent(Student student);
    boolean updateStudent(Student student);
    boolean deleteStudent(int id);
    List<Student> searchStudents(String keyword);
    List<Student> getStudentSorted(String column, String direction);
}

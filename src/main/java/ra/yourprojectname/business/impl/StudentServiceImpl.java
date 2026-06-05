package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.StudentService;
import ra.yourprojectname.dao.impl.StudentDAOImpl;
import ra.yourprojectname.model.Student;

import java.util.List;

public class StudentServiceImpl implements StudentService {
    private final StudentDAOImpl studentDAO = new StudentDAOImpl();

    @Override
    public boolean login(String email, String password) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                !email.trim().matches(emailRegex)) {
            return false;
        }
        return new StudentDAOImpl().login(email, password);
    }

    @Override
    public List<Student> getALLStudents() {
        return studentDAO.findAll();
    }

    @Override
    public boolean insertStudent(Student student) {
        return studentDAO.insertStudent(student);
    }

    @Override
    public boolean updateStudent(Student student) {
        return studentDAO.updateStudent(student);
    }

    @Override
    public boolean deleteStudent(int id) {
        return studentDAO.deleteStudent(id);
    }

    @Override
    public List<Student> searchStudents(String keyword) {
        return studentDAO.findStudentByNameEmailOrId(keyword);
    }

    @Override
    public List<Student> getStudentSorted(String column, String direction) {
        return studentDAO.findAllSorted(column, direction);
    }
}

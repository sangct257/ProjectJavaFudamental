package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.StudentService;
import ra.yourprojectname.dao.StudentDAO;
import ra.yourprojectname.dao.impl.StudentDAOImpl;
import ra.yourprojectname.model.Student;

import java.util.List;

public class StudentServiceImpl implements StudentService {
    private final StudentDAO studentDAO = new StudentDAOImpl();

    @Override
    public boolean login(String email, String password) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty() || !email.trim().matches(emailRegex)) {
            return false;
        }
        return studentDAO.login(email, password);
    }

    @Override
    public List<Student> getAllStudents(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return studentDAO.getAllStudents(pageSize, offset);
    }

    @Override
    public int getTotalPages(int pageSize) {
        int total = studentDAO.countTotalStudents();
        return (int) Math.ceil((double) total / pageSize);
    }

    @Override
    public boolean insertStudent(Student student) {
        return studentDAO.insertStudent(student);
    }

    @Override
    public Student getStudentByEmail(String email) {
        return studentDAO.getStudentByEmail(email);
    }

    @Override
    public Student getStudentById(int id) {
        return studentDAO.getStudentById(id);
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
    public List<Student> findStudentByNameEmailOrId(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return studentDAO.findStudentByNameEmailOrId(keyword.trim(), pageSize, offset);
    }

    @Override
    public int getSearchTotalPages(String keyword, int pageSize) {
        int total = studentDAO.countSearchStudents(keyword.trim());
        return (int) Math.ceil((double) total / pageSize);
    }

    @Override
    public List<Student> getAllSortedByNameOrById(String column, String direction, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return studentDAO.getAllSortedByNameOrById(column, direction, pageSize, offset);
    }
}
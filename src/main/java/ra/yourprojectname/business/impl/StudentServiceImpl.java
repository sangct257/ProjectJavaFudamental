package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.StudentService;
import ra.yourprojectname.dao.impl.StudentDAOImpl;

public class StudentServiceImpl implements StudentService {
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
}

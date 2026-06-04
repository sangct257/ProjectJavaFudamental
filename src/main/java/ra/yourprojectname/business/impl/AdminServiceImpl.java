package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.AdminService;
import ra.yourprojectname.dao.impl.AdminDAOImpl;

public class AdminServiceImpl implements AdminService {

    @Override
    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ) {
            return false;
        }
        return new AdminDAOImpl().login(username, password);
    }
}

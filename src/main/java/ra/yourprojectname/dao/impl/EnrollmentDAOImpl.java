package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.EnrollmentDAO;

public class EnrollmentDAOImpl implements EnrollmentDAO {
    @Override
    public boolean insertEnrollment(int studentId, int courseId) {
        return false;
    }

    @Override
    public boolean cancelEnrollment(int id) {
        return false;
    }

    @Override
    public boolean updateEnrollment(int id, int status) {
        return false;
    }

    @Override
    public boolean deleteEnrollment(int id) {
        return false;
    }

    @Override
    public boolean isEnrolled(int studentId, int courseId) {
        return false;
    }

    @Override
    public boolean isEnrolled(int studentId) {
        return false;
    }
}

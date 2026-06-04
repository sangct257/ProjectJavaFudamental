package ra.yourprojectname.dao;

public interface EnrollmentDAO {
    boolean insertEnrollment(int studentId, int courseId);
    boolean cancelEnrollment(int id);
    boolean updateEnrollment(int id, int status);
    boolean deleteEnrollment(int id);
    boolean isEnrolled(int studentId, int courseId);
    boolean isEnrolled(int studentId);
}

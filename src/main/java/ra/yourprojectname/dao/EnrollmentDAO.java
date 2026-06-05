package ra.yourprojectname.dao;

import ra.yourprojectname.model.Course;

import java.util.List;

public interface EnrollmentDAO {
    boolean insertEnrollment(int studentId, int courseId);
    List<Course> getCoursesByStudentId(int studentId);
    List<Course> getCoursesByStudentIdSorted(int studentId, String column, String direction);
    boolean cancelEnrollmentByStudent(int studentId, int courseId);
    boolean isEnrolled(int studentId, int courseId);
}

package ra.yourprojectname.business;

import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Student;

import java.util.List;

public interface EnrollmentService {
    Student getStudentByEmail(String email);
    List<Course> getAllCourses();
    boolean enrollCourse(int studentId, int courseId);
    List<Course> getCoursesByStudentId(int studentId);
    List<Course> getCoursesByStudentIdSorted(int studentId, String column, String direction);
    boolean cancelEnrolledCourse(int studentId, int courseId);

}

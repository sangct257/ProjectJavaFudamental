package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.EnrollmentService;
import ra.yourprojectname.dao.impl.CourseDAOImpl;
import ra.yourprojectname.dao.impl.EnrollmentDAOImpl;
import ra.yourprojectname.dao.impl.StudentDAOImpl;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Student;


import java.util.List;

public class EnrollmentServiceImpl implements EnrollmentService {
    private final CourseDAOImpl courseDAO = new CourseDAOImpl();
    private final EnrollmentDAOImpl enrollmentDAO = new EnrollmentDAOImpl();
    private final StudentDAOImpl studentDAO = new StudentDAOImpl();

    @Override
    public Student getStudentByEmail(String email) {
        return studentDAO.getStudentByEmail(email);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    @Override
    public boolean enrollCourse(int studentId, int courseId) {
        if (enrollmentDAO.isEnrolled(studentId, courseId)) {
            System.out.println("Bạn đã đăng ký khóa học này trước đó rồi!");
            return false;
        }
        return enrollmentDAO.insertEnrollment(studentId, courseId);
    }

    @Override
    public List<Course> getCoursesByStudentId(int studentId) {
        return enrollmentDAO.getCoursesByStudentId(studentId);
    }

    @Override
    public List<Course> getCoursesByStudentIdSorted(int studentId, String column, String direction) {
        return enrollmentDAO.getCoursesByStudentIdSorted(studentId,column,direction);
    }

    @Override
    public boolean cancelEnrolledCourse(int studentId, int courseId) {
        return enrollmentDAO.cancelEnrollmentByStudent(studentId, courseId);
    }
}

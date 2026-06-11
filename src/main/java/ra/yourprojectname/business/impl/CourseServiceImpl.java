package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.CourseService;
import ra.yourprojectname.dao.CourseDAO;
import ra.yourprojectname.dao.impl.CourseDAOImpl;
import ra.yourprojectname.model.Course;

import java.util.List;

public class CourseServiceImpl implements CourseService {
    private final CourseDAO courseDAO = new CourseDAOImpl();

    private int computeOffset(int page, int pageSize) {
        return (page - 1) * pageSize;
    }

    @Override
    public List<Course> getAllCourse(int page, int pageSize) {
        return courseDAO.getAllCourse(pageSize, computeOffset(page, pageSize));
    }

    @Override
    public int countTotalCourses(int pageSize) {
        return (int) Math.ceil((double) courseDAO.countTotalCourses() / pageSize);
    }

    @Override
    public boolean insertCourse(Course course) {
        return courseDAO.insertCourse(course);
    }

    @Override
    public Course getCourseById(int id) { return courseDAO.getCourseById(id); }

    @Override
    public boolean updateCourse(Course course) {
        return courseDAO.updateCourse(course);
    }

    @Override
    public boolean deleteCourse(int id) {
        return courseDAO.deleteCourse(id);
    }

    @Override
    public List<Course> findCourseByName(String name, int page, int pageSize) {
        return courseDAO.findCourseByName(name, pageSize, computeOffset(page, pageSize));
    }

    @Override
    public int countCoursesByName(String name, int pageSize) {
        return (int) Math.ceil((double) courseDAO.countCoursesByName(name) / pageSize);
    }

    @Override
    public List<Course> getAllSortedByNameOrById(String column, String direction, int page, int pageSize) {
        return courseDAO.getAllSortedByNameOrById(column, direction, pageSize, computeOffset(page, pageSize));
    }
}

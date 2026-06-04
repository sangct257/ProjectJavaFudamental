package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.CourseService;
import ra.yourprojectname.dao.impl.CourseDAOImpl;
import ra.yourprojectname.model.Course;

import java.util.List;

public class CourseServiceImpl implements CourseService {
    @Override
    public List<Course> getCourse() {
        return new CourseDAOImpl().findAll();
    }

    @Override
    public boolean insertCourse(Course course) {
        return new CourseDAOImpl().insertCourse(course);
    }

    @Override
    public boolean updateCourse(Course course) {
        return new CourseDAOImpl().updateCourse(course);
    }

    @Override
    public boolean deleteCourse(int id) {
        return new CourseDAOImpl().deleteCourse(id);
    }

    @Override
    public List<Course> findCourseByName(String name) {
        return new CourseDAOImpl().findCourseByName(name);
    }

    @Override
    public List<Course> getCourseSorted(String column, String direction) {
        return new CourseDAOImpl().findAllSorted(column, direction);
    }
}

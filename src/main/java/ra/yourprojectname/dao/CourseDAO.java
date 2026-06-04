package ra.yourprojectname.dao;

import ra.yourprojectname.model.Course;

import java.util.List;

public interface CourseDAO {
    List<Course> findAll();
    boolean insertCourse(Course course);
    boolean updateCourse(Course course);
    boolean deleteCourse(int id);
    List<Course> findCourseByName(String name);
    List<Course> findAllSorted(String orderByColumn, String direction);

}

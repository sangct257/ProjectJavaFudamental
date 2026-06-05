package ra.yourprojectname.dao;

import ra.yourprojectname.model.Course;

import java.util.List;

public interface CourseDAO {
    List<Course> findAllWithPagination(int limit, int offset);
    int countTotalCourses();
    boolean insertCourse(Course course);
    boolean updateCourse(Course course);
    boolean deleteCourse(int id);
    List<Course> findCourseByNameWithPagination(String name, int limit, int offset);
    int countCoursesByName(String name);
    List<Course> findAllSortedWithPagination(String orderByColumn, String direction, int limit, int offset);


}

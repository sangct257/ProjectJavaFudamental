package ra.yourprojectname.business;

import ra.yourprojectname.model.Course;

import java.util.List;

public interface CourseService {
    List<Course> getCoursesByPage(int page, int pageSize);
    int getTotalPages(int pageSize);

    boolean insertCourse(Course course);
    boolean updateCourse(Course course);
    boolean deleteCourse(int id);
    List<Course> searchCoursesByPage(String name, int page, int pageSize);
    int getTotalPagesForSearch(String name, int pageSize);
    List<Course> getSortedCoursesByPage(String column, String direction, int page, int pageSize);
}

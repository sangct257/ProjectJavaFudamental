package ra.yourprojectname.business;

import ra.yourprojectname.model.Course;
import java.util.List;

public interface CourseService {
    List<Course> getAllCourseByPage(int page, int pageSize);
    int getTotalPages(int pageSize);
    boolean insertCourse(Course course);
    boolean updateCourse(Course course);
    boolean deleteCourse(int id);
    List<Course> findCourseByName(String name, int page, int pageSize);
    int countCoursesByName(String name, int pageSize);
    List<Course> findAllSorted(String column, String direction, int page, int pageSize);
    Course getCourseById(int id); // Phương thức nghiệp vụ mới bổ sung
}
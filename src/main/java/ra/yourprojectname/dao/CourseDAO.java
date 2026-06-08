package ra.yourprojectname.dao;

import ra.yourprojectname.model.Course;
import java.util.List;

public interface CourseDAO {
    List<Course> getAllCourse(int limit, int offset);
    int countTotalCourses();
    boolean insertCourse(Course course);
    boolean updateCourse(Course course);
    boolean deleteCourse(int id);
    List<Course> findCourseByName(String name, int limit, int offset);
    int countCoursesByName(String name);
    List<Course> findAllSorted(String orderByColumn, String direction, int limit, int offset);
    Course findCourseById(int id); // Hàm bổ sung giúp tối ưu hóa tiến trình Update
}
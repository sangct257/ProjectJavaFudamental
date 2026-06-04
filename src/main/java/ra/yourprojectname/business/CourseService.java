package ra.yourprojectname.business;

import ra.yourprojectname.model.Course;

import java.util.List;

public interface CourseService {
    List<Course> getCourse();
    boolean insertCourse(Course course);
    boolean updateCourse(Course course);
    boolean deleteCourse(int id);
    List<Course> findCourseByName(String name);
    List<Course> getCourseSorted(String column, String direction);
}

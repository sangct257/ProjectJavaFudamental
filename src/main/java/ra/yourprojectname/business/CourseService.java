package ra.yourprojectname.business;

import ra.yourprojectname.model.Course;
import java.util.List;

public interface CourseService {
    //Danh sách khoá học
    List<Course> getAllCourse(int page, int pageSize);
    int getTotalPages(int pageSize);
    // Thêm khoá học
    boolean insertCourse(Course course);
    // Cập nhập khoá học
    Course getCourseById(int id); // Phương thức nghiệp vụ mới bổ sung
    boolean updateCourse(Course course);
    // Xoá khoá học
    boolean deleteCourse(int id);
    // Tìm kiếm khoá học theo tên
    List<Course> findCourseByName(String name, int page, int pageSize);
    int countCoursesByName(String name, int pageSize);

    // Sắp xếp khoá học theo tên hoặc id
    List<Course> getAllSorted(String column, String direction, int page, int pageSize);
}
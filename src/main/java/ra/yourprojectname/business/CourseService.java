package ra.yourprojectname.business;

import ra.yourprojectname.model.Course;
import java.util.List;

public interface CourseService {
    //Danh sách khoá học
    List<Course> getAllCourse(int page, int pageSize);

    // Đếm số lượng tổng số khoá học
    int countTotalCourses(int pageSize);

    // Thêm khoá học
    boolean insertCourse(Course course);

    // Tìm khoá học theo id để kiểm tra sự tồn tại của id khoá học
    Course getCourseById(int id);

    // Chỉnh sửa thông tin khóa học (hiển thị menu con cho phép chọn thuộc tính cần sửa)
    boolean updateCourse(Course course);

    // Xoá khoá học
    boolean deleteCourse(int id);

    // Tim kiếm khóa học theo tên (tìm kiếm tương đối)
    List<Course> findCourseByName(String name, int page, int pageSize);

    // Đếm số lượng khoá học tìm kiếm theo tên
    int countCoursesByName(String name, int pageSize);

    // Sắp xếp khóa học (theo tên/id - tăng dần/giảm dần)
    List<Course> getAllSortedByNameOrById(String column, String direction, int page, int pageSize);
}
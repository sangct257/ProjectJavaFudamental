package ra.yourprojectname.dao;

import ra.yourprojectname.model.Course;
import java.util.List;

public interface CourseDAO {
    // Danh sách khoá học có phân trang
    List<Course> getAllCourse(int limit, int offset);

    // Đếm số lượng tổng số khoá học
    int countTotalCourses();

    // Thêm mới khoá học
    boolean insertCourse(Course course);

    // Tìm khoá học theo id để kiểm tra sự tồn tại của id khoá học
    Course getCourseById(int id);

    // Chỉnh sửa thông tin khóa học (hiển thị menu con cho phép chọn thuộc tính cần sửa)
    boolean updateCourse(Course course);

    // Xoá khoá học theo id
    boolean deleteCourse(int id);

    // Tìm kiếm khoá học theo theo tên tương đối có phân trang
    List<Course> findCourseByName(String name, int limit, int offset);

    // Đếm số lượng khoá học tìm kiếm theo tên
    int countCoursesByName(String name);

    // Sắp xếp khóa học (theo tên/id - tăng dần/giảm dần)
    List<Course> getAllSortedByNameOrById(String orderByColumn, String direction, int limit, int offset);
}
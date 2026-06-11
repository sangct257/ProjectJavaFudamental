package ra.yourprojectname.dao;

import java.util.Map;

public interface StatisticalDAO {
    // Thống kê tổng số lượng khóa học
    int getTotalCoursesCount();

    // Thống kê tổng số lượng học viên
    int getTotalStudentsCount();

    // Thống kê tổng số học viên theo từng khóa
    Map<String, Integer> getStudentCountByCourse(int page, int pageSize);

    // Thống kê top 5 khóa học đông sinh viên nhất
    Map<String, Integer> getTop5PopularCourses();

    // Liệt kê các khóa học có trên 10 học viên
    Map<String, Integer> getCoursesWithMoreThan10Students();
}

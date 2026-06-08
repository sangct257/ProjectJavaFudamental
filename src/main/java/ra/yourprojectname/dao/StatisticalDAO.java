package ra.yourprojectname.dao;

import java.util.Map;

public interface StatisticalDAO {
    int getTotalCoursesCount();
    int getTotalStudentsCount();
    Map<String, Integer> getStudentCountByCourse(int page, int pageSize);
    Map<String, Integer> getTop5PopularCourses();
    Map<String, Integer> getCoursesWithMoreThan10Students();
}

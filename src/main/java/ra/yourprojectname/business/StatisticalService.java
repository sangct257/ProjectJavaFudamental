package ra.yourprojectname.business;

import java.util.Map;

public interface StatisticalService {
    int getTotalCoursesCount();
    int getTotalStudentsCount();
    Map<String, Integer> getStudentCountByCourse(int page, int pageSize);
    Map<String, Integer> getTop5PopularCourses();
    Map<String, Integer> getCoursesWithMoreThan10Students();
}

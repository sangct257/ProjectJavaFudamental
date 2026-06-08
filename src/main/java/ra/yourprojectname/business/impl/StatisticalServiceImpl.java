package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.StatisticalService;
import ra.yourprojectname.dao.StatisticalDAO;
import ra.yourprojectname.dao.impl.StatisticalDAOImpl;

import java.util.Map;

public class StatisticalServiceImpl implements StatisticalService {
    private final StatisticalDAO statisticalDAO = new StatisticalDAOImpl();
    @Override
    public int getTotalCoursesCount() {
        return statisticalDAO.getTotalCoursesCount();
    }

    @Override
    public int getTotalStudentsCount() {
        return statisticalDAO.getTotalStudentsCount();
    }

    @Override
    public Map<String, Integer> getStudentCountByCourse(int page, int pageSize) {
        return statisticalDAO.getStudentCountByCourse(page, pageSize);
    }

    @Override
    public Map<String, Integer> getTop5PopularCourses() {
        return statisticalDAO.getTop5PopularCourses();
    }

    @Override
    public Map<String, Integer> getCoursesWithMoreThan10Students() {
        return statisticalDAO.getCoursesWithMoreThan10Students();
    }
}

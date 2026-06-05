package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.CourseService;
import ra.yourprojectname.dao.impl.CourseDAOImpl;
import ra.yourprojectname.model.Course;

import java.util.List;

public class CourseServiceImpl implements CourseService {
    @Override
    public List<Course> getCoursesByPage(int page, int pageSize) {
        // Xử lý tính toán logic offset tại tầng Service theo đúng kiến trúc mong muốn
        int offset = (page - 1) * pageSize;
        int limit = pageSize;
        return new CourseDAOImpl().findAllWithPagination(limit, offset);
    }

    @Override
    public int getTotalPages(int pageSize) {
        int totalCourses = new CourseDAOImpl().countTotalCourses();
        return (int) Math.ceil((double) totalCourses / pageSize);
    }

    @Override
    public boolean insertCourse(Course course) {
        return new CourseDAOImpl().insertCourse(course);
    }

    @Override
    public boolean updateCourse(Course course) {
        return new CourseDAOImpl().updateCourse(course);
    }

    @Override
    public boolean deleteCourse(int id) {
        return new CourseDAOImpl().deleteCourse(id);
    }

    @Override
    public List<Course> searchCoursesByPage(String name, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return new CourseDAOImpl().findCourseByNameWithPagination(name, pageSize, offset);
    }

    @Override
    public int getTotalPagesForSearch(String name, int pageSize) {
        int total = new CourseDAOImpl().countCoursesByName(name);
        return (int) Math.ceil((double) total / pageSize);
    }

    @Override
    public List<Course> getSortedCoursesByPage(String column, String direction, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return new CourseDAOImpl().findAllSortedWithPagination(column, direction, pageSize, offset);
    }
}

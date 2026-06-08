package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.CourseDAO;
import ra.yourprojectname.data_login.CheckLogin;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.until.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public List<Course> getAllCourseByPage(int limit, int offset) {
        List<Course> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBUtility.openConnection();
            // Câu lệnh SQL nhận tham số LIMIT (số dòng lấy ra) và OFFSET (vị trí bắt đầu lấy)
            String sql = "SELECT * FROM Course ORDER BY id ASC LIMIT ? OFFSET ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructor(rs.getString("instructor"));
                course.setCreate_at(rs.getDate("create_at"));
                list.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public int countTotalCourses() {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBUtility.openConnection();
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Course");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return 0;
    }

    @Override
    public boolean insertCourse(Course course) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;

        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("INSERT INTO Course(name, duration, instructor) VALUES(?, ?, ?)");
            pstmt.setString(1, course.getName());
            pstmt.setInt(2, course.getDuration());
            pstmt.setString(3, course.getInstructor());
            flag = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(null, pstmt, con);
        }

        return flag;
    }

    @Override
    public boolean updateCourse(Course course) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("UPDATE Course SET name = ?, duration = ?, instructor = ? WHERE id = ?");
            pstmt.setString(1, course.getName());
            pstmt.setInt(2, course.getDuration());
            pstmt.setString(3, course.getInstructor());
            pstmt.setInt(4, course.getId());
            flag = pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(null, pstmt, con);
        }
        return flag;
    }

    @Override
    public boolean deleteCourse(int id) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("DELETE FROM Course WHERE id = ?");
            pstmt.setInt(1, id);
            flag = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(null, pstmt, con);
        }
        return flag;
    }

    @Override
    public List<Course> findCourseByName(String name, int limit, int offset) {
        List<Course> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBUtility.openConnection();
            // Thêm LIMIT và OFFSET vào cuối câu lệnh tìm kiếm
            String sql = "SELECT * FROM Course WHERE name ILIKE ? ORDER BY id ASC LIMIT ? OFFSET ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + name + "%");
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructor(rs.getString("instructor"));
                course.setCreate_at(rs.getDate("create_at"));
                list.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public int countCoursesByName(String name) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBUtility.openConnection();
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Course WHERE name ILIKE ?");
            pstmt.setString(1, "%" + name + "%");
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return 0;
    }

    @Override
    public List<Course> findAllSorted(String orderByColumn, String direction, int limit, int offset) {
        List<Course> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBUtility.openConnection();
            // Kết hợp Sắp xếp động + Phân trang động
            String sql = "SELECT * FROM Course ORDER BY " + orderByColumn + " " + direction + " LIMIT ? OFFSET ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getString("instructor"),
                        rs.getDate("create_at")
                );
                list.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public Course findCourseById(int id) {
        Course course = new Course();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT * FROM Course WHERE id = ?");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructor(rs.getString("instructor"));
                course.setCreate_at(rs.getDate("create_at"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return course;
    }
}

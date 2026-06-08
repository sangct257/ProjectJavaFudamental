package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.StatisticalDAO;
import ra.yourprojectname.until.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticalDAOImpl implements StatisticalDAO {
    // 1.1 Lấy tổng số lượng khóa học hiện có
    @Override
    public int getTotalCoursesCount() {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM course");
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

    // 1.2 Lấy tổng số lượng học viên hiện có trên hệ thống
    @Override
    public int getTotalStudentsCount() {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM student");
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

    // 2. Thống kê số lượng học viên theo từng khóa học (Có phân trang)
    @Override
    public Map<String, Integer> getStudentCountByCourse(int page, int pageSize) {
        Map<String, Integer> map = new LinkedHashMap<>();
        int offset = (page - 1) * pageSize;
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT c.name AS course_name, COUNT(e.student_id) AS total_students " +
                    "FROM course c " +
                    "LEFT JOIN enrollment e ON c.id = e.course_id " +
                    "GROUP BY c.id, c.name " +
                    "ORDER BY total_students DESC, c.id ASC " +
                    "LIMIT ? OFFSET ?");
            pstmt.setInt(1, pageSize);
            pstmt.setInt(2, offset);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("course_name"), rs.getInt("total_students"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return map;
    }

    // 3. Thống kê Top 5 khóa học đông học viên nhất
    @Override
    public Map<String, Integer> getTop5PopularCourses() {
        Map<String, Integer> map = new LinkedHashMap<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT c.name AS course_name, COUNT(e.student_id) AS total_students " +
                    "FROM course c " +
                    "JOIN enrollment e ON c.id = e.course_id " +
                    "GROUP BY c.id, c.name " +
                    "ORDER BY total_students DESC, c.id ASC " +
                    "LIMIT 5");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("course_name"), rs.getInt("total_students"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }

        return map;
    }

    // 4. Liệt kê các khóa học có trên 10 học viên
    @Override
    public Map<String, Integer> getCoursesWithMoreThan10Students() {
        Map<String, Integer> map = new LinkedHashMap<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT c.name AS course_name, COUNT(e.student_id) AS total_students " +
                    "FROM course c " +
                    "JOIN enrollment e ON c.id = e.course_id " +
                    "GROUP BY c.id, c.name " +
                    "HAVING COUNT(e.student_id) > 10 " +
                    "ORDER BY total_students DESC, c.id ASC");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("course_name"), rs.getInt("total_students"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return map;
    }
}

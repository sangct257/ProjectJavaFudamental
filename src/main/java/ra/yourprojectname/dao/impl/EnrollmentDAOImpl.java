package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.EnrollmentDAO;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.until.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements EnrollmentDAO {

    @Override
    public boolean insertEnrollment(int studentId, int courseId) {
        boolean flag = true;
        Connection con = null;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("INSERT INTO enrollment (student_id, course_id) VALUES (?, ?)");
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            flag = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(null, pstmt, con);
        }

        return flag;
    }

    @Override
    public List<Course> getCoursesByStudentId(int studentId) {
        List<Course> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT c.* FROM Course c JOIN enrollment e ON c.id = e.course_id WHERE e.student_id = ? ORDER BY e.id ASC");
            pstmt.setInt(1,studentId);
            rs = pstmt.executeQuery();
            while (rs.next()){
                list.add(new Course(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getString("instructor"),
                        rs.getDate("create_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public List<Course> getCoursesByStudentIdSorted(int studentId, String column, String direction) {
        List<Course> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            String orderTarget = column.equalsIgnoreCase("date") ? "e.create_at" : "c." + column;
            String sql = "SELECT c.* FROM Course c JOIN enrollment e ON c.id = e.course_id WHERE e.student_id = ? ORDER BY " + orderTarget + " " + direction;
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,studentId);
            rs = pstmt.executeQuery();
            while (rs.next()){
                list.add(new Course(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getString("instructor"),
                        rs.getDate("create_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public boolean cancelEnrollmentByStudent(int studentId, int courseId) {
        boolean flag = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("DELETE FROM enrollment WHERE student_id = ? AND course_id = ? AND status = 'WAITING'::enrollment_status");
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            flag = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(null, pstmt, con);
        }

        return flag;
    }

    @Override
    public boolean isEnrolled(int studentId, int courseId) {
        boolean flag = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM enrollment WHERE student_id = ? AND course_id = ?");
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            rs = pstmt.executeQuery();
            if(rs.next()){
                flag = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return flag;
    }
}

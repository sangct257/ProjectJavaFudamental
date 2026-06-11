package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.EnrollmentDAO;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Enrollment;
import ra.yourprojectname.model.EnrollmentStatus;
import ra.yourprojectname.model.Student;
import ra.yourprojectname.until.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements EnrollmentDAO {

    @Override
    public List<Course> getCoursesByStudentId(int studentId, int limit, int offset) {
        List<Course> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBUtility.openConnection();
            String sql = "SELECT c.* FROM Course c JOIN enrollment e ON c.id = e.course_id " +
                    "WHERE e.student_id = ? ORDER BY e.id ASC LIMIT ? OFFSET ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
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
    public int countCoursesByStudentId(int studentId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBUtility.openConnection();
            String sql = "SELECT COUNT(*) FROM enrollment WHERE student_id = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, studentId);
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
    public boolean insertEnrollment(int studentId, int courseId) {
        boolean flag = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DBUtility.openConnection();
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
    public List<Course> getCoursesByStudentIdSorted(int studentId, String column, String direction, int limit, int offset) {
        List<Course> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            String orderTarget = column.equalsIgnoreCase("date") ? "e.create_at" : "c." + column;
            String sql = "SELECT c.* FROM Course c JOIN enrollment e ON c.id = e.course_id " +
                    "WHERE e.student_id = ? ORDER BY " + orderTarget + " " + direction + " LIMIT ? OFFSET ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
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
        Connection con;
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
        try {
            con = DBUtility.openConnection();
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

    @Override
    public List<Enrollment> getStudentsByCourseId(int courseId, int limit, int offset) {
        List<Enrollment> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBUtility.openConnection();
            // ĐÃ SỬA: SELECT đầy đủ thông tin của cả 3 bảng và JOIN thêm bảng course c
            String sql = "SELECT e.id AS enrollment_id, e.status, e.registered_at, " +
                    "s.id AS student_id, s.name AS student_name, s.dob, s.email, s.sex, s.phone, s.password, s.create_at, " +
                    "c.id AS course_id, c.name AS course_name, c.instructor " +
                    "FROM enrollment e " +
                    "JOIN Student s ON e.student_id = s.id " +
                    "JOIN Course c ON e.course_id = c.id " +
                    "WHERE e.course_id = ? ORDER BY e.id ASC LIMIT ? OFFSET ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // 1. Đóng gói Object Student
                Student student = new Student(
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getDate("dob"),
                        rs.getString("email"),
                        rs.getBoolean("sex"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getDate("create_at")
                );

                // 2. Đóng gói Object Course (Đầy đủ tên và giảng viên)
                Course course = new Course();
                course.setId(rs.getInt("course_id"));
                course.setName(rs.getString("course_name"));
                course.setInstructor(rs.getString("instructor"));

                // 3. Xử lý trạng thái Enum
                String statusStr = rs.getString("status");
                EnrollmentStatus status = EnrollmentStatus.valueOf(statusStr);

                // 4. Đóng gói vào đối tượng Enrollment tổng
                Enrollment enrollment = new Enrollment();
                enrollment.setId(rs.getInt("enrollment_id"));
                enrollment.setStudent(student);
                enrollment.setCourse(course);

                if (rs.getTimestamp("registered_at") != null) {
                    enrollment.setRegisteredAt(rs.getTimestamp("registered_at").toLocalDateTime());
                }
                enrollment.setStatus(status);

                list.add(enrollment);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public List<Enrollment> getPendingEnrollments(int limit, int offset) {
        List<Enrollment> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBUtility.openConnection();
            // ĐÃ SỬA: SELECT đầy đủ thông tin của cả 3 bảng cho danh sách chờ duyệt WAITING
            String sql = "SELECT e.id AS enrollment_id, e.status, e.registered_at, " +
                    "s.id AS student_id, s.name AS student_name, s.dob, s.email, s.sex, s.phone, s.password, s.create_at, " +
                    "c.id AS course_id, c.name AS course_name, c.instructor " +
                    "FROM enrollment e " +
                    "JOIN Student s ON e.student_id = s.id " +
                    "JOIN Course c ON e.course_id = c.id " +
                    "WHERE e.status = 'WAITING'::enrollment_status " +
                    "ORDER BY e.id ASC LIMIT ? OFFSET ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // 1. Đóng gói Object Student
                Student student = new Student(
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getDate("dob"),
                        rs.getString("email"),
                        rs.getBoolean("sex"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getDate("create_at")
                );

                // 2. Đóng gói Object Course
                Course course = new Course();
                course.setId(rs.getInt("course_id"));
                course.setName(rs.getString("course_name"));
                course.setInstructor(rs.getString("instructor"));

                // 3. Xử lý trạng thái Enum
                String statusStr = rs.getString("status");
                EnrollmentStatus status = EnrollmentStatus.valueOf(statusStr);

                // 4. Đóng gói vào đối tượng Enrollment tổng
                Enrollment enrollment = new Enrollment();
                enrollment.setId(rs.getInt("enrollment_id"));
                enrollment.setStudent(student);
                enrollment.setCourse(course);

                if (rs.getTimestamp("registered_at") != null) {
                    enrollment.setRegisteredAt(rs.getTimestamp("registered_at").toLocalDateTime());
                }
                enrollment.setStatus(status);

                list.add(enrollment);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public boolean approveEnrollment(int studentId, int courseId) {
        boolean flag = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DBUtility.openConnection();
            pstmt = con.prepareStatement("UPDATE enrollment SET status = 'CONFIRMED'::enrollment_status " +
                    "WHERE student_id = ? AND course_id = ? AND status = 'WAITING'::enrollment_status");
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
    public boolean isStudentInCourse(int studentId, int courseId) {
        boolean flag = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBUtility.openConnection();
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM enrollment WHERE student_id = ? AND course_id = ?");
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                flag = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return flag;
    }

    @Override
    public boolean removeStudentFromCourse(int studentId, int courseId) {
        boolean flag = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DBUtility.openConnection();
            pstmt = con.prepareStatement("DELETE FROM enrollment WHERE student_id = ? AND course_id = ?");
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
}
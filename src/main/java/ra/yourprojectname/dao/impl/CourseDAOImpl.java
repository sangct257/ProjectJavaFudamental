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
    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        if (CheckLogin.isAdmin==1){
            Connection con;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            con = DBUtility.openConnection();
            try {
                pstmt = con.prepareStatement("select * from Course order by id asc");
                rs = pstmt.executeQuery();
                while (rs.next()){
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
                DBUtility.closeConnection(rs,pstmt,con);
            }
        } else {
            System.out.println("Tài khoản của bạn không được quyền xem khóa học");
        }

        return list;
    }

    @Override
    public boolean insertCourse(Course course) {
        //check tài khoản đăng nhập có phải là admin hay không?
        if(CheckLogin.isAdmin==1){
            Connection con;
            PreparedStatement pstmt = null;

            con = DBUtility.openConnection();
            try {
                pstmt = con.prepareStatement("INSERT INTO Course(name, duration, instructor) VALUES(?, ?, ?)");
                pstmt.setString(1, course.getName());
                pstmt.setInt(2, course.getDuration());
                pstmt.setString(3, course.getInstructor());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                DBUtility.closeConnection(null,pstmt,con);
            }

        }else{
            System.out.println("Tài khoản của bạn không được quyền thêm khóa học");
        }
        return false;
    }

    @Override
    public boolean updateCourse(Course course) {
        if (CheckLogin.isAdmin==1){
            Connection con;
            PreparedStatement pstmt = null;
            con = DBUtility.openConnection();
            try {
                pstmt = con.prepareStatement("UPDATE Course SET name = ?, duration = ?, instructor = ? WHERE id = ?");
                pstmt.setString(1, course.getName());
                pstmt.setInt(2, course.getDuration());
                pstmt.setString(3, course.getInstructor());
                pstmt.setInt(4, course.getId());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                DBUtility.closeConnection(null,pstmt,con);
            }
        } else {
            System.out.println("Tài khoản của bạn không được quyền sửa khóa học");
        }
        return false;
    }

    @Override
    public boolean deleteCourse(int id) {
        if (CheckLogin.isAdmin==1){
            Connection con;
            PreparedStatement pstmt = null;
            con = DBUtility.openConnection();
            try {
                pstmt = con.prepareStatement("DELETE FROM Course WHERE id = ?");
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                DBUtility.closeConnection(null,pstmt,con);
            }
        } else {
            System.out.println("Tài khoản của bạn không được quyền xoá khóa học");
        }
        return false;
    }

    @Override
    public List<Course> findCourseByName(String name) {
        List<Course> list = new ArrayList<>();
        if (CheckLogin.isAdmin==1){
            Connection con;
            PreparedStatement pstm = null;
            ResultSet rs = null;
            con = DBUtility.openConnection();
            try {
                pstm = con.prepareStatement("SELECT * FROM Course WHERE name ILIKE ?");
                pstm.setString(1, "%" + name + "%");
                rs = pstm.executeQuery();
                while (rs.next()){
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
                DBUtility.closeConnection(rs,pstm,con);
            }
        } else {
            System.out.println("Tài khoản của bạn không được quyền tìm kiếm khóa học");
        }
        return list;
    }

    // Hỗ trợ chức năng sắp xếp linh hoạt theo cột và chiều tăng/giảm trực tiếp dưới Database
    public List<Course> findAllSorted(String orderByColumn, String direction) {
        List<Course> list = new ArrayList<>();
        if (CheckLogin.isAdmin==1){
            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                con = DBUtility.openConnection();
                // Vì tên cột và hướng sắp xếp không truyền qua dấu ? được, ta cần nối chuỗi an toàn bằng lập trình kiểm soát
                pstmt = con.prepareStatement("SELECT * FROM Course ORDER BY " + orderByColumn + " " + direction);
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
        } else {
            System.out.println("Tài khoản của bạn không được quyền tìm kiếm khóa học");
        }
        return list;
    }
}

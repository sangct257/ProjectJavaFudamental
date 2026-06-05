package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.StudentDAO;
import ra.yourprojectname.data_login.CheckLogin;
import ra.yourprojectname.model.Student;
import ra.yourprojectname.until.DBUtility;
import ra.yourprojectname.until.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {
    @Override
    public boolean login(String email, String password) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = DBUtility.openConnection();
            pstmt = con.prepareStatement("select * from Student where email=? and password=?");
            pstmt.setString(1, email);
            String encryptedInputPassword = PasswordHasher.hashPassword(password);
            pstmt.setString(2, encryptedInputPassword);

            rs = pstmt.executeQuery();
            if(rs.next()){
                //..login thành công
                CheckLogin.isAdmin = 2;
                return true;
            }else{
                System.out.println("Sai username hoặc password");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs,pstmt,con);
        }
        return false;
    }

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT * FROM Student ORDER BY id ASC");
            rs = pstmt.executeQuery();
            while (rs.next()){
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("dob"),
                        rs.getString("email"),
                        rs.getBoolean("sex"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getDate("create_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs,pstmt,con);
        }
        return list;
    }

    @Override
    public boolean insertStudent(Student student) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("INSERT INTO Student(name, dob, email, sex, phone, password, create_at) VALUES(?, ?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, student.getName());
            pstmt.setDate(2, new java.sql.Date(student.getDob().getTime()));
            pstmt.setString(3, student.getEmail());
            pstmt.setBoolean(4, student.isSex());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, PasswordHasher.hashPassword(student.getPassword()));
            pstmt.setDate(7, new java.sql.Date(student.getCreate_at().getTime()));
            flag = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(null, pstmt, con);
        }
        return flag;
    }

    @Override
    public boolean updateStudent(Student student) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("UPDATE Student SET name = ?, dob = ?, email = ?, sex = ?, phone = ?, password = ? WHERE id = ?");
            pstmt.setString(1, student.getName());
            pstmt.setDate(2, new java.sql.Date(student.getDob().getTime()));
            pstmt.setString(3, student.getEmail());
            pstmt.setBoolean(4, student.isSex());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getPassword());
            pstmt.setInt(7, student.getId());
            flag = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(null, pstmt, con);
        }
        return flag;
    }

    @Override
    public boolean deleteStudent(int id) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("DELETE FROM Student WHERE id = ?");
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
    public List<Student> findStudentByNameEmailOrId(String keyword) {
        List<Student> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            String sql = "SELECT * FROM Student WHERE name ILIKE ? OR email ILIKE ? ";
            // Kiểm tra xem từ khóa có phải là ID dạng số không
            boolean isNumeric = keyword.matches("-?\\d+");
            if(isNumeric){
                sql += "OR id = ?";
            }
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            if (isNumeric) {
                pstmt.setInt(3, Integer.parseInt(keyword));
            }

            rs = pstmt.executeQuery();
            while (rs.next()){
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("dob"),
                        rs.getString("email"),
                        rs.getBoolean("sex"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getDate("create_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs,pstmt,con);
        }
        return list;
    }

    @Override
    public List<Student> findAllSorted(String column, String direction) {
        List<Student> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT * FROM Student ORDER BY " + column + " " + direction);
            rs = pstmt.executeQuery();
            while (rs.next()){
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("dob"),
                        rs.getString("email"),
                        rs.getBoolean("sex"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getDate("create_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs,pstmt,con);
        }
        return list;
    }

    @Override
    public Student getStudentByEmail(String email) {
        Student student = new Student();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT * FROM student WHERE email = ?");
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            while (rs.next()){
                return new Student(
                        rs.getInt("id"), rs.getString("name"), rs.getDate("dob"),
                        rs.getString("email"), rs.getBoolean("sex"), rs.getString("phone"),
                        rs.getString("password"), rs.getDate("create_at")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs,pstmt,con);
        }
        return student;
    }
}

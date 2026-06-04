package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.StudentDAO;
import ra.yourprojectname.data_login.CheckLogin;
import ra.yourprojectname.model.Student;
import ra.yourprojectname.until.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            pstmt.setString(2, password);

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
    public List<String> findAll() {
        return List.of();
    }

    @Override
    public boolean insertStudent(Student student) {
        return false;
    }

    @Override
    public boolean updateStudent(Student student) {
        return false;
    }

    @Override
    public boolean deleteStudent(int id) {
        return false;
    }

    @Override
    public Student findStudentById(int id) {
        return null;
    }

    @Override
    public Student findStudentByEmail(String email) {
        return null;
    }
}

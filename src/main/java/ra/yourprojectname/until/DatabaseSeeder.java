package ra.yourprojectname.until;

import java.sql.*;

public class DatabaseSeeder {
    public static void seedData() {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Admin");
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                pstmt.close();
                pstmt = con.prepareStatement("INSERT INTO Admin (username, password) VALUES (?, ?)");
                pstmt.setString(1, "admin");
                pstmt.setString(2, PasswordHasher.hashPassword("123456"));
                pstmt.executeUpdate();
                System.out.println("[Database Seed]: Khởi tạo tài khoản Admin mặc định thành công! (Tài khoản: admin / Pass: 123456)");
            }
            if (rs != null) rs.close();

            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Student");
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                pstmt.close();
                pstmt = con.prepareStatement( "INSERT INTO Student (name, dob, email, sex, phone, password) VALUES (?, ?, ?, ?, ?, ?)");
                pstmt.setString(1, "Nguyễn Văn A");
                pstmt.setDate(2, Date.valueOf("2002-05-15"));
                pstmt.setString(3, "hocvien@gmail.com");
                pstmt.setBoolean(4, true);
                pstmt.setString(5, "0987654321");
                pstmt.setString(6, PasswordHasher.hashPassword("123456"));
                pstmt.executeUpdate();
                System.out.println("[Database Seed]: Khởi tạo tài khoản Học viên mẫu thành công! (Email: hocvien@gmail.com / Pass: 123456)");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi tự động khởi tạo dữ liệu: " + e.getMessage());
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
    }
}

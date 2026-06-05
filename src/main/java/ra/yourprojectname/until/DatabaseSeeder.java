package ra.yourprojectname.until;

import java.sql.*;

public class DatabaseSeeder {
    public static void seedData() {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = DBUtility.openConnection();

            // 1. KHỞI TẠO TÀI KHOẢN ADMIN MẶC ĐỊNH (Nếu chưa có tài khoản nào)
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Admin");
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Đóng pstmt cũ để tạo câu lệnh insert
                pstmt.close();

                pstmt = con.prepareStatement("INSERT INTO Admin (username, password) VALUES (?, ?)");
                pstmt.setString(1, "admin");

                // MẬT KHẨU "123456" SẼ ĐƯỢC MÃ HÓA TRƯỚC KHI LƯU
                String encryptedAdminPass = PasswordHasher.hashPassword("123456");
                pstmt.setString(2, encryptedAdminPass);

                pstmt.executeUpdate();
                System.out.println("[Database Seed]: Khởi tạo tài khoản Admin mặc định thành công! (Tài khoản: admin / Pass: 123456)");
            }
            if (rs != null) rs.close();

            // 2. KHỞI TẠO TÀI KHOẢN HỌC VIÊN MẪU (Nếu bảng Student trống)
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Student");
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                pstmt.close();

                String sqlStudent = "INSERT INTO Student (name, dob, email, sex, phone, password) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt = con.prepareStatement(sqlStudent);
                pstmt.setString(1, "Nguyễn Văn A");
                pstmt.setDate(2, Date.valueOf("2002-05-15"));
                pstmt.setString(3, "hocvien@gmail.com");
                pstmt.setBoolean(4, true);
                pstmt.setString(5, "0987654321");

                // 🔑 MẬT KHẨU "student123" SẼ ĐƯỢC MÃ HÓA TRƯỚC KHI LƯU
                String encryptedStudentPass = PasswordHasher.hashPassword("123456");
                pstmt.setString(6, encryptedStudentPass);

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

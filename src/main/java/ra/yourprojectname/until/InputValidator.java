package ra.yourprojectname.until;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class InputValidator {
    private static final Scanner scanner = new Scanner(System.in);

    // Kiểm tra định dạng Email chuẩn toàn cầu
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    // Kiểm tra số điện thoại (Chấp nhận từ 9-11 số)
    private static final String PHONE_REGEX = "^\\d{9,11}$";

    // --- HÀM VALIDATE CHUỖI NHẬP TRỐNG ---
    public static String inputNonEmptyString(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Lỗi ràng buộc: Trường này bắt buộc nhập, không được bỏ trống!");
            } else {
                return input;
            }
        }
    }

    // --- HÀM VALIDATE EMAIL (ĐÚNG ĐỊNH DẠNG & KHÔNG TRÙNG LẶP TRONG DB) ---
    public static String inputUniqueEmail() {
        while (true) {
            System.out.print("Nhập địa chỉ Email học viên: ");
            String email = scanner.nextLine().trim();

            if (!email.matches(EMAIL_REGEX)) {
                System.out.println("Lỗi ràng buộc: Định dạng Email không hợp lệ! (Ví dụ chuẩn: example@gmail.com)");
                continue;
            }

            // Kiểm tra trùng lặp (UNIQUE) trong cơ sở dữ liệu PostgreSQL
            if (isEmailAlreadyExist(email)) {
                System.out.println("Lỗi ràng buộc: Email [" + email + "] đã được đăng ký bởi học viên khác!");
                continue;
            }
            return email;
        }
    }

    // --- HÀM VALIDATE TÊN ĐĂNG NHẬP ADMIN (UNIQUE) ---
    public static String inputUniqueAdminUsername() {
        while (true) {
            String username = inputNonEmptyString("Nhập tên tài khoản quản trị (Username): ");
            if (isAdminUsernameExist(username)) {
                System.out.println("Lỗi ràng buộc: Tên đăng nhập này đã tồn tại trên hệ thống!");
                continue;
            }
            return username;
        }
    }

    // --- HÀM VALIDATE SỐ ĐIỆN THOẠI (CÓ THỂ NULL HOẶC ĐÚNG ĐỊNH DẠNG SỐ) ---
    public static String inputNullablePhone() {
        while (true) {
            System.out.print("Nhập số điện thoại (Ấn Enter nếu muốn bỏ trống): ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty()) {
                return null; // Trả về null vì thiết kế database cho phép NULLABLE
            }
            if (!phone.matches(PHONE_REGEX)) {
                System.out.println("Lỗi ràng buộc: Số điện thoại phải từ 9 đến 11 ký tự số!");
                continue;
            }
            return phone;
        }
    }

    // --- HÀM VALIDATE MẬT KHẨU AN TOÀN (TỐI THIỂU 6 KÝ TỰ) ---
    public static String inputSecurePassword() {
        while (true) {
            System.out.print("Nhập mật khẩu (Tối thiểu 6 ký tự): ");
            String password = scanner.nextLine().trim();
            if (password.length() < 6) {
                System.out.println("Lỗi ràng buộc: Mật khẩu quá ngắn! Phải bảo mật với độ dài ít nhất 6 ký tự.");
                continue;
            }
            return password;
        }
    }

    // --- HÀM VALIDATE ĐỊNH DẠNG NGÀY SINH (DATE) ---
    public static java.util.Date inputDateOfBirth() {
        while (true) {
            System.out.print("Nhập ngày sinh học viên (Định dạng chuẩn YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();
            try {
                return java.sql.Date.valueOf(dateStr); // Chuyển chuỗi YYYY-MM-DD thành kiểu Date của SQL luôn
            } catch (IllegalArgumentException e) {
                System.out.println("Lỗi ràng buộc: Ngày sinh nhập sai định dạng năm-tháng-ngày! Hoặc ngày nhập không có thực.");
            }
        }
    }

    // --- HÀM VALIDATE SỐ NGUYÊN DƯƠNG (DÙNG CHO THỜI LƯỢNG KHÓA HỌC) ---
    public static int inputPositiveInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value <= 0) {
                    System.out.println("Lỗi ràng buộc: Giá trị nhập vào phải là một số nguyên dương > 0!");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Định dạng nhập vào bắt buộc phải là số nguyên!");
            }
        }
    }

    // --- HÀM VALIDATE CHUYỂN PHÍM BẤM SANG TRẠNG THÁI GIỚI TÍNH (BOOLEAN) ---
    public static boolean inputSex() {
        while (true) {
            System.out.print("Chọn giới tính (1: Nam, 0: Nữ): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) return true;
            if (input.equals("0")) return false;
            System.out.println("Lỗi: Chỉ chấp nhận nhập phím số 1 (Nam) hoặc 0 (Nữ)!");
        }
    }

    private static boolean isEmailAlreadyExist(String email) {
        boolean exist = false;
        Connection con = DBUtility.openConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Student WHERE email = ?");
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                exist = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return exist;
    }

    private static boolean isAdminUsernameExist(String username) {
        boolean exist = false;
        Connection con = DBUtility.openConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Admin WHERE username = ?");
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                exist = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return exist;
    }
}

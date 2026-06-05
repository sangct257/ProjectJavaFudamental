package ra.yourprojectname.until;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    /**
     * Hàm băm mật khẩu thô thành chuỗi mã hóa SHA-256
     * @param password Mật khẩu thô (Ví dụ: "123456")
     * @return Chuỗi đã mã hóa dài 64 ký tự
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Chuyển mảng byte sang dạng Chuỗi Hex (Hexadecimal string)
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi cấu hình thuật toán mã hóa: " + e.getMessage());
        }
    }
}

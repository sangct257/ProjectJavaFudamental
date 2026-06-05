package ra.yourprojectname.presentation.student;

import ra.yourprojectname.business.EnrollmentService;
import ra.yourprojectname.business.StudentService;
import ra.yourprojectname.business.impl.EnrollmentServiceImpl;
import ra.yourprojectname.business.impl.StudentServiceImpl;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Student;
import ra.yourprojectname.until.PasswordHasher;

import java.util.List;
import java.util.Scanner;

public class StudentView {
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final StudentService studentService = new StudentServiceImpl();
    private Student currentStudent;

    public StudentView(Scanner scanner,String loginEmail) {

        this.currentStudent = enrollmentService.getStudentByEmail(loginEmail);

        if (this.currentStudent == null) {
            System.err.println("Lỗi hệ thống: Không tìm thấy thông tin tài khoản học viên!");
            return;
        }

        int choose = 0;
        boolean flag = true;
        while (flag) {
            System.out.println("============= MENU STUDENT ==========");
            System.out.println("Chào mừng học viên: " + currentStudent.getName());
            System.out.println("1. Xem danh sách khoá học");
            System.out.println("2. Đăng ký khoá học");
            System.out.println("3. Xem khoá học đã đăng ký");
            System.out.println("4. Huỷ đăng ký (nếu chưa bắt đầu)");
            System.out.println("5. Dổi mật khẩu");
            System.out.println("6. Đăng xuất");
            System.out.println("===================================");
            while (true){
                try {
                    System.out.println("Mời chọn: ");
                    choose = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Bạn phải nhập là số");
                }
            }

            switch (choose) {
                case 1:
                    displayAllCourses();
                    break;
                case 2:
                    enrollCourse(scanner);
                    break;
                case 3:
                    displayEnrolledCourses(scanner);
                    break;
                case 4:
                    cancelEnrollmentCourse(scanner);
                    break;
                case 5:
                    changePassword(scanner);
                    break;
                case 6:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 6");
            }
        }
    }

    // XEM DANH SÁCH KHÓA HỌC ĐANG CÓ ---
    public void displayAllCourses(){
        List<Course> courses = enrollmentService.getAllCourses();
        System.out.println("=== DANH SÁCH KHÓA HỌC HIỆN CÓ PHÂN HỆ ===");
        printCourseTable(courses);
    }

    // ĐĂNG KÝ KHÓA HỌC ---
    public void enrollCourse(Scanner scanner){
        displayAllCourses();
        System.out.print("Nhập mã số ID khóa học bạn muốn đăng ký: ");
        try {
            int courseId = Integer.parseInt(scanner.nextLine().trim());
            if (enrollmentService.enrollCourse(currentStudent.getId(), courseId)) {
                System.out.println("Đăng ký học thành công! Vui lòng chờ Admin phê duyệt.");
            } else {
                System.err.println("Đăng ký khóa học thất bại.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Lỗi: Mã ID khóa học phải là một số nguyên!");
        }
    }

    // XEM KHÓA HỌC ĐÃ ĐĂNG KÝ + SẮP XẾP ---
    public void displayEnrolledCourses(Scanner scanner) {
        // Đầu tiên, lấy danh sách mặc định (sắp xếp tăng dần theo ID/Thứ tự đăng ký)
        List<Course> enrolled = enrollmentService.getCoursesByStudentId(currentStudent.getId());

        if (enrolled == null || enrolled.isEmpty()) {
            System.out.println("Bạn chưa đăng ký tham gia khóa học nào.");
            return;
        }

        System.out.println("=== CÁC KHÓA HỌC BẠN ĐÃ ĐĂNG KÝ ===");
        printCourseTable(enrolled);

        // Hiển thị lựa chọn sắp xếp nâng cao theo yêu cầu đề bài
        System.out.print("Bạn có muốn sắp xếp lại danh sách này không? (Y: Có / Phím khác: Bỏ qua): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            String column = "id";
            String direction = "ASC";

            // 1. Chọn tiêu chí sắp xếp
            while (true) {
                System.out.println("Sắp xếp theo: 1. Tên khóa học | 2. Ngày đăng ký");
                System.out.print("Lựa chọn của bạn (1-2): ");
                String choiceCol = scanner.nextLine().trim();
                if (choiceCol.equals("1")) {
                    column = "name";
                    break;
                } else if (choiceCol.equals("2")) {
                    column = "date";
                    break;
                } else {
                    System.err.println("Lựa chọn không hợp lệ. Vui lòng chọn lại!");
                }
            }

            // 2. Chọn chiều sắp xếp
            while (true) {
                System.out.println("Chiều sắp xếp: 1. Tăng dần (A-Z) | 2. Giảm dần (Z-A)");
                System.out.print("Lựa chọn của bạn (1-2): ");
                String choiceDir = scanner.nextLine().trim();
                if (choiceDir.equals("1")) {
                    direction = "ASC";
                    break;
                } else if (choiceDir.equals("2")) {
                    direction = "DESC";
                    break;
                } else {
                    System.err.println("Lựa chọn không hợp lệ. Vui lòng chọn lại!");
                }
            }

            List<Course> sortedList = enrollmentService.getCoursesByStudentIdSorted(currentStudent.getId(), column, direction);
            System.out.println("=== KẾT QUẢ DANH SÁCH SAU KHI SẮP XẾP ===");
            printCourseTable(sortedList);
        }
    }

    //HỦY ĐĂNG KÝ KHÓA HỌC ---
    public void cancelEnrollmentCourse(Scanner scanner) {
        List<Course> enrolled = enrollmentService.getCoursesByStudentId(currentStudent.getId());

        if (enrolled == null || enrolled.isEmpty()) {
            System.out.println("📭 Bạn không có khóa học nào đang ở trạng thái chờ duyệt để hủy.");
            return;
        }

        System.out.println("=== DANH SÁCH KHÓA HỌC CÓ THỂ HỦY ===");
        printCourseTable(enrolled);

        System.out.print("Nhập mã số ID khóa học bạn muốn hủy đăng ký: ");
        try {
            int courseId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Bạn chắc chắn muốn hủy đăng ký khóa học này? (Gõ 'Y' để xác nhận, phím khác để hủy bỏ): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                if (enrollmentService.cancelEnrolledCourse(currentStudent.getId(), courseId)) {
                    System.out.println("Hủy đăng ký khóa học thành công!");
                } else {
                    System.err.println("Không thể hủy! Lý do: Khóa học không tồn tại hoặc đã được Ban quản lý phê duyệt học chính thức.");
                }
            } else {
                System.out.println("Thao tác hủy bỏ được dừng lại.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Lỗi: Mã số ID nhập vào phải ở dạng số nguyên!");
        }
    }
    // ĐỔI MẬT KHẨU (XÁC THỰC MẬT KHẨU CŨ) ---
    private void changePassword(Scanner scanner) {
        System.out.println("--- ĐỔI MẬT KHẨU TÀI KHOẢN ---");
        System.out.print("Nhập mật khẩu cũ hiện tại của bạn: ");
        String oldPass = scanner.nextLine().trim();

        String hashedOldPass = PasswordHasher.hashPassword(oldPass);
        if (!hashedOldPass.equals(currentStudent.getPassword())) {
            System.err.println("Lỗi: Mật khẩu cũ không chính xác!");
            return;
        }

        String newPass;
        while (true) {
            System.out.print("Nhập thiết lập mật khẩu MỚI: ");
            newPass = scanner.nextLine().trim();
            if (newPass.isEmpty()) {
                System.err.println("Lỗi: Mật khẩu mới không được để trống!");
                continue;
            }
            break;
        }

        String hashedNewPass = PasswordHasher.hashPassword(newPass);
        currentStudent.setPassword(hashedNewPass);

        if (studentService.updateStudent(currentStudent)) {
            System.out.println("Bạn đã đổi mật khẩu tài khoản thành công!");
        } else {
            System.err.println("Lỗi hệ thống: Không thể cập nhật mật khẩu mới.");
        }
    }

    private void printCourseTable(List<Course> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Không có dữ liệu khóa học nào trong danh sách.");
            return;
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+");
        System.out.printf("| %-4s | %-28s | %-13s | %-23s |\n", "ID", "TÊN KHÓA HỌC", "THỜI LƯỢNG (H)", "GIẢNG VIÊN HƯỚNG DẪN");
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+");
        for (Course c : list) {
            System.out.printf("| %-4d | %-28s | %-13d | %-23s |\n", c.getId(), c.getName(), c.getDuration(), c.getInstructor());
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+");
    }

}

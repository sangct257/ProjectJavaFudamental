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
    private final Student currentStudent;
    private final int pageSize = 3;

    public StudentView(Scanner scanner, String loginEmail) {
        this.currentStudent = enrollmentService.getStudentByEmail(loginEmail);

        if (this.currentStudent == null) {
            System.err.println("Lỗi hệ thống: Không tìm thấy thông tin tài khoản học viên!");
            return;
        }

        while (true) {
            System.out.println("\n============= MENU STUDENT ==========");
            System.out.println("Chào mừng học viên: " + currentStudent.getName());
            System.out.println("1. Xem danh sách \n2. Đăng ký khoá học\n3. Xem khoá học đã đăng ký\n4. Huỷ đăng ký khoá học (nếu chưa bắt đầu)\n5. Đổi mật khẩu\n6. Đăng xuất");
            System.out.println("===================================");
            int choose = inputInt(scanner, "Mới chọn: ");

            switch (choose) {
                case 1: displayAllCourses(scanner); break;
                case 2: enrollCourse(scanner); break;
                case 3: displayEnrolledCourses(scanner); break;
                case 4: cancelEnrollmentCourse(scanner); break;
                case 5: changePassword(scanner); break;
                case 6: return;
                default: System.out.println("Bạn chỉ được nhập từ 1 đến 6!");
            }
        }
    }

    // --- PHÂN TRANG ---
    private int handlePagination(Scanner scanner, int currentPage, int totalPages) {
        if (totalPages <= 1) return -1;
        System.out.print("[P]: Trang trước  |  [N]: Trang kế  |  [E]: Thoát. Chọn hành động: ");
        String action = scanner.nextLine().trim().toUpperCase();

        if (action.equals("N") && currentPage < totalPages) return currentPage + 1;
        if (action.equals("P") && currentPage > 1) return currentPage - 1;
        if (action.equals("E")) return -1;

        System.err.println("Lệnh điều hướng không phù hợp!");
        return currentPage;
    }

    // Xem toàn bộ danh sách khóa học
    public void displayAllCourses(Scanner scanner) {
        int currentPage = 1;
        String keyword = "";

        while (true) {
            int totalPages;
            List<Course> coursesPage;

            // THUẬT TOÁN ĐỀ XUẤT: CHỈ HIỂN THỊ Ở TRANG 1
            if (currentPage == 1 && keyword.isEmpty()) {
                // Gọi tầng Service quét các khóa đã học để lấy ra tối đa 2 khóa đề xuất bổ trợ phù hợp
                List<Course> recommendations = enrollmentService.getRecommendedCoursesByEnrolled(currentStudent.getId(), 2);

                if (recommendations != null && !recommendations.isEmpty()) {
                    System.out.println("\nKHÓA HỌC ĐỀ XUẤT DÀNH RIÊNG CHO BẠN");
                    System.out.println("(Hệ thống gợi ý dựa trên các chủ đề khóa học bạn đã đăng ký tham gia)");
                    printCourseTable(recommendations);
                    System.out.println("----------------------------------------------------------------------------------");
                }
            }

            if (keyword.isEmpty()) {
                totalPages = enrollmentService.getTotalPages(pageSize);
                coursesPage = enrollmentService.getAllCourses(currentPage, pageSize);
                System.out.printf("\n=== DANH SÁCH KHÓA HỌC HIỆN CÓ (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            } else {
                totalPages = enrollmentService.getSearchCoursesTotalPages(keyword, pageSize);
                coursesPage = enrollmentService.searchCourses(keyword, currentPage, pageSize);
                System.out.printf("\n=== KẾT QUẢ TÌM KIẾM [Từ khóa: '%s'] (TRANG %d / %d) ===\n", keyword, currentPage, totalPages == 0 ? 1 : totalPages);
            }

            printCourseTable(coursesPage);

            System.out.print("[P]: Trang trước  |  [N]: Trang kế  |  [F]: Tìm kiếm theo tên  |  [E]: Thoát: ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) {
                break;
            }

            if (action.equals("N")) {
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Bạn đang ở trang cuối cùng.");
                }
                continue;
            }

            if (action.equals("P")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Bạn đang ở trang đầu tiên.");
                }
                continue;
            }

            if (action.equals("F")) {
                System.out.print("Nhập từ khóa tên khóa học muốn tìm (Để trống để quay lại xem tất cả): ");
                keyword = scanner.nextLine().trim();
                currentPage = 1;
                continue;
            }

            System.err.println("Lệnh không phù hợp!");
        }
    }

    // ---ĐĂNG KÝ KHÓA HỌC---
    public void enrollCourse(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalPages = enrollmentService.getTotalPages(pageSize);
            List<Course> coursesPage = enrollmentService.getAllCourses(currentPage, pageSize);

            System.out.printf("\n=== CHỌN KHÓA HỌC BẠN MUỐN ĐĂNG KÝ (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTable(coursesPage);

            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [Mã ID]: Nhập ID để đăng ký  |  [E]: Thoát: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("E")) break;
            if (input.equals("N")) {
                currentPage = (currentPage < totalPages) ? currentPage + 1 : currentPage;
                if (currentPage == totalPages) System.out.println("Bạn đang ở trang cuối cùng.");
                continue;
            }
            if (input.equals("P")) {
                currentPage = (currentPage > 1) ? currentPage - 1 : currentPage;
                if (currentPage == 1) System.out.println("Bạn đang ở trang đầu tiên.");
                continue;
            }

            try {
                int courseId = Integer.parseInt(input);
                if (enrollmentService.enrollCourse(currentStudent.getId(), courseId)) {
                    System.out.println("Đăng ký học thành công! Vui lòng chờ Ban quản lý phê duyệt.");
                    break;
                }
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Vui lòng nhập đúng mã ID số nguyên hoặc lệnh (N/P/E)!");
            }
        }
    }

    // ---XEM KHÓA HỌC ĐÃ ĐĂNG KÝ---
    public void displayEnrolledCourses(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalPages = enrollmentService.getStudentCoursesTotalPages(currentStudent.getId(), pageSize);
            List<Course> enrolledPage = enrollmentService.getCoursesByStudentId(currentStudent.getId(), currentPage, pageSize);

            if (enrolledPage.isEmpty() && currentPage == 1) {
                System.out.println("Bạn chưa đăng ký tham gia khóa học nào.");
                return;
            }

            System.out.printf("\n=== CÁC KHÓA HỌC BẠN ĐÃ ĐĂNG KÝ (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTable(enrolledPage);

            System.out.print("\n[N]: Trang kế  |  [P]: Trang trước  |  [S]: Sắp xếp lại  |  [E]: Thoát: ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) return;
            if (action.equals("N") && currentPage < totalPages) currentPage++;
            else if (action.equals("P") && currentPage > 1) currentPage--;
            else if (action.equals("S")) {
                configAndShowSortedCourses(scanner);
                return;
            } else {
                System.err.println("Lệnh không hợp lệ hoặc đã hết trang!");
            }
        }
    }

    private void configAndShowSortedCourses(Scanner scanner) {
        System.out.println("\nSắp xếp theo: 1. Tên khóa học | 2. Thời lượng (h)");
        String column = inputInt(scanner, "Lựa chọn (1-2): ") == 2 ? "duration" : "name";

        System.out.println("Chiều sắp xếp: 1. Tăng dần (A-Z) | 2. Giảm dần (Z-A)");
        String direction = inputInt(scanner, "Lựa chọn (1-2): ") == 2 ? "DESC" : "ASC";

        int currentPage = 1;
        while (true) {
            int totalPages = enrollmentService.getStudentCoursesTotalPages(currentStudent.getId(), pageSize);
            List<Course> sortedPage = enrollmentService.getCoursesByStudentIdSorted(currentStudent.getId(), column, direction, currentPage, pageSize);

            System.out.printf("\n=== DANH SÁCH SAU KHI SẮP XẾP (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTable(sortedPage);

            currentPage = handlePagination(scanner, currentPage, totalPages);
            if (currentPage == -1) break;
        }
    }

    // --- HỦY ĐĂNG KÝ KHÓA HỌC ---
    public void cancelEnrollmentCourse(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalPages = enrollmentService.getStudentCoursesTotalPages(currentStudent.getId(), pageSize);
            List<Course> enrolledPage = enrollmentService.getCoursesByStudentId(currentStudent.getId(), currentPage, pageSize);

            if (enrolledPage.isEmpty() && currentPage == 1) {
                System.out.println("Bạn không có khóa học nào để thực hiện hủy.");
                return;
            }

            System.out.printf("\n=== DANH SÁCH KHÓA HỌC CÓ THỂ HỦY (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTable(enrolledPage);

            System.out.print("\n[N]: Trang kế  |  [P]: Trang trước  |  [Mã ID]: Nhập ID để HỦY  |  [E]: Thoát: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("E")) break;
            if (input.equals("N")) {
                currentPage = (currentPage < totalPages) ? currentPage + 1 : currentPage;
                continue;
            }
            if (input.equals("P")) {
                currentPage = (currentPage > 1) ? currentPage - 1 : currentPage;
                continue;
            }

            try {
                int courseId = Integer.parseInt(input);
                System.out.print("Bạn chắc chắn muốn hủy đăng ký khóa học này? (Gõ 'Y' để xác nhận): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                    if (enrollmentService.cancelEnrolledCourse(currentStudent.getId(), courseId)) {
                        System.out.println("Hủy đăng ký khóa học thành công!");
                        break;
                    } else {
                        System.err.println("Hủy thất bại! Khóa học đã được duyệt hoặc không tồn tại.");
                    }
                } else {
                    System.out.println("Thao tác hủy được dừng lại.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Vui lòng nhập đúng mã ID số nguyên hoặc lệnh chức năng!");
            }
        }
    }

    // ---ĐỔI MẬT KHẨU TÀI KHOẢN---
    private void changePassword(Scanner scanner) {
        System.out.println("\n--- ĐỔI MẬT KHẨU TÀI KHOẢN ---");
        System.out.print("Nhập mật khẩu cũ hiện tại: ");
        String oldPass = scanner.nextLine().trim();

        if (!PasswordHasher.hashPassword(oldPass).equals(currentStudent.getPassword())) {
            System.err.println("Lỗi: Mật khẩu cũ không chính xác!");
            return;
        }

        System.out.print("Nhập thiết lập mật khẩu MỚI: ");
        String newPass = scanner.nextLine().trim();
        if (newPass.isEmpty()) {
            System.err.println("Lỗi: Mật khẩu mới không được để trống!");
            return;
        }

        currentStudent.setPassword(PasswordHasher.hashPassword(newPass));
        if (studentService.updateStudent(currentStudent)) {
            System.out.println("Bạn đã đổi mật khẩu tài khoản thành công!");
        } else {
            System.err.println("Lỗi hệ thống: Không thể cập nhật mật khẩu mới.");
        }
    }

    // ---HÀM ÉP KIỂU SỐ NGUYÊN DÙNG CHUNG---
    private int inputInt(Scanner scanner, String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Bạn phải nhập vào là số nguyên!");
            }
        }
    }

    // ---TỰ ĐỘNG ĐÁNH DẤU TRẠNG THÁI [ĐÃ ĐĂNG KÝ]---
    private void printCourseTable(List<Course> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Không có dữ liệu khóa học nào trong trang này.");
            return;
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(49) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(25) + "+");
        System.out.printf("| %-4s | %-47s | %-13s | %-23s | %-23s\n", "ID", "TÊN KHÓA HỌC", "THỜI LƯỢNG (H)", "GIẢNG VIÊN HƯỚNG DẪN","NGÀY ĐĂNG KÝ");
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(49) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+"  + "-".repeat(25) + "+");

        for (Course c : list) {
            String courseNameDisplay = c.getName();
            if (enrollmentService.isCourseEnrolledByStudent(currentStudent.getId(), c.getId())) {
                courseNameDisplay += " [ĐÃ ĐĂNG KÝ]";
            }
            System.out.printf("| %-4d | %-47s | %-13d | %-23s | %-23s |\n", c.getId(), courseNameDisplay, c.getDuration(), c.getInstructor(),c.getCreate_at());
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(49) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(25) + "+");
    }
}
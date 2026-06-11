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

        String errorMsg = "";
        while (true) {
            System.out.println("\n============= MENU STUDENT ==========");
            System.out.println("Chào mừng học viên: " + currentStudent.getName());
            System.out.println("""
            1. Xem danh sách khóa học hiện có
            2. Đăng ký khoá học mới
            3. Xem khoá học đã đăng ký
            4. Huỷ đăng ký khoá học (nếu chưa bắt đầu)
            5. Đổi mật khẩu tài khoản
            6. Đăng xuất """);
            System.out.println("===================================");

            int choose;
            while (true) {
                if (!errorMsg.isEmpty()) {
                    System.out.print(errorMsg);
                    errorMsg = "";
                }
                System.out.print("Mời chọn (1-6): ");
                try {
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (NumberFormatException e) {
                    errorMsg = "Lỗi: Bạn phải nhập vào là số nguyên!\n";
                }
            }

            switch (choose) {
                case 1: displayAllCourses(scanner); break;
                case 2: enrollCourse(scanner); break;
                case 3: displayEnrolledCourses(scanner); break;
                case 4: cancelEnrollmentCourse(scanner); break;
                case 5: changePassword(scanner); break;
                case 6:
                    System.out.println("Đã đăng xuất tài khoản Học viên thành công.");
                    return;
                default:
                    errorMsg = "Lỗi: Bạn chỉ được nhập lựa chọn từ số 1 đến số 6!\n";
            }
        }
    }

    // --- HÀM HỖ TRỢ PHÂN TRANG ĐÃ ĐỒNG BỘ LỖI ---
    private int handlePagination(Scanner scanner, int currentPage, int totalPages) {
        if (totalPages <= 1) return -1;
        String errorMsg = "";
        while (true) {
            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }
            System.out.print("[P]: Trang trước | [N]: Trang kế | [E]: Thoát. Chọn hành động: ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("N") && currentPage < totalPages) return currentPage + 1;
            if (action.equals("N") && currentPage >= totalPages) {
                errorMsg = "Lỗi: Đã ở trang cuối cùng, không thể đi tiếp!\n";
                continue;
            }
            if (action.equals("P") && currentPage > 1) return currentPage - 1;
            if (action.equals("P") && currentPage <= 1) {
                errorMsg = "Lỗi: Đã ở trang đầu tiên, không thể quay lại!\n";
                continue;
            }
            if (action.equals("E")) return -1;

            errorMsg = "Lỗi: Lệnh điều hướng không hợp lệ (Chỉ gõ P, N hoặc E)!\n";
        }
    }

    // --- 1. XEM TOÀN BỘ DANH SÁCH KHÓA HỌC (PHÂN TRANG & TÌM KIẾM & ĐỀ XUẤT) ---
    public void displayAllCourses(Scanner scanner) {
        int currentPage = 1;
        String keyword = "";
        String errorMsg = "";

        while (true) {
            int totalPages;
            List<Course> coursesPage;

            // THUẬT TOÁN ĐỀ XUẤT: CHỈ HIỂN THỊ Ở TRANG 1 KHI CHƯA TÌM KIẾM
            if (currentPage == 1 && keyword.isEmpty()) {
                List<Course> recommendations = enrollmentService.getRecommendedCoursesByEnrolled(currentStudent.getId(), 2);
                if (recommendations != null && !recommendations.isEmpty()) {
                    System.out.println("\nKHÓA HỌC ĐỀ XUẤT DÀNH RIÊNG CHO BẠN:");
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
                coursesPage = enrollmentService.searchCoursesByName(keyword, currentPage, pageSize);
                System.out.printf("\n=== KẾT QUẢ TÌM KIẾM [Từ khóa: '%s'] (TRANG %d / %d) ===\n", keyword, currentPage, totalPages == 0 ? 1 : totalPages);
            }

            printCourseTable(coursesPage);

            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }

            System.out.print("[P]: Trang trước | [N]: Trang kế | [F]: Tìm theo tên | [E]: Thoát: ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) break;

            if (action.equals("N")) {
                if (currentPage < totalPages) currentPage++;
                else errorMsg = "Lỗi: Bạn đang ở trang cuối cùng.\n";
                continue;
            }
            if (action.equals("P")) {
                if (currentPage > 1) currentPage--;
                else errorMsg = "Lỗi: Bạn đang ở trang đầu tiên.\n";
                continue;
            }
            if (action.equals("F")) {
                System.out.print("Nhập từ khóa tên khóa học muốn tìm (Để trống để quay lại xem tất cả): ");
                keyword = scanner.nextLine().trim();
                currentPage = 1;
                continue;
            }
            errorMsg = "Lỗi: Lệnh thao tác điều hướng không phù hợp!\n";
        }
    }

    // --- 2. ĐĂNG KÝ KHÓA HỌC ---
    public void enrollCourse(Scanner scanner) {
        int currentPage = 1;
        String errorMsg = "";

        while (true) {
            int totalPages = enrollmentService.getTotalPages(pageSize);
            List<Course> coursesPage = enrollmentService.getAllCourses(currentPage, pageSize);

            System.out.printf("\n=== CHỌN KHÓA HỌC BẠN MUỐN ĐĂNG KÝ (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTable(coursesPage);

            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }

            System.out.print("\n[P]: Trang trước | [N]: Trang kế | [Mã ID]: Nhập ID để đăng ký | [E]: Thoát: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("E")) break;
            if (input.equals("N")) {
                if (currentPage < totalPages) currentPage++;
                else errorMsg = "Lỗi: Đã ở trang cuối cùng của danh sách.\n";
                continue;
            }
            if (input.equals("P")) {
                if (currentPage > 1) currentPage--;
                else errorMsg = "Lỗi: Đã ở trang đầu tiên của danh sách.\n";
                continue;
            }

            try {
                int courseId = Integer.parseInt(input);
                if (enrollmentService.enrollCourse(currentStudent.getId(), courseId)) {
                    System.out.println("Đăng ký học thành công! Vui lòng chờ Ban quản lý phê duyệt trạng thái.");
                    break;
                } else {
                    errorMsg = "Lỗi: Đăng ký thất bại. Bạn có thể đã đăng ký hoặc lớp học không khả dụng!\n";
                }
            } catch (NumberFormatException e) {
                errorMsg = "Lỗi: Vui lòng nhập đúng mã ID số nguyên hoặc bộ lệnh điều hướng (N/P/E)!\n";
            }
        }
    }

    // --- 3. XEM KHÓA HỌC ĐÃ ĐĂNG KÝ (HỖ TRỢ TRUYỂN TAB SẮP XẾP) ---
    public void displayEnrolledCourses(Scanner scanner) {
        int currentPage = 1;
        String errorMsg = "";

        while (true) {
            int totalPages = enrollmentService.getStudentCoursesTotalPages(currentStudent.getId(), pageSize);
            List<Course> enrolledPage = enrollmentService.getCoursesByStudentId(currentStudent.getId(), currentPage, pageSize);

            if ((enrolledPage == null || enrolledPage.isEmpty()) && currentPage == 1) {
                System.out.println("Bạn chưa thực hiện đăng ký tham gia bất kỳ khóa học nào.");
                return;
            }

            System.out.printf("\n=== CÁC KHÓA HỌC BẠN ĐÃ ĐĂNG KÝ (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTable(enrolledPage);

            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }

            System.out.print("\n[N]: Trang kế | [P]: Trang trước | [S]: Sắp xếp lại lớp | [E]: Thoát: ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) return;
            if (action.equals("N")) {
                if (currentPage < totalPages) currentPage++;
                else errorMsg = "Lỗi: Đã ở trang cuối cùng.\n";
                continue;
            }
            if (action.equals("P")) {
                if (currentPage > 1) currentPage--;
                else errorMsg = "Lỗi: Đã ở trang đầu tiên.\n";
                continue;
            }
            if (action.equals("S")) {
                configAndShowSortedCourses(scanner);
                return;
            }
            errorMsg = "Lỗi: Tổ hợp lệnh nhập vào không hợp lệ hoặc đã hết trang điều hướng!\n";
        }
    }

    // --- PHẦN CHỨC NĂNG PHỤ: SẮP XẾP KHÓA HỌC ĐÃ ĐĂNG KÝ ---
    private void configAndShowSortedCourses(Scanner scanner) {
        String subError = "";
        int colChoice;
        while (true) {
            if (!subError.isEmpty()) {
                System.out.print(subError);
                subError = "";
            }
            System.out.println("\nSắp xếp theo: 1. Tên khóa học | 2. Ngày tạo khóa học");
            colChoice = inputInt(scanner, "Lựa chọn của bạn (1-2): ");
            if (colChoice == 1 || colChoice == 2) break;
            subError = "Lỗi: Chỉ được chọn số 1 hoặc số 2!\n";
        }
        String column = (colChoice == 2) ? "create_at" : "name";

        int dirChoice;
        while (true) {
            if (!subError.isEmpty()) {
                System.out.print(subError);
                subError = "";
            }
            System.out.println("Chiều sắp xếp: 1. Tăng dần (A-Z / Cũ nhất) | 2. Giảm dần (Z-A / Mới nhất)");
            dirChoice = inputInt(scanner, "Lựa chọn của bạn (1-2): ");
            if (dirChoice == 1 || dirChoice == 2) break;
            subError = "Lỗi: Chỉ được chọn số 1 hoặc số 2!\n";
        }
        String direction = (dirChoice == 2) ? "DESC" : "ASC";

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

    // --- 4. HỦY ĐĂNG KÝ KHÓA HỌC ---
    public void cancelEnrollmentCourse(Scanner scanner) {
        int currentPage = 1;
        String errorMsg = "";

        while (true) {
            int totalPages = enrollmentService.getStudentCoursesTotalPages(currentStudent.getId(), pageSize);
            List<Course> enrolledPage = enrollmentService.getCoursesByStudentId(currentStudent.getId(), currentPage, pageSize);

            if ((enrolledPage == null || enrolledPage.isEmpty()) && currentPage == 1) {
                System.out.println("Bạn hiện tại không có khóa học nào khả dụng để thực hiện hủy đăng ký.");
                return;
            }

            System.out.printf("\n=== DANH SÁCH KHÓA HỌC CÓ THỂ HỦY (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTable(enrolledPage);

            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }

            System.out.print("\n[N]: Trang kế | [P]: Trang trước | [Mã ID]: Nhập ID để HỦY | [E]: Thoát: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("E")) break;
            if (input.equals("N")) {
                if (currentPage < totalPages) currentPage++;
                else errorMsg = "Lỗi: Đã ở trang cuối.\n";
                continue;
            }
            if (input.equals("P")) {
                if (currentPage > 1) currentPage--;
                else errorMsg = "Lỗi: Đã ở trang đầu.\n";
                continue;
            }

            try {
                int courseId = Integer.parseInt(input);

                String confirmError = "";
                while (true) {
                    if (!confirmError.isEmpty()) {
                        System.out.print(confirmError);
                        confirmError = "";
                    }
                    System.out.print("Bạn chắc chắn muốn hủy đăng ký khóa học này? (Gõ 'Y': Xác nhận | 'N': Bỏ qua): ");
                    String confirm = scanner.nextLine().trim().toUpperCase();

                    if (confirm.equals("Y")) {
                        if (enrollmentService.cancelEnrollment(currentStudent.getId(), courseId)) {
                            System.out.println("Hủy đăng ký khóa học thành công!");
                        } else {
                            System.err.println("Hủy thất bại! Khóa học đã được duyệt duyệt thành công hoặc không nằm trong danh sách đăng ký.");
                        }
                        break;
                    } else if (confirm.equals("N")) {
                        System.out.println("Thao tác hủy được dừng lại. Dữ liệu giữ nguyên.");
                        break;
                    } else {
                        confirmError = "Lỗi: Bạn chỉ được gõ ký tự 'Y' hoặc 'N'!\n";
                    }
                }
                break;
            } catch (NumberFormatException e) {
                errorMsg = "Lỗi: Vui lòng nhập đúng mã ID số nguyên hoặc lệnh chức năng điều hướng!\n";
            }
        }
    }

    // --- 5. ĐỔI MẬT KHẨU TÀI KHOẢN ---
    private void changePassword(Scanner scanner) {
        System.out.println("\n--- ĐỔI MẬT KHẨU TÀI KHOẢN ---");

        String errorMsg = "";
        while (true) {
            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }
            System.out.print("Nhập mật khẩu cũ hiện tại: ");
            String oldPass = scanner.nextLine().trim();

            if (!PasswordHasher.hashPassword(oldPass).equals(currentStudent.getPassword())) {
                errorMsg = "Lỗi: Mật khẩu cũ không chính xác! Vui lòng kiểm tra lại.\n";
                continue;
            }
            break;
        }

        while (true) {
            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }
            System.out.print("Nhập thiết lập mật khẩu MỚI: ");
            String newPass = scanner.nextLine().trim();
            if (newPass.isEmpty()) {
                errorMsg = "Lỗi: Mật khẩu mới không được để trống!\n";
                continue;
            }

            currentStudent.setPassword(PasswordHasher.hashPassword(newPass));
            if (studentService.updateStudent(currentStudent)) {
                System.out.println("Bạn đã đổi mật khẩu tài khoản thành công!");
            } else {
                System.err.println("Lỗi hệ thống: Không thể cập nhật mật khẩu mới.");
            }
            break;
        }
    }

    // --- HÀM ÉP KIỂU SỐ NGUYÊN ĐỒNG BỘ LỖI CỰC ĐẸP ---
    private int inputInt(Scanner scanner, String msg) {
        String errorMsg = "";
        while (true) {
            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }
            System.out.print(msg);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                errorMsg = "Lỗi: Bạn phải nhập vào là số nguyên hợp lệ!\n";
            }
        }
    }

    // --- TỰ ĐỘNG ĐÁNH DẤU TRẠNG THÁI [ĐÃ ĐĂNG KÝ] ---
    private void printCourseTable(List<Course> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Không có dữ liệu khóa học nào trong trang này.");
            return;
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(49) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(25) + "+");
        System.out.printf("| %-4s | %-47s | %-13s | %-23s | %-23s |\n", "ID", "TÊN KHÓA HỌC", "THỜI LƯỢNG (H)", "GIẢNG VIÊN HƯỚNG DẪN", "NGÀY TẠO KHÓA");
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(49) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+"  + "-".repeat(25) + "+");

        for (Course c : list) {
            String courseNameDisplay = c.getName();
            if (enrollmentService.isCourseEnrolled(currentStudent.getId(), c.getId())) {
                courseNameDisplay += " [ĐÃ ĐĂNG KÝ]";
            }
            System.out.printf("| %-4d | %-47s | %-13d | %-23s | %-23s |\n", c.getId(), courseNameDisplay, c.getDuration(), c.getInstructor(), c.getCreateAt().toString());
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(49) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(25) + "+");
    }
}
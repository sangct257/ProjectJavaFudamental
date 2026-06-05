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
    private final int pageSize = 3; // Số lượng bản ghi cố định hiển thị trên mỗi trang console

    public StudentView(Scanner scanner, String loginEmail) {
        this.currentStudent = enrollmentService.getStudentByEmail(loginEmail);

        if (this.currentStudent == null) {
            System.err.println("Lỗi hệ thống: Không tìm thấy thông tin tài khoản học viên!");
            return;
        }

        int choose = 0;
        boolean flag = true;
        while (flag) {
            System.out.println("\n============= MENU STUDENT ==========");
            System.out.println("Chào mừng học viên: " + currentStudent.getName());
            System.out.println("1. Xem danh sách & Tìm kiếm khoá học (Phân trang)");
            System.out.println("2. Đăng ký khoá học");
            System.out.println("3. Xem khoá học đã đăng ký (Sắp xếp/Phân trang)");
            System.out.println("4. Huỷ đăng ký (Nếu trạng thái WAITING)");
            System.out.println("5. Đổi mật khẩu");
            System.out.println("6. Đăng xuất");
            System.out.println("===================================");
            while (true) {
                try {
                    System.out.print("Mời chọn: ");
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("Bạn phải nhập vào là số!");
                }
            }

            switch (choose) {
                case 1:
                    menuDisplayAndSearchCourses(scanner);
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

    // --- TÍCH HỢP SUB-MENU CHO TÍNH NĂNG XEM VÀ TÌM KIẾM ---
    private void menuDisplayAndSearchCourses(Scanner scanner) {
        System.out.println("\n--- TÙY CHỌN XEM DANH SÁCH KHÓA HỌC ---");
        System.out.println("1. Xem toàn bộ danh sách khóa học hiện có");
        System.out.println("2. Tìm kiếm khóa học theo tên");
        System.out.print("Lựa chọn của bạn (1-2): ");
        String choice = scanner.nextLine().trim();
        if (choice.equalsIgnoreCase("1")) {
            displayAllCourses(scanner);
        } else if (choice.equalsIgnoreCase("2")) {
            searchCoursesByName(scanner);
        } else {
            System.err.println("Lựa chọn không phù hợp, tự động quay lại menu chính.");
        }
    }

    // XEM TOÀN BỘ DANH SÁCH KHÓA HỌC PHÂN TRANG (CÓ ĐÁNH DẤU)
    public void displayAllCourses(Scanner scanner) {
        int currentPage = 1;

        while (true) {
            int totalPages = enrollmentService.getTotalPages(pageSize);
            List<Course> coursesPage = enrollmentService.getCoursesByPage(currentPage, pageSize);

            System.out.println("\n=== DANH SÁCH KHÓA HỌC HIỆN CÓ ===");
            System.out.printf("TRANG %d / %d\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTable(coursesPage);

            if (totalPages > 1) {
                System.out.println("[N]: Trang kế tiếp  |  [P]: Trang trước đó  |  [E]: Thoát");
                System.out.print("Mời bạn chọn hành động: ");
                String action = scanner.nextLine().trim().toUpperCase();

                if (action.equalsIgnoreCase("N") && currentPage < totalPages) {
                    currentPage++;
                } else if (action.equalsIgnoreCase("P") && currentPage > 1) {
                    currentPage--;
                } else if (action.equalsIgnoreCase("E")) {
                    break;
                } else {
                    System.err.println("Lệnh điều hướng không hợp lệ!");
                }
            } else {
                break;
            }
        }
    }

    // TÌM KIẾM KHÓA HỌC THEO TÊN PHÂN TRANG (CÓ ĐÁNH DẤU)
    public void searchCoursesByName(Scanner scanner) {
        System.out.print("\nNhập từ khóa tên khóa học muốn tìm kiếm: ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) {
            System.err.println("Từ khóa tìm kiếm không thể bỏ trống!");
            return;
        }

        int currentPage = 1;
        while (true) {
            int totalPages = enrollmentService.getSearchCoursesTotalPages(keyword, pageSize);
            List<Course> searchPage = enrollmentService.searchCoursesByPage(keyword, currentPage, pageSize);

            System.out.println("\n=== KẾT QUẢ TÌM KIẾM KHÓA HỌC ===");
            System.out.printf("TRANG %d / %d (Từ khóa: '%s')\n", currentPage, totalPages == 0 ? 1 : totalPages, keyword);
            printCourseTable(searchPage);

            if (totalPages > 1) {
                System.out.println("[N]: Trang kế tiếp  |  [P]: Trang trước đó  |  [E]: Thoát");
                System.out.print("Mời bạn chọn hành động: ");
                String action = scanner.nextLine().trim().toUpperCase();

                if (action.equals("N") && currentPage < totalPages) {
                    currentPage++;
                } else if (action.equals("P") && currentPage > 1) {
                    currentPage--;
                } else if (action.equals("E")) {
                    break;
                } else System.err.println("Lệnh điều hướng không hợp lệ!");
            } else {
                break;
            }
        }
    }

    // ĐĂNG KÝ KHÓA HỌC (PHÂN TRANG + ĐỀ XUẤT + ĐÁNH DẤU TRẠNG THÁI)
    public void enrollCourse(Scanner scanner) {
        int currentPage = 1;

        while (true) {
            int totalPages = enrollmentService.getTotalPages(pageSize);
            List<Course> coursesPage = enrollmentService.getCoursesByPage(currentPage, pageSize);

            System.out.println("\n=== CHỌN KHÓA HỌC BẠN MUỐN ĐĂNG KÝ ===");
            System.out.printf("TRANG %d / %d\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTable(coursesPage);

            List<Course> recommendations = enrollmentService.getRecommendedCourses(currentStudent.getId());
            if (recommendations != null && !recommendations.isEmpty()) {
                System.out.println("\nCÓ THỂ BẠN SẼ THÍCH (Gợi ý dựa trên xu hướng của bạn):");
                printCourseTable(recommendations);
            }

            System.out.println("\n[N]: Trang kế  |  [P]: Trang trước  |  [Mã ID]: Nhập ID khóa học để đăng ký  |  [E]: Thoát");
            System.out.print("Lựa chọn của bạn: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("E")) {
                break;
            } else if (input.equalsIgnoreCase("N")) {
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Bạn đang ở trang cuối cùng.");
                }
            } else if (input.equalsIgnoreCase("P")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Bạn đang ở trang đầu tiên.");
                }
            } else {
                try {
                    int courseId = Integer.parseInt(input);
                    if (enrollmentService.enrollCourse(currentStudent.getId(), courseId)) {
                        System.out.println("Đăng ký học thành công! Vui lòng chờ Ban quản lý phê duyệt.");
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi: Vui lòng nhập đúng mã ID số nguyên hoặc lệnh điều hướng (N/P/E)!");
                }
            }
        }
    }

    // XEM KHÓA HỌC ĐÃ ĐĂNG KÝ (PHÂN TRANG & SẮP XẾP)
    public void displayEnrolledCourses(Scanner scanner) {
        int currentPage = 1;

        while (true) {
            int totalPages = enrollmentService.getStudentCoursesTotalPages(currentStudent.getId(), pageSize);
            List<Course> enrolledPage = enrollmentService.getStudentCoursesByPage(currentStudent.getId(), currentPage, pageSize);

            if (enrolledPage.isEmpty() && currentPage == 1) {
                System.out.println("Bạn chưa đăng ký tham gia khóa học nào.");
                return;
            }

            System.out.println("\n=== CÁC KHÓA HỌC BẠN ĐÃ ĐĂNG KÝ ===");
            System.out.printf("TRANG %d / %d\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTable(enrolledPage);

            System.out.println("\n[N]: Trang kế  |  [P]: Trang trước  |  [S]: Sắp xếp lại danh sách  |  [E]: Thoát");
            System.out.print("Lựa chọn của bạn: ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) {
                return;
            } else if (action.equals("N") && currentPage < totalPages) {
                currentPage++;
            } else if (action.equals("P") && currentPage > 1) {
                currentPage--;
            } else if (action.equals("S")) {
                configAndShowSortedCourses(scanner);
                return;
            } else {
                System.err.println("Lệnh không hợp lệ hoặc đã hết danh sách trang!");
            }
        }
    }

    private void configAndShowSortedCourses(Scanner scanner) {
        String column = "id";
        String direction = "ASC";

        while (true) {
            System.out.println("\nSắp xếp theo: 1. Tên khóa học | 2. Thời lượng (h)");
            System.out.print("Lựa chọn của bạn (1-2): ");
            String choiceCol = scanner.nextLine().trim();
            if (choiceCol.equalsIgnoreCase("1")) {
                column = "name";
                break;
            } else if (choiceCol.equalsIgnoreCase("2")) {
                column = "duration";
                break;
            } else System.err.println("Lựa chọn không hợp lệ!");
        }

        while (true) {
            System.out.println("Chiều sắp xếp: 1. Tăng dần (A-Z) | 2. Giảm dần (Z-A)");
            System.out.print("Lựa chọn của bạn (1-2): ");
            String choiceDir = scanner.nextLine().trim();
            if (choiceDir.equalsIgnoreCase("1")) {
                direction = "ASC";
                break;
            } else if (choiceDir.equalsIgnoreCase("2")) {
                direction = "DESC";
                break;
            } else System.err.println("Lựa chọn không hợp lệ!");
        }

        int currentPage = 1;

        while (true) {
            int totalPages = enrollmentService.getStudentCoursesTotalPages(currentStudent.getId(), pageSize);
            List<Course> sortedPage = enrollmentService.getStudentCoursesSortedByPage(currentStudent.getId(), column, direction, currentPage, pageSize);

            System.out.println("\n=== KẾT QUẢ DANH SÁCH SAU KHI SẮP XẾP (PHÂN TRANG) ===");
            System.out.printf("TRANG %d / %d (Tiêu chí: %s - %s)\n", currentPage, totalPages, column, direction);
            printCourseTable(sortedPage);

            if (totalPages > 1) {
                System.out.println("[N]: Trang kế tiếp  |  [P]: Trang trước đó  |  [E]: Quay lại");
                System.out.print("Mời chọn hành động: ");
                String act = scanner.nextLine().trim().toUpperCase();
                if (act.equalsIgnoreCase("N") && currentPage < totalPages) {
                    currentPage++;
                } else if (act.equalsIgnoreCase("P") && currentPage > 1) {
                    currentPage--;
                } else if (act.equalsIgnoreCase("E")) {
                    break;
                } else {
                    System.err.println("Lệnh không hợp lệ!");
                }
            } else {
                break;
            }
        }
    }

    // HỦY ĐĂNG KÝ KHÓA HỌC (PHÂN TRANG CHỌN HỦY)
    public void cancelEnrollmentCourse(Scanner scanner) {
        int currentPage = 1;

        while (true) {
            int totalPages = enrollmentService.getStudentCoursesTotalPages(currentStudent.getId(), pageSize);
            List<Course> enrolledPage = enrollmentService.getStudentCoursesByPage(currentStudent.getId(), currentPage, pageSize);

            if (enrolledPage.isEmpty() && currentPage == 1) {
                System.out.println("Bạn không có khóa học nào để thực hiện hủy.");
                return;
            }

            System.out.println("\n=== DANH SÁCH KHÓA HỌC CÓ THỂ HỦY ===");
            System.out.printf("TRANG %d / %d\n", currentPage, totalPages);
            printCourseTable(enrolledPage);

            System.out.println("\n[N]: Trang kế  |  [P]: Trang trước  |  [Mã ID]: Nhập mã ID khóa học để HỦY  |  [E]: Thoát");
            System.out.print("Lựa chọn của bạn: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("E")) {
                break;
            } else if (input.equalsIgnoreCase("N")) {
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Bạn đang ở trang cuối cùng.");
                }
            } else if (input.equalsIgnoreCase("P")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Bạn đang ở trang đầu tiên.");
                }
            } else {
                try {
                    int courseId = Integer.parseInt(input);
                    System.out.print("Bạn chắc chắn muốn hủy đăng ký khóa học này? (Gõ 'Y' để xác nhận): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                        if (enrollmentService.cancelEnrolledCourse(currentStudent.getId(), courseId)) {
                            System.out.println("🎉 Hủy đăng ký khóa học thành công!");
                            break;
                        } else {
                            System.err.println("Không thể hủy! Lý do: Khóa học đã được duyệt hoặc không tồn tại.");
                        }
                    } else {
                        System.out.println("Thao tác hủy bỏ được dừng lại.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi: Vui lòng nhập đúng mã ID số nguyên hoặc các phím chức năng!");
                }
            }
        }
    }

    // ĐỔI MẬT KHẨU TÀI KHOẢN
    private void changePassword(Scanner scanner) {
        System.out.println("\n--- ĐỔI MẬT KHẨU TÀI KHOẢN ---");
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

    // --- HÀM VẼ BẢNG: TỰ ĐỘNG ĐÁNH DẤU TRẠNG THÁI [ĐÃ ĐĂNG KÝ] ---
    private void printCourseTable(List<Course> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Không có dữ liệu khóa học nào trong trang này.");
            return;
        }
        // Độ rộng cột TÊN KHÓA HỌC tăng lên 47 để hiển thị gọn gàng khi nối thêm chuỗi trạng thái
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(49) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+");
        System.out.printf("| %-4s | %-47s | %-13s | %-23s |\n", "ID", "TÊN KHÓA HỌC", "THỜI LƯỢNG (H)", "GIẢNG VIÊN HƯỚNG DẪN");
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(49) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+");

        for (Course c : list) {
            String courseNameDisplay = c.getName();

            // Gọi hàm check chéo thông tin đăng ký trong DB từ Service
            boolean isRegistered = enrollmentService.isCourseEnrolledByStudent(currentStudent.getId(), c.getId());
            if (isRegistered) {
                courseNameDisplay += " [ĐÃ ĐĂNG KÝ]";
            }

            System.out.printf("| %-4d | %-47s | %-13d | %-23s |\n",
                    c.getId(), courseNameDisplay, c.getDuration(), c.getInstructor());
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(49) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+");
    }
}
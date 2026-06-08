package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.EnrollmentService;
import ra.yourprojectname.business.impl.EnrollmentServiceImpl;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Enrollment;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class EnrollmentForAdminView {
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final int pageSize = 3;
    // Tạo sẵn formatter để định dạng ngày giờ cho gọn đẹp, vừa vặn với bảng
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EnrollmentForAdminView(Scanner scanner) {
        boolean flag = true;
        int choose;
        while (flag) {
            System.out.println("\n================= QUẢN LÝ ĐĂNG KÝ KHOÁ HỌC =================");
            System.out.println("1. Hiển thị học viên theo từng khoá học");
            System.out.println("2. Duyệt học viên đăng ký khóa học");
            System.out.println("3. Xóa học viên khỏi khoá học (Xem danh sách trước)");
            System.out.println("4. Quay về menu chính");
            System.out.println("============================================================");
            while (true) {
                System.out.print("Nhập lựa chọn: ");
                try {
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (Exception e) {
                    System.out.println("Lỗi: Bạn phải nhập vào là số nguyên!");
                }
            }
            switch (choose) {
                case 1:
                    displayStudentsByCourse(scanner);
                    break;
                case 2:
                    approveStudentEnrollment(scanner);
                    break;
                case 3:
                    removeStudentFromCourse(scanner);
                    break;
                case 4:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 4");
            }
        }
    }

    private void displayStudentsByCourse(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalPages = enrollmentService.getTotalPages(pageSize);
            List<Course> courses = enrollmentService.getAllCourses(currentPage, pageSize);

            System.out.printf("\n--- CHỌN KHÓA HỌC ĐỂ XEM DANH SÁCH HỌC VIÊN (TRANG %d / %d) ---\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTableSummary(courses);

            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [Mã ID]: Chọn khóa học  |  [E]: Thoát: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("E")) {
                break;
            }
            if (input.equals("N")) {
                if (currentPage < totalPages) currentPage++;
                else System.out.println("Bạn đang ở trang cuối cùng của danh sách khóa học.");
                continue;
            }
            if (input.equals("P")) {
                if (currentPage > 1) currentPage--;
                else System.out.println("Bạn đang ở trang đầu tiên của danh sách khóa học.");
                continue;
            }

            try {
                int courseId = Integer.parseInt(input);

                int studentPage = 1;
                while (true) {
                    List<Enrollment> enrollments = enrollmentService.getStudentsByCourseId(courseId, studentPage, pageSize);

                    System.out.printf("\nDANH SÁCH HỌC VIÊN ĐĂNG KÝ KHÓA HỌC ID [%d] (TRANG: %d):\n", courseId, studentPage);
                    printStudentTable(enrollments);

                    System.out.print("\n[P]: Trang trước SV  |  [N]: Trang kế SV  |  [E]: Quay lại danh sách khóa học: ");
                    String subInput = scanner.nextLine().trim().toUpperCase();

                    if (subInput.equals("E")) {
                        break;
                    }
                    if (subInput.equals("N")) {
                        if (enrollments.size() == pageSize) {
                            studentPage++;
                        } else {
                            System.out.println("Đã hết danh sách học viên ở trang kế tiếp.");
                        }
                        continue;
                    }
                    if (subInput.equals("P")) {
                        if (studentPage > 1) {
                            studentPage--;
                        } else {
                            System.out.println("Bạn đang ở trang học viên đầu tiên.");
                        }
                    }
                }
                break;
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Vui lòng nhập đúng mã ID số nguyên hoặc lệnh điều hướng [P/N/E]!");
            }
        }
    }

    private void approveStudentEnrollment(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            List<Enrollment> pendingEnrollments = enrollmentService.getPendingEnrollments(currentPage, pageSize);

            System.out.printf("\nDANH SÁCH HỌC VIÊN CHỜ DUYỆT ĐĂNG KÝ (TRANG %d) ===\n", currentPage);
            printStudentTable(pendingEnrollments);

            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [S]: Tiến hành duyệt  |  [E]: Thoát: ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) break;
            if (action.equals("N")) {
                if (pendingEnrollments.size() == pageSize) currentPage++;
                else System.out.println("Không còn yêu cầu chờ duyệt nào ở trang tiếp theo.");
                continue;
            }
            if (action.equals("P")) {
                if (currentPage > 1) currentPage--;
                else System.out.println("Bạn đang ở trang đầu tiên.");
                continue;
            }
            if (action.equals("S")) {
                int studentId = inputInt(scanner, "Nhập ID Học viên muốn duyệt: ");
                int courseId = inputInt(scanner, "Nhập ID Khóa học muốn duyệt: ");

                if (enrollmentService.approveEnrollment(studentId, courseId)) {
                    System.out.println("Phê duyệt thành công! Trạng thái chuyển từ WAITING sang CONFIRMED.");
                } else {
                    System.err.println("Duyệt thất bại! Vui lòng kiểm tra lại cặp ID học viên và khóa học (Phải ở trạng thái WAITING).");
                }
                break;
            }
        }
    }

    private void removeStudentFromCourse(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalPages = enrollmentService.getTotalPages(pageSize);
            List<Course> courses = enrollmentService.getAllCourses(currentPage, pageSize);

            System.out.printf("\n--- BƯỚC 1: CHỌN KHÓA HỌC MUỐN XÓA HỌC VIÊN (TRANG %d / %d) ---\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTableSummary(courses);

            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [Mã ID]: Chọn khóa học  |  [E]: Hủy bỏ: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("E")) {
                break;
            }
            if (input.equals("N")) {
                if (currentPage < totalPages) currentPage++;
                else System.out.println("Bạn đang ở trang cuối cùng.");
                continue;
            }
            if (input.equals("P")) {
                if (currentPage > 1) currentPage--;
                else System.out.println("Bạn đang ở trang đầu tiên.");
                continue;
            }

            try {
                int courseId = Integer.parseInt(input);

                int studentPage = 1;
                while (true) {
                    List<Enrollment> enrollments = enrollmentService.getStudentsByCourseId(courseId, studentPage, pageSize);

                    System.out.printf("\n--- BƯỚC 2: DANH SÁCH HỌC VIÊN TRONG LỚP [%d] (TRANG SV: %d) ---\n", courseId, studentPage);
                    printStudentTable(enrollments);

                    System.out.print("\n[P]: Trang trước SV | [N]: Trang kế SV | [D]: Chọn ID xóa | [E]: Quay lại chọn khóa học: ");
                    String subInput = scanner.nextLine().trim().toUpperCase();

                    if (subInput.equals("E")) {
                        break;
                    }
                    if (subInput.equals("N")) {
                        if (enrollments.size() == pageSize) {
                            studentPage++;
                        } else {
                            System.out.println("Đã hết danh sách học viên ở trang kế tiếp.");
                        }
                        continue;
                    }
                    if (subInput.equals("P")) {
                        if (studentPage > 1) {
                            studentPage--;
                        } else {
                            System.out.println("Bạn đang ở trang học viên đầu tiên.");
                        }
                        continue;
                    }

                    if (subInput.equals("D")) {
                        int studentId = inputInt(scanner, "Nhập ID Học viên muốn xóa khỏi lớp này: ");

                        if (!enrollmentService.isStudentInCourse(studentId, courseId)) {
                            System.err.println("Lỗi: Mã học viên này không học trong lớp hiện tại!");
                            continue;
                        }

                        System.out.print("CẢNH BÁO: Bạn chắc chắn muốn XÓA học viên này ra khỏi khóa học? (Gõ 'Y' để đồng ý : Gõ bất kì để huỷ giữ nguyên thông tin): ");
                        String confirm = scanner.nextLine().trim();

                        if (confirm.equalsIgnoreCase("Y")) {
                            if (enrollmentService.removeStudentFromCourse(studentId, courseId)) {
                                System.out.println("Thành công: Đã xóa học viên ra khỏi lớp học!");
                            } else {
                                System.err.println("Lỗi hệ thống: Không thể thực thi hành động xóa.");
                            }
                        } else {
                            System.out.println("Đã hủy lệnh xóa. Học viên vẫn an toàn trong lớp.");
                        }
                        break;
                    }
                }
                break;
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Vui lòng nhập số nguyên hợp lệ!");
            }
        }
    }

    private int inputInt(Scanner scanner, String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.err.println("⚠️ Lỗi: Giá trị nhập vào phải là số nguyên!");
            }
        }
    }

    private void printCourseTableSummary(List<Course> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Không có dữ liệu khóa học.");
            return;
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(40) + "+" + "-".repeat(25) + "+");
        System.out.printf("| %-4s | %-38s | %-23s |\n", "ID", "TÊN KHÓA HỌC", "GIẢNG VIÊN");
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(40) + "+" + "-".repeat(25) + "+");
        for (Course c : list) {
            System.out.printf("| %-4d | %-38s | %-23s |\n", c.getId(), c.getName(), c.getInstructor());
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(40) + "+" + "-".repeat(25) + "+");
    }

    private void printStudentTable(List<Enrollment> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("ℹ️ Danh sách trống (Không có dữ liệu đăng ký).");
            return;
        }

        System.out.println("+" + "-".repeat(15) + "+" + "-".repeat(22) + "+" + "-".repeat(12) + "+" + "-".repeat(25) + "+" + "-".repeat(20) + "+" + "-".repeat(13) + "+" + "-".repeat(18) + "+");
        System.out.printf("| %-13s | %-20s | %-10s | %-23s | %-18s | %-11s | %-16s |\n",
                "Mã HỌC VIÊN", "TÊN HỌC VIÊN", "SĐT", "TÊN KHÓA HỌC", "GIẢNG VIÊN", "TRẠNG THÁI", "NGÀY ĐĂNG KÝ");
        System.out.println("+" + "-".repeat(15) + "+" + "-".repeat(22) + "+" + "-".repeat(12) + "+" + "-".repeat(25) + "+" + "-".repeat(20) + "+" + "-".repeat(13) + "+" + "-".repeat(18) + "+");

        for (Enrollment e : list) {
            if (e.getStudent() != null) {
                String formattedDate = e.getRegisteredAt() != null ? e.getRegisteredAt().format(formatter) : "Chưa có";
                String courseName = e.getCourse() != null ? e.getCourse().getName() : "Không rõ";
                String instructor = e.getCourse() != null ? e.getCourse().getInstructor() : "Chưa phân công";
                String phone = e.getStudent().getPhone() != null ? e.getStudent().getPhone() : "Chưa có";

                System.out.printf("| %-13d | %-20s | %-10s | %-23s | %-18s | %-11s | %-16s |\n",
                        e.getStudent().getId(),
                        e.getStudent().getName(),
                        phone,
                        courseName,
                        instructor,
                        e.getStatus().toString(),
                        formattedDate);
            }
        }
        System.out.println("+" + "-".repeat(15) + "+" + "-".repeat(22) + "+" + "-".repeat(12) + "+" + "-".repeat(25) + "+" + "-".repeat(20) + "+" + "-".repeat(13) + "+" + "-".repeat(18) + "+");
    }
}
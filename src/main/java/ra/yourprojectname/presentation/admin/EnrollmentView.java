package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.EnrollmentService;
import ra.yourprojectname.business.impl.EnrollmentServiceImpl;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Enrollment;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class EnrollmentView {
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final int pageSize = 3;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EnrollmentView(Scanner scanner) {
        boolean flag = true;
        int choose;
        String errorMsg = "";

        while (flag) {
            System.out.println("\n================= QUẢN LÝ ĐĂNG KÝ KHOÁ HỌC =================");
            System.out.println("""
            1. Hiển thị học viên theo từng khoá học
            2. Duyệt học viên đăng ký khóa học
            3. Xóa học viên khỏi khoá học (Xem danh sách trước)
            4. Quay về menu chính """);
            System.out.println("============================================================");

            while (true) {
                if (!errorMsg.isEmpty()) {
                    System.out.print(errorMsg);
                    errorMsg = "";
                }
                System.out.print("Nhập lựa chọn (1-4): ");
                try {
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (Exception e) {
                    errorMsg = "Lỗi: Bạn phải nhập vào là số nguyên!\n";
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
                    errorMsg = "Lỗi: Bạn chỉ được nhập từ 1 đến 4!\n";
            }
        }
    }

    private void displayStudentsByCourse(Scanner scanner) {
        int currentPage = 1;
        String errorMsg = "";

        while (true) {
            int totalPages = enrollmentService.getTotalPages(pageSize);
            List<Course> courses = enrollmentService.getAllCourses(currentPage, pageSize);

            System.out.printf("\n--- CHỌN KHÓA HỌC ĐỂ XEM DANH SÁCH HỌC VIÊN (TRANG %d / %d) ---\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTableSummary(courses);

            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }

            System.out.print("\n[P]: Trang trước | [N]: Trang kế | [Mã ID]: Chọn khóa học | [E]: Thoát: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("E")) {
                break;
            }
            if (input.equals("N")) {
                if (currentPage < totalPages) currentPage++;
                else errorMsg = "Lỗi: Bạn đang ở trang cuối cùng của danh sách khóa học.\n";
                continue;
            }
            if (input.equals("P")) {
                if (currentPage > 1) currentPage--;
                else errorMsg = "Lỗi: Bạn đang ở trang đầu tiên của danh sách khóa học.\n";
                continue;
            }

            try {
                int courseId = Integer.parseInt(input);
                int studentPage = 1;
                String subErrorMsg = "";

                while (true) {
                    List<Enrollment> enrollments = enrollmentService.getStudentsByCourseId(courseId, studentPage, pageSize);

                    System.out.printf("\nDANH SÁCH HỌC VIÊN ĐĂNG KÝ KHÓA HỌC ID [%d] (TRANG: %d):\n", courseId, studentPage);
                    printStudentTable(enrollments);

                    if (!subErrorMsg.isEmpty()) {
                        System.out.print(subErrorMsg);
                        subErrorMsg = "";
                    }

                    System.out.print("\n[P]: Trang trước SV | [N]: Trang kế SV | [E]: Quay lại danh sách khóa học: ");
                    String subInput = scanner.nextLine().trim().toUpperCase();

                    if (subInput.equals("E")) {
                        break;
                    }
                    if (subInput.equals("N")) {
                        if (enrollments.size() == pageSize) {
                            studentPage++;
                        } else {
                            subErrorMsg = "Lỗi: Đã hết danh sách học viên ở trang kế tiếp.\n";
                        }
                        continue;
                    }
                    if (subInput.equals("P")) {
                        if (studentPage > 1) {
                            studentPage--;
                        } else {
                            subErrorMsg = "Lỗi: Bạn đang ở trang học viên đầu tiên.\n";
                        }
                        continue;
                    }
                    subErrorMsg = "Lỗi: Lệnh điều hướng không hợp lệ!\n";
                }
                break;
            } catch (NumberFormatException e) {
                errorMsg = "Lỗi: Vui lòng nhập đúng mã ID số nguyên hoặc lệnh điều hướng [P/N/E]!\n";
            }
        }
    }

    private void approveStudentEnrollment(Scanner scanner) {
        int currentPage = 1;
        String errorMsg = "";

        while (true) {
            List<Enrollment> pendingEnrollments = enrollmentService.getPendingEnrollments(currentPage, pageSize);

            System.out.printf("\n=== DANH SÁCH HỌC VIÊN CHỜ DUYỆT ĐĂNG KÝ (TRANG %d) ===\n", currentPage);
            printStudentTable(pendingEnrollments);

            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }

            System.out.print("\n[P]: Trang trước | [N]: Trang kế | [S]: Tiến hành duyệt | [E]: Thoát: ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) break;
            if (action.equals("N")) {
                if (pendingEnrollments.size() == pageSize) currentPage++;
                else errorMsg = "Lỗi: Không còn yêu cầu chờ duyệt nào ở trang tiếp theo.\n";
                continue;
            }
            if (action.equals("P")) {
                if (currentPage > 1) currentPage--;
                else errorMsg = "Lỗi: Bạn đang ở trang đầu tiên.\n";
                continue;
            }
            if (action.equals("S")) {
                int studentId = inputInt(scanner, "Nhập ID Học viên muốn duyệt: ");
                int courseId = inputInt(scanner, "Nhập ID Khóa học muốn duyệt: ");

                if (enrollmentService.approveEnrollment(studentId, courseId)) {
                    System.out.println("Phê duyệt thành công! Trạng thái chuyển từ WAITING sang CONFIRMED.");
                } else {
                    errorMsg = "Duyệt thất bại! Vui lòng kiểm tra lại cặp ID học viên và khóa học (Phải ở trạng thái WAITING).\n";
                }
                continue; // Trở lại màn hình danh sách chờ duyệt sau khi thực thi
            }
            errorMsg = "Lỗi: Lệnh thao tác không hợp lệ!\n";
        }
    }

    private void removeStudentFromCourse(Scanner scanner) {
        int currentPage = 1;
        String errorMsg = "";

        while (true) {
            int totalPages = enrollmentService.getTotalPages(pageSize);
            List<Course> courses = enrollmentService.getAllCourses(currentPage, pageSize);

            System.out.printf("\n--- BƯỚC 1: CHỌN KHÓA HỌC MUỐN XÓA HỌC VIÊN (TRANG %d / %d) ---\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTableSummary(courses);

            if (!errorMsg.isEmpty()) {
                System.out.print(errorMsg);
                errorMsg = "";
            }

            System.out.print("\n[P]: Trang trước | [N]: Trang kế | [Mã ID]: Chọn khóa học | [E]: Hủy bỏ: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("E")) {
                break;
            }
            if (input.equals("N")) {
                if (currentPage < totalPages) currentPage++;
                else errorMsg = "Lỗi: Bạn đang ở trang cuối cùng.\n";
                continue;
            }
            if (input.equals("P")) {
                if (currentPage > 1) currentPage--;
                else errorMsg = "Lỗi: Bạn đang ở trang đầu tiên.\n";
                continue;
            }

            try {
                int courseId = Integer.parseInt(input);
                int studentPage = 1;
                String subErrorMsg = "";

                while (true) {
                    List<Enrollment> enrollments = enrollmentService.getStudentsByCourseId(courseId, studentPage, pageSize);

                    System.out.printf("\n--- BƯỚC 2: DANH SÁCH HỌC VIÊN TRONG LỚP [%d] (TRANG SV: %d) ---\n", courseId, studentPage);
                    printStudentTable(enrollments);

                    if (!subErrorMsg.isEmpty()) {
                        System.out.print(subErrorMsg);
                        subErrorMsg = "";
                    }

                    System.out.print("\n[P]: Trang trước SV | [N]: Trang kế SV | [D]: Chọn ID xóa | [E]: Quay lại chọn khóa học: ");
                    String subInput = scanner.nextLine().trim().toUpperCase();

                    if (subInput.equals("E")) {
                        break;
                    }
                    if (subInput.equals("N")) {
                        if (enrollments.size() == pageSize) {
                            studentPage++;
                        } else {
                            subErrorMsg = "Lỗi: Đã hết danh sách học viên ở trang kế tiếp.\n";
                        }
                        continue;
                    }
                    if (subInput.equals("P")) {
                        if (studentPage > 1) {
                            studentPage--;
                        } else {
                            subErrorMsg = "Lỗi: Bạn đang ở trang học viên đầu tiên.\n";
                        }
                        continue;
                    }

                    if (subInput.equals("D")) {
                        int studentId = inputInt(scanner, "Nhập ID Học viên muốn xóa khỏi lớp này: ");

                        if (!enrollmentService.isStudentInCourse(studentId, courseId)) {
                            subErrorMsg = "Lỗi: Mã học viên này không học trong lớp hiện tại!\n";
                            continue;
                        }

                        String confirmErrorMsg = "";
                        while (true) {
                            if (!confirmErrorMsg.isEmpty()) {
                                System.out.print(confirmErrorMsg);
                                confirmErrorMsg = "";
                            }
                            System.out.print("CẢNH BÁO: Bạn chắc chắn muốn XÓA học viên ra khỏi khóa học? (Gõ 'Y': Đồng ý | 'N': Hủy): ");
                            String confirm = scanner.nextLine().trim().toUpperCase();

                            if (confirm.equals("Y")) {
                                if (enrollmentService.removeStudentFromCourse(studentId, courseId)) {
                                    System.out.println("Thành công: Đã xóa học viên ra khỏi lớp học!");
                                } else {
                                    System.err.println("Lỗi hệ thống: Không thể thực thi hành động xóa.");
                                }
                                break;
                            } else if (confirm.equals("N")) {
                                System.out.println("↩️ Đã hủy lệnh xóa. Học viên vẫn an toàn trong lớp.");
                                break;
                            } else {
                                confirmErrorMsg = "Lỗi: Bạn chỉ được gõ chữ 'Y' hoặc 'N'!\n";
                            }
                        }
                        break;
                    }
                    subErrorMsg = "Lỗi: Lệnh thao tác không hợp lệ!\n";
                }
                break;
            } catch (NumberFormatException e) {
                errorMsg = "Lỗi: Vui lòng nhập số nguyên hợp lệ!\n";
            }
        }
    }

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
                errorMsg = "Lỗi: Giá trị nhập vào phải là số nguyên!\n";
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
            System.out.println("Danh sách trống (Không có dữ liệu đăng ký).");
            return;
        }

        System.out.println("+" + "-".repeat(10) + "+" + "-".repeat(20) + "+" + "-".repeat(12) + "+" + "-".repeat(10) + "+" + "-".repeat(25) + "+" + "-".repeat(18) + "+" + "-".repeat(13) + "+" + "-".repeat(18) + "+");
        System.out.printf("| %-8s | %-18s | %-10s | %-8s | %-23s | %-16s | %-11s | %-16s |\n",
                "ID SV", "TÊN HỌC VIÊN", "SĐT", "ID KH", "TÊN KHÓA HỌC", "GIẢNG VIÊN", "TRẠNG THÁI", "NGÀY ĐĂNG KÝ");
        System.out.println("+" + "-".repeat(10) + "+" + "-".repeat(20) + "+" + "-".repeat(12) + "+" + "-".repeat(10) + "+" + "-".repeat(25) + "+" + "-".repeat(18) + "+" + "-".repeat(13) + "+" + "-".repeat(18) + "+");

        for (Enrollment e : list) {
            if (e.getStudent() != null) {
                String formattedDate = e.getRegisteredAt() != null ? e.getRegisteredAt().format(formatter) : "Chưa có";
                int courseId = e.getCourse() != null ? e.getCourse().getId() : 0;
                String courseName = e.getCourse() != null ? e.getCourse().getName() : "Không rõ";
                String instructor = e.getCourse() != null ? e.getCourse().getInstructor() : "Chưa phân công";
                String phone = e.getStudent().getPhone() != null ? e.getStudent().getPhone() : "Chưa có";

                System.out.printf("| %-8d | %-18s | %-10s | %-8d | %-23s | %-16s | %-11s | %-16s |\n",
                        e.getStudent().getId(),
                        e.getStudent().getName(),
                        phone,
                        courseId,
                        courseName,
                        instructor,
                        e.getStatus().toString(),
                        formattedDate);
            }
        }
        System.out.println("+" + "-".repeat(10) + "+" + "-".repeat(20) + "+" + "-".repeat(12) + "+" + "-".repeat(10) + "+" + "-".repeat(25) + "+" + "-".repeat(18) + "+" + "-".repeat(13) + "+" + "-".repeat(18) + "+");
    }
}
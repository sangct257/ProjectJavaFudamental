package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.StudentService;
import ra.yourprojectname.business.impl.StudentServiceImpl;
import ra.yourprojectname.model.Student;
import ra.yourprojectname.until.PasswordHasher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class StudentForAdminView {
    private final StudentService studentService = new StudentServiceImpl();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private final int pageSize = 3;

    public StudentForAdminView(Scanner scanner) {
        while (true) {
            System.out.println("\n=============== QUẢN LÝ HỌC VIÊN =============");
            System.out.println("1. Hiển thị danh sách học viên\n" +
                    "2. Thêm mới học viên\n" +
                    "3. Chỉnh sửa thông tin\n" +
                    "4. Xóa học viên\n" +
                    "5. Tìm kiếm học viên\n" +
                    "6. Sắp xếp danh sách\n" +
                    "7. Quay về menu chính");
            System.out.println("==============================================");
            int choose = inputInt(scanner, "Nhập lựa chọn: ");

            switch (choose) {
                case 1:
                    displayStudentWithPagination(scanner);
                    break;
                case 2:
                    insertStudent(scanner);
                    break;
                case 3:
                    updateStudent(scanner);
                    break;
                case 4:
                    deleteStudent(scanner);
                    break;
                case 5:
                    findStudentByNameEmailOrId(scanner);
                    break;
                case 6:
                    findAllSorted(scanner);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 7!");
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

    // --- HIỂN THỊ DANH SÁCH ---
    public void displayStudentWithPagination(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalPages = studentService.getTotalPages(pageSize);
            List<Student> list = studentService.getAllStudents(currentPage, pageSize);

            System.out.printf("\n=== DANH SÁCH HỌC VIÊN HỆ THỐNG (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printStudentTable(list);

            currentPage = handlePagination(scanner, currentPage, totalPages);
            if (currentPage == -1) break;
        }
    }

    // --- TÌM KIẾM HỌC VIÊN ---
    public void findStudentByNameEmailOrId(Scanner scanner) {
        System.out.println("\n--- TÌM KIẾM HỌC VIÊN TƯƠNG ĐỐI ---");
        String keyword = inputNonEmpty(scanner, "Nhập từ khóa tìm kiếm (Tên / Email / Mã số ID): ");

        int currentPage = 1;
        while (true) {
            int totalPages = studentService.getSearchTotalPages(keyword, pageSize);
            List<Student> list = studentService.findStudentByNameEmailOrId(keyword, currentPage, pageSize);

            System.out.printf("\n=== KẾT QUẢ TÌM KIẾM CHO TỪ KHÓA [%s] (TRANG %d / %d) ===\n", keyword, currentPage, totalPages == 0 ? 1 : totalPages);
            printStudentTable(list);

            currentPage = handlePagination(scanner, currentPage, totalPages);
            if (currentPage == -1) break;
        }
    }

    // --- SẮP XẾP DANH SÁCH ---
    public void findAllSorted(Scanner scanner) {
        System.out.println("\n--- SẮP XẾP DANH SÁCH HỌC VIÊN ---");
        String column = inputNonEmpty(scanner, "Lựa chọn tiêu chí (1. Tên | 2. Mã ID): ").equals("2") ? "id" : "name";
        String direction = inputNonEmpty(scanner, "Chọn chiều (1. Tăng dần A-Z | 2. Giảm dần Z-A): ").equals("2") ? "DESC" : "ASC";

        int currentPage = 1;
        while (true) {
            int totalPages = studentService.getTotalPages(pageSize);
            List<Student> list = studentService.getAllSortedByNameOrById(column, direction, currentPage, pageSize);

            System.out.printf("\n=== DANH SÁCH HỌC VIÊN SAU KHI SẮP XẾP (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printStudentTable(list);

            currentPage = handlePagination(scanner, currentPage, totalPages);
            if (currentPage == -1) break;
        }
    }

    // --- THÊM MỚI HỌC VIÊN ---
    public void insertStudent(Scanner scanner) {
        System.out.println("\n---- THÊM MỚI HỌC VIÊN ----");
        String name = inputNonEmpty(scanner, "Nhập họ tên học viên: ");
        Date dob = inputDate(scanner, "Nhập ngày sinh (DD-MM-YYYY): ");
        String email = inputEmail(scanner, "Nhập địa chỉ Email: ");
        boolean sex = inputSex(scanner);
        System.out.print("Nhập số điện thoại (Ấn Enter để bỏ qua): ");
        String phone = scanner.nextLine().trim();
        String password = inputNonEmpty(scanner, "Thiết lập mật khẩu đăng nhập: ");

        Student student = new Student(0, name, dob, email, sex, phone.isEmpty() ? null : phone, password, new Date());
        if (studentService.insertStudent(student)) {
            System.out.println("Thêm mới học viên thành công!");
        } else {
            System.err.println("Thêm mới học viên thất bại.");
        }
    }

    // --- CHỈNH SỬA THÔNG TIN ---
    public void updateStudent(Scanner scanner) {
        System.out.println("\n--- CHỈNH SỬA THÔNG TIN HỌC VIÊN ---");
        int id = inputInt(scanner, "Nhập mã ID học viên cần chỉnh sửa: ");

        Student editStudent = studentService.getStudentById(id);

        if (editStudent == null) {
            System.err.println("Không tìm thấy thông tin học viên nào ứng với mã ID = " + id);
            return;
        }

        // Tạo đối tượng Clone để lưu tạm các thay đổi, tránh hỏng dữ liệu gốc khi bấm Hủy
        Student cloneStudent = new Student(
                editStudent.getId(), editStudent.getName(), editStudent.getDob(), editStudent.getEmail(),
                editStudent.isSex(), editStudent.getPhone(), editStudent.getPassword(), editStudent.getCreate_at()
        );

        while (true) {
            System.out.printf("\nHọc viên đang chỉnh sửa: %s\n", cloneStudent.getName());
            System.out.println("1. Sửa họ tên      | 2. Sửa ngày sinh | 3. Sửa Email | 4. Sửa giới tính");
            System.out.println("5. Sửa số điện thoại| 6. Đổi mật khẩu  | 7. Lưu lại   | 8. Hủy bỏ");
            int subChoose = inputInt(scanner, "Chọn thuộc tính cần sửa (1-8): ");

            if (subChoose == 7) {
                if (studentService.updateStudent(cloneStudent)) {
                    System.out.println("Cập nhật thông tin học viên thành công!");
                } else {
                    System.err.println("Thao tác lưu thất bại.");
                }
                break;
            }

            if (subChoose == 8) {
                System.out.println("Đã hủy bỏ mọi sửa đổi. Dữ liệu gốc được giữ nguyên!");
                break;
            }

            switch (subChoose) {
                case 1:
                    cloneStudent.setName(inputNonEmpty(scanner, "Nhập họ tên mới: "));
                    break;
                case 2:
                    cloneStudent.setDob(inputDate(scanner, "Nhập ngày sinh mới (DD-MM-YYYY): "));
                    break;
                case 3:
                    cloneStudent.setEmail(inputEmail(scanner, "Nhập Email mới: "));
                    break;
                case 4:
                    cloneStudent.setSex(inputSex(scanner));
                    break;
                case 5:
                    System.out.print("Nhập số điện thoại mới (Ấn Enter để xóa/bỏ qua): ");
                    String p = scanner.nextLine().trim();
                    cloneStudent.setPhone(p.isEmpty() ? null : p);
                    break;
                case 6:
                    String rawPass = inputNonEmpty(scanner, "Nhập mật khẩu mới: ");
                    cloneStudent.setPassword(PasswordHasher.hashPassword(rawPass));
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn từ 1 đến 8.");
            }
        }
    }

    // --- XÓA HỌC VIÊN ---
    public void deleteStudent(Scanner scanner) {
        System.out.println("\n--- XÓA HỌC VIÊN ---");
        int id = inputInt(scanner, "Nhập mã ID học viên cần xóa: ");

        System.out.print("Cảnh báo: Bạn chắc chắn muốn xóa học viên này? (Gõ 'Y' để đồng ý): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            if (studentService.deleteStudent(id)) {
                System.out.println("Xóa dữ liệu học viên thành công!");
            } else {
                System.err.println("Không tìm thấy học viên mã ID phù hợp.");
            }
        } else {
            System.out.println("Thao tác xóa đã được hủy bỏ.");
        }
    }

    // --- CÁC HÀM TRỢ GIÚP KIỂM TRA ĐẦU VÀO TẬP TRUNG ---
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

    private String inputNonEmpty(Scanner scanner, String msg) {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.err.println("Lỗi: Vui lòng nhập dữ liệu, không bỏ trống!");
        }
    }

    private boolean inputSex(Scanner scanner) {
        while (true) {
            String choice = inputNonEmpty(scanner, "Nhập giới tính học viên (1: Nam / 0: Nữ): ");
            if (choice.equals("1")) return true;
            if (choice.equals("0")) return false;
            System.err.println("Lỗi: Hãy chọn số 1 hoặc số 0!");
        }
    }

    private Date inputDate(Scanner scanner, String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return sdf.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.err.println("Lỗi: Định dạng ngày bắt buộc là DD-MM-YYYY (Ví dụ: 20-11-2003)!");
            }
        }
    }

    private String inputEmail(Scanner scanner, String msg) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        while (true) {
            String email = inputNonEmpty(scanner, msg);
            if (email.matches(regex)) return email;
            System.err.println("Lỗi: Email sai cấu trúc định dạng!");
        }
    }

    private void printStudentTable(List<Student> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Không có dữ liệu học viên nào hiển thị.");
            return;
        }
        System.out.println("+" + "-".repeat(5) + "+" + "-".repeat(25) + "+" + "-".repeat(13) + "+" + "-".repeat(25) + "+" + "-".repeat(10) + "+" + "-".repeat(15) + "+");
        System.out.printf("| %-3s | %-23s | %-11s | %-23s | %-8s | %-13s |\n", "ID", "HỌ TÊN HỌC VIÊN", "NGÀY SINH", "EMAIL", "GIỚI TÍNH", "SỐ ĐIỆN THOẠI");
        System.out.println("+" + "-".repeat(5) + "+" + "-".repeat(25) + "+" + "-".repeat(13) + "+" + "-".repeat(25) + "+" + "-".repeat(10) + "+" + "-".repeat(15) + "+");

        for (Student s : list) {
            String sexStr = s.isSex() ? "Nam" : "Nữ";
            String dobStr = (s.getDob() != null) ? sdf.format(s.getDob()) : "N/A";
            System.out.printf("| %-3d | %-23s | %-11s | %-23s | %-8s | %-13s |\n",
                    s.getId(), s.getName(), dobStr, s.getEmail(), sexStr, s.getPhone() != null ? s.getPhone() : "N/A");
        }
        System.out.println("+" + "-".repeat(5) + "+" + "-".repeat(25) + "+" + "-".repeat(13) + "+" + "-".repeat(25) + "+" + "-".repeat(10) + "+" + "-".repeat(15) + "+");
    }
}
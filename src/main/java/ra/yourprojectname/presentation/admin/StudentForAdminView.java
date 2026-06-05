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
    private final int pageSize = 3; // Định cấu hình cố định 3 bản ghi một trang hiển thị

    public StudentForAdminView(Scanner scanner) {
        boolean flag = true;
        int choose;
        String menuError = "";

        while (flag) {
            System.out.println("\n=============== QUẢN LÝ HỌC VIÊN =============");
            System.out.println("1. Hiển thị danh sách học viên (Phân trang)");
            System.out.println("2. Thêm mới học viên");
            System.out.println("3. Chỉnh sửa thông tin học viên");
            System.out.println("4. Xóa học viên (xác nhận trước khi xoá)");
            System.out.println("5. Tìm kiếm theo tên, email hoặc id (Phân trang)");
            System.out.println("6. Sắp xếp theo tên hoặc id (Phân trang)");
            System.out.println("7. Quay về menu chính");
            System.out.println("==============================================");
            if (!menuError.isEmpty()) {
                System.err.println(menuError);
            }
            while (true) {
                System.out.print("Nhập lựa chọn: ");
                try {
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("Bạn phải nhập vào là số!");
                }
            }
            switch (choose) {
                case 1:
                    menuError = "";
                    displayStudentWithPagination(scanner);
                    break;
                case 2:
                    menuError = "";
                    insertStudent(scanner);
                    break;
                case 3:
                    menuError = "";
                    updateStudent(scanner);
                    break;
                case 4:
                    menuError = "";
                    deleteStudent(scanner);
                    break;
                case 5:
                    menuError = "";
                    searchStudentWithPagination(scanner);
                    break;
                case 6:
                    menuError = "";
                    sortStudentWithPagination(scanner);
                    break;
                case 7:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 7");
            }
        }
    }

    //HIỂN THỊ DANH SÁCH MẶC ĐỊNH PHÂN TRANG ---
    public void displayStudentWithPagination(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalPages = studentService.getTotalPages(pageSize);
            List<Student> list = studentService.getStudentsByPage(currentPage, pageSize);

            System.out.println("\n=== DANH SÁCH HỌC VIÊN HỆ THỐNG ===");
            System.out.printf("TRANG %d / %d\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printStudentTable(list);

            if (totalPages > 1) {
                System.out.println("[N]: Trang kế  |  [P]: Trang trước  |  [E]: Thoát");
                System.out.print("Chọn hành động: ");
                String action = scanner.nextLine().trim().toUpperCase();
                if (action.equalsIgnoreCase("N") && currentPage < totalPages) {
                    currentPage++;
                } else if (action.equalsIgnoreCase("P") && currentPage > 1) {
                    currentPage--;
                } else if (action.equalsIgnoreCase("E")) {
                    break;
                } else {
                    System.err.println("Lệnh điều hướng không phù hợp!");
                }
            } else {
                break;
            }
        }
    }

    // TÌM KIẾM ĐƯỢC PHÂN TRANG
    public void searchStudentWithPagination(Scanner scanner) {
        System.out.println("\n--- TÌM KIẾM HỌC VIÊN TƯƠNG ĐỐI ---");
        System.out.print("Nhập từ khóa tìm kiếm (Tên / Email / Mã số ID): ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) {
            System.err.println("Từ khóa không được bỏ trống!");
            return;
        }

        int currentPage = 1;
        while (true) {
            int totalPages = studentService.getSearchTotalPages(keyword, pageSize);
            List<Student> list = studentService.searchStudentsByPage(keyword, currentPage, pageSize);

            System.out.println("\nKẾT QUẢ TÌM KIẾM CHO TỪ KHÓA [" + keyword + "]");
            System.out.printf("TRANG %d / %d\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printStudentTable(list);

            if (totalPages > 1) {
                System.out.println("[N]: Trang kế  |  [P]: Trang trước  |  [E]: Thoát");
                System.out.print("Chọn hành động: ");
                String action = scanner.nextLine().trim().toUpperCase();
                if (action.equalsIgnoreCase("N") && currentPage < totalPages) {
                    currentPage++;
                } else if (action.equalsIgnoreCase("P") && currentPage > 1) {
                    currentPage--;
                } else if (action.equalsIgnoreCase("E")) {
                    break;
                } else {
                    System.err.println("Lệnh điều hướng không phù hợp!");
                }
            } else {
                break;
            }
        }
    }

    // SẮP XẾP ĐƯỢC PHÂN TRANG
    public void sortStudentWithPagination(Scanner scanner) {
        System.out.println("\n--- SẮP XẾP DANH SÁCH HỌC VIÊN ---");
        System.out.println("1. Sắp xếp theo Tên học viên");
        System.out.println("2. Sắp xếp theo Mã ID học viên");
        System.out.print("Lựa chọn tiêu chí (1 hoặc 2): ");
        String column = scanner.nextLine().trim().equals("2") ? "id" : "name";

        System.out.println("1. Tăng dần (A-Z)");
        System.out.println("2. Giảm dần (Z-A)");
        System.out.print("Chọn chiều sắp xếp (1 hoặc 2): ");
        String direction = scanner.nextLine().trim().equals("2") ? "DESC" : "ASC";

        int currentPage = 1;
        while (true) {
            int totalPages = studentService.getTotalPages(pageSize);
            List<Student> list = studentService.getStudentsSortedByPage(column, direction, currentPage, pageSize);

            System.out.println("\nDANH SÁCH HỌC VIÊN SAU KHI SẮP XẾP");
            System.out.printf("TRANG %d / %d\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printStudentTable(list);

            if (totalPages > 1) {
                System.out.println("[N]: Trang kế  |  [P]: Trang trước  |  [E]: Thoát");
                System.out.print("Chọn hành động: ");
                String action = scanner.nextLine().trim().toUpperCase();
                if (action.equalsIgnoreCase("N") && currentPage < totalPages) {
                    currentPage++;
                } else if (action.equalsIgnoreCase("P") && currentPage > 1) {
                    currentPage--;
                } else if (action.equalsIgnoreCase("E")) {
                    break;
                } else {
                    System.err.println("Lệnh điều hướng không phù hợp!");
                }
            } else {
                break;
            }
        }
    }

    public void insertStudent(Scanner scanner) {
        System.out.println("\n---- THÊM MỚI HỌC VIÊN ----");
        String name = inputNonEmpty(scanner, "Nhập họ tên học viên: ");
        Date dob = inputDate(scanner, "Nhập ngày sinh (Định dạng: DD-MM-YYYY): ");
        String email = inputEmail(scanner, "Nhập địa chỉ Email: ");
        boolean sex = inputSex(scanner);
        System.out.print("Nhập số điện thoại (Ấn Enter nếu muốn bỏ qua): ");
        String phone = scanner.nextLine().trim();
        if (phone.isEmpty()) phone = null;
        String password = inputNonEmpty(scanner, "Thiết lập mật khẩu đăng nhập: ");

        Student student = new Student(0, name, dob, email, sex, phone, password, new Date());
        try {
            if (studentService.insertStudent(student)) {
                System.out.println("Thêm mới học viên thành công!");
            } else {
                System.err.println("Thêm mới học viên thất bại.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }

    public void updateStudent(Scanner scanner) {
        System.out.println("\n--- CHỈNH SỬA THÔNG TIN HỌC VIÊN ---");
        System.out.print("Nhập mã ID học viên cần chỉnh sửa: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("Lỗi: ID bắt buộc phải là số nguyên!");
            return;
        }

        List<Student> all = studentService.getALLStudents();
        Student editStudent = null;
        for (Student s : all) {
            if (s.getId() == id) {
                editStudent = s;
                break;
            }
        }

        if (editStudent == null) {
            System.err.println("Không tìm thấy thông tin học viên nào ứng với mã ID = " + id);
            return;
        }

        Student cloneStudent = new Student(
                editStudent.getId(), editStudent.getName(), editStudent.getDob(), editStudent.getEmail(),
                editStudent.isSex(), editStudent.getPhone(), editStudent.getPassword(), editStudent.getCreate_at()
        );

        boolean subFlag = true;
        boolean isSave = false;

        while (subFlag) {
            System.out.println("\nHọc viên đang chỉnh sửa: " + cloneStudent.getName());
            System.out.println("1. Sửa họ tên");
            System.out.println("2. Sửa ngày sinh");
            System.out.println("3. Sửa Email");
            System.out.println("4. Sửa giới tính");
            System.out.println("5. Sửa số điện thoại");
            System.out.println("6. Đổi mật khẩu");
            System.out.println("7. Lưu lại thay đổi");
            System.out.println("8. Hủy bỏ (Thoát không lưu)");
            System.out.print("Chọn cấu phần thuộc tính cần sửa (1-8): ");

            int subChoose;
            try {
                subChoose = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.err.println("Vui lòng nhập số!");
                continue;
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
                    System.out.print("Nhập số điện thoại mới: ");
                    String p = scanner.nextLine().trim();
                    cloneStudent.setPhone(p.isEmpty() ? null : p);
                    break;
                case 6:
                    String rawPass = inputNonEmpty(scanner, "Nhập mật khẩu mới: ");
                    cloneStudent.setPassword(PasswordHasher.hashPassword(rawPass));
                    break;
                case 7:
                    isSave = true;
                    subFlag = false;
                    break;
                case 8:
                    isSave = false;
                    subFlag = false;
                    break;
                default:
                    System.out.println("Lựa chọn thuộc phạm vi từ 1 đến 8!");
            }
        }

        if (isSave) {
            try {
                if (studentService.updateStudent(cloneStudent)) {
                    System.out.println("Cập nhật thông tin học viên thành công!");
                } else {
                    System.err.println("Thao tác lưu thất bại.");
                }
            } catch (Exception e) {
                System.err.println("Lỗi: " + e.getMessage());
            }
        } else {
            System.out.println("Đã hủy bỏ mọi sửa đổi.");
        }
    }

    public void deleteStudent(Scanner scanner) {
        System.out.println("\n--- XÓA HỌC VIÊN ---");
        System.out.print("Nhập mã ID học viên cần xóa: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("ID phải là định dạng số!");
            return;
        }

        System.out.print("Cảnh báo: Bạn chắc chắn muốn xóa học viên này khỏi hệ thống? (Gõ 'Y' để đồng ý): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            try {
                if (studentService.deleteStudent(id)) {
                    System.out.println("Xóa dữ liệu học viên thành công!");
                } else {
                    System.err.println("Không tìm thấy học viên mã ID phù hợp.");
                }
            } catch (Exception e) {
                System.err.println("Lỗi: " + e.getMessage());
            }
        } else {
            System.out.println("➡Thao tác xóa đã được hủy bỏ.");
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

    private boolean inputSex(Scanner scanner) {
        while (true) {
            System.out.print("Nhập giới tính học viên (1: Nam / 0: Nữ): ");
            String choice = scanner.nextLine().trim();
            if (choice.equalsIgnoreCase("1")) {
                return true;
            }
            if (choice.equalsIgnoreCase("0")) {
                return false;
            }
            System.err.println("Lỗi: Hãy chọn số 1 hoặc số 0!");
        }
    }

    private Date inputDate(Scanner scanner, String msg) {
        Date date;
        while (true) {
            System.out.print(msg);
            try {
                date = sdf.parse(scanner.nextLine().trim());
                break;
            } catch (Exception e) {
                System.err.println("Lỗi: Định dạng ngày bắt buộc là DD-MM-YYYY (Ví dụ: 20-11-2003)!");
            }
        }
        return date;
    }

    private String inputEmail(Scanner scanner, String msg) {
        String email;
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        while (true) {
            System.out.print(msg);
            email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.err.println("Lỗi: Email không được trống!");
                continue;
            }
            if (!email.matches(regex)) {
                System.err.println("Lỗi: Email sai cấu trúc định dạng!");
                continue;
            }
            break;
        }
        return email;
    }

    private String inputNonEmpty(Scanner scanner, String msg) {
        String input;
        while (true) {
            System.out.print(msg);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.err.println("Lỗi: Vui lòng nhập dữ liệu, không bỏ trống!");
                continue;
            }
            break;
        }
        return input;
    }
}
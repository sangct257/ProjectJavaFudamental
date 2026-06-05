package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.StudentService;
import ra.yourprojectname.business.impl.StudentServiceImpl;
import ra.yourprojectname.model.Student;
import ra.yourprojectname.until.PasswordHasher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class StudentForAdminView {
    private final StudentService studentService = new StudentServiceImpl();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public StudentForAdminView(Scanner scanner) {
        boolean flag = true;
        int choose;
        String menuError = "";

        while (flag) {
            System.out.println("=============== QUẢN LÝ HỌC VIÊN =============");
            System.out.println("1. Hiển thị danh sách học viên");
            System.out.println("2. Thêm mới học viên");
            System.out.println("3. Chỉnh sửa thông tin học viên (hiển thị menu chọn thuộc tính cần sửa)");
            System.out.println("4. Xóa học viên (xác nhận trước khi xoá)");
            System.out.println("5. Tìm kiếm theo tên,email hoặc id (tương đối)");
            System.out.println("6. Sắp xếp theo tên hoặc id (tăng/giảm dần)");
            System.out.println("7. Quay về menu chính");
            System.out.println("================================");
            if (!menuError.isEmpty()) {
                System.err.println(menuError);
            }
            while (true) {
                System.out.println("Nhập lựa chọn: ");
                try {
                    choose = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (Exception e) {
                    System.err.println("Bạn phải nhập vào là số");
                }
            }
            switch (choose) {
                case 1:
                    menuError = "";
                    displayStudent(studentService.getALLStudents());
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
                    searchStudent(scanner);
                    break;
                case 6:
                    menuError = "";
                    sortStudent(scanner);
                    break;
                case 7:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 7");
            }
        }
    }

    private void displayStudent(List<Student> list) {
        if (list.isEmpty()) {
            System.out.println("Danh sách học viên đang trống.");
            return;
        }
        System.out.println("\n+" + "-".repeat(5) + "+" + "-".repeat(25) + "+" + "-".repeat(13) + "+" + "-".repeat(25) + "+" + "-".repeat(10) + "+" + "-".repeat(15) + "+");
        System.out.printf("| %-3s | %-23s | %-11s | %-23s | %-8s | %-13s |\n", "ID", "HỌ TÊN HỌC VIÊN", "NGÀY SINH", "EMAIL", "GIỚI TÍNH", "SỐ ĐIỆN THOẠI");
        System.out.println("+" + "-".repeat(5) + "+" + "-".repeat(25) + "+" + "-".repeat(13) + "+" + "-".repeat(25) + "+" + "-".repeat(10) + "+" + "-".repeat(15) + "+");

        for (Student s : list) {
            String sexStr = s.isSex() ? "Nam" : "Nữ";
            String dobStr = sdf.format(s.getDob());
            System.out.printf("| %-3d | %-23s | %-11s | %-23s | %-8s | %-13s |\n",
                    s.getId(), s.getName(), dobStr, s.getEmail(), sexStr, s.getPhone() != null ? s.getPhone() : "N/A");
        }
        System.out.println("+" + "-".repeat(5) + "+" + "-".repeat(25) + "+" + "-".repeat(13) + "+" + "-".repeat(25) + "+" + "-".repeat(10) + "+" + "-".repeat(15) + "+");
    }

    public void insertStudent(Scanner scanner) {
        System.out.println("----THÊM MỚI HỌC VIÊN----");
        String name = inputNonEmpty(scanner, "Nhập họ tên học viên: ");
        Date dob = inputDate(scanner, "Nhập ngày sinh (Định dạng: DD-MM-YYYY): ");
        String email = inputEmail(scanner, "Nhập địa chỉ Email: ");
        boolean sex = inputSex(scanner);
        System.out.print("Nhập số điện thoại (Ấn Enter nếu muốn bỏ qua): ");
        String phone = scanner.nextLine().trim();
        if (phone.isEmpty()) {
            phone = null;
        }
        String password = inputNonEmpty(scanner, "Thiết lập mật khẩu đăng nhập: ");
        Student student = new Student(0, name, dob, email, sex, phone, password, new Date());
        try {
            if (studentService.insertStudent(student)) {
                System.out.println("Thêm mới học viên thành công!");
            } else {
                System.err.println("Thêm mới học viên thất bại.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void updateStudent(Scanner scanner) {
        System.out.println("--- CHỈNH SỬA THÔNG TIN HỌC VIÊN ---");
        System.out.print("Nhập mã ID học viên cần chỉnh sửa: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            System.err.println("Lỗi: ID bắt buộc phải là số nguyên!");
            return;
        }

        List<Student> all = new ArrayList<>();
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
            System.out.println("Học viên đang chỉnh sửa: " + cloneStudent.getName());
            System.out.println("1. Sửa họ tên");
            System.out.println("2. Sửa ngày sinh");
            System.out.println("3. Sửa Email");
            System.out.println("4. Sửa giới tính");
            System.out.println("5. Sửa số điện thoại");
            System.out.println("6. Đổi mật khẩu");
            System.out.println("7. Lưu lại thay đổi");
            System.out.println("8. Hủy bỏ (Thoát không lưu)");
            System.out.print("Chọn cấu phần thuộc tính cần sửa: ");

            int subChoose;
            try {
                subChoose = Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
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
            System.out.println("Đã hủy bỏ mọi sửa đổi. Thông tin học viên được giữ nguyên vẹn!");
        }
    }

    public void deleteStudent(Scanner scanner) {
        System.out.println("--- XÓA HỌC VIÊN ---");
        System.out.print("Nhập mã ID học viên cần xóa: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            System.err.println("ID phải là định dạng số!");
            return;
        }

        System.out.print("Cảnh báo: Bạn chắc chắn muốn xóa học viên này khỏi hệ thống? (Gõ 'Y' để đồng ý, phím khác để hủy): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            try {
                if (studentService.deleteStudent(id)) {
                    System.out.println("Xóa dữ liệu học viên thành công!");
                } else {
                    System.err.println("Không tìm thấy học viên nào khớp mã ID để tiến hành xóa.");
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("Thao tác xóa đã được hủy bỏ.");
        }
    }

    public void searchStudent(Scanner scanner){
        System.out.println("--- TÌM KIẾM HỌC VIÊN TƯƠNG ĐỐI ---");
        System.out.print("Nhập từ khóa tìm kiếm (Tên / Email / Mã số ID): ");
        String keyword = scanner.nextLine().trim();

        List<Student> results = studentService.searchStudents(keyword);
        System.out.println("Kết quả tìm kiếm tương đối ứng với từ khóa [" + keyword + "]:");
        displayStudent(results);
    }

    public void sortStudent(Scanner scanner){
        System.out.println("--- SẮP XẾP DANH SÁCH HỌC VIÊN ---");
        System.out.println("1. Sắp xếp theo Tên học viên");
        System.out.println("2. Sắp xếp theo Mã ID học viên");
        System.out.print("Lựa chọn tiêu chí (1 hoặc 2): ");
        String column = scanner.nextLine().trim().equals("2") ? "id" : "name";

        System.out.println("1. Tăng dần (A-Z / Thấp đến Cao)");
        System.out.println("2. Giảm dần (Z-A / Cao đến Thấp)");
        System.out.print("Chọn chiều sắp xếp (1 hoặc 2): ");
        String direction = scanner.nextLine().trim().equals("2") ? "DESC" : "ASC";

        List<Student> listSorted = studentService.getStudentSorted(column, direction);
        System.out.println("Kết quả danh sách học viên sau sắp xếp:");
        displayStudent(listSorted);
    }

    private boolean inputSex(Scanner scanner) {
        while (true) {
            System.out.print("Nhập giới tính học viên (1: Nam / 0: Nữ): ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                return true;
            }
            if (choice.equals("0")) {
                return false;
            }
            System.err.println("Lỗi: Giới tính chỉ chấp nhận lựa chọn số 1 hoặc số 0!");
        }
    }

    private Date inputDate(Scanner scanner, String msg) {
        Date date;
        while (true) {
            System.out.println(msg);
            try {
                date = sdf.parse(scanner.nextLine().trim());
                break;
            } catch (Exception e) {
                System.err.println("Lỗi: Sai định dạng ngày! Vui lòng nhập chuẩn Ngày-Tháng-Năm (Ví dụ: 25-12-2005)");
            }
        }
        return date;
    }

    private String inputEmail(Scanner scanner, String msg) {
        String email;
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        while (true) {
            System.out.println(msg);
            email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.err.println("Lỗi: Email không được phép để trống!");
                continue;
            }

            if (!email.matches(regex)) {
                System.err.println("Lỗi: Email sai định dạng (Ví dụ đúng: student@gmail.com)!");
                continue;
            }
            break;
        }
        return email;
    }

    private String inputNonEmpty(Scanner scanner, String msg) {
        String input;
        while (true) {
            System.out.println(msg);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.err.println("Lỗi: Trường dữ liệu này không cho phép để trống!");
                continue;
            }
            break;
        }
        return input;
    }

}

package ra.yourprojectname.presentation.login;

import ra.yourprojectname.business.AdminService;
import ra.yourprojectname.business.StudentService;
import ra.yourprojectname.business.impl.AdminServiceImpl;
import ra.yourprojectname.business.impl.StudentServiceImpl;
import ra.yourprojectname.presentation.admin.AdminView;
import ra.yourprojectname.presentation.student.StudentView;

import java.util.Scanner;

public class LoginView {

    public void loginAdmin(Scanner scanner){
        System.out.println("--- ĐĂNG NHẬP HỆ THỐNG ADMIN ---");
        String username = "";
        while (true){
            System.out.print("Nhập username: ");
            username = scanner.nextLine().trim();

            if (username.isEmpty()){
                System.err.println("Lỗi: Username không được để trống!");
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }
            break;
        }
        String password = "";

        while (true){
            System.out.print("Nhập password: ");
            password = scanner.nextLine().trim();

            if (password.isEmpty()){
                System.err.println("Lỗi: Password không được để trống!");
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }
            break;
        }

        AdminService adminService = new AdminServiceImpl();

        boolean isLoginSuccess = adminService.login(username, password);
        if(isLoginSuccess){
            //Đăng nhập thành công
            new AdminView(scanner);
        }else{
            //Đăng nhập không thành công
            System.err.println("Sai username hoặc password");
        }
    }

    public void loginStudent(Scanner scanner) {
        System.out.println("--- ĐĂNG NHẬP HỆ THỐNG HỌC VIÊN ---");

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        String email = "";

        while (true) {
            System.out.print("Nhập email: ");
            email = scanner.nextLine().trim();

            if (email.isEmpty()) {
                System.err.println("Lỗi: Email không được để trống!");
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }
            if (!email.matches(emailRegex)) {
                System.err.println("Lỗi: Email sai định dạng cấu trúc!");
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }
            break;
        }

        String password = "";
        while (true) {
            System.out.print("Nhập password: ");
            password = scanner.nextLine().trim();

            if (password.isEmpty()) {
                System.err.println("Lỗi: Mật khẩu không được để trống!");
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }
            break;
        }

        StudentService studentService = new StudentServiceImpl();
        boolean isLoginSuccess = studentService.login(email, password);

        if (isLoginSuccess) {
            new StudentView(scanner, email);
        } else {
            System.err.println("Sai email hoặc password");
        }
    }

    public void showLoginInfo() {
        Scanner scanner = new Scanner(System.in);
        int choose;

        while (true){
            System.out.println("========== HỆ THỐNG QUẢN LÝ ĐÀO TẠO ===========");
            System.out.println("""
            1. Đăng nhập với tư cách quản trị viên
            2. Đăng nhập với tư cách học viên
            3. Thoát """);
            System.out.println("=======================================");
            while (true){
                System.out.println("Nhập lựa chọn: ");
                try{
                    choose = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (Exception e) {
                    System.out.println("Bạn phải nhập vào là số");
                }
            }

            switch (choose){
                case 1:
                    loginAdmin(scanner);
                    break;
                case  2:
                    loginStudent(scanner);
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Bạn chỉ được chọn 1 đến 3");
            }
        }
    }
}

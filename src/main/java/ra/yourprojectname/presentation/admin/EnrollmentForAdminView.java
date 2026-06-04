package ra.yourprojectname.presentation.admin;

import java.util.Scanner;

public class EnrollmentForAdminView {
    public EnrollmentForAdminView(Scanner scanner) {
        boolean flag = true;
        int choose;
        while (flag){
            System.out.println("=============== QUẢN LÝ ĐĂNG KÝ KHOÁ HỌC =============");
            System.out.println("1. Hiển thị học viên theo từng");
            System.out.println("2. Thêm học viên vào khoá học");
            System.out.println("3. Xóa học viên khỏi khoá học (xác nhận trước khi xoá)");
            System.out.println("4. Quay về menu chính");
            System.out.println("================================");
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
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 4");
            }
        }
    }
}

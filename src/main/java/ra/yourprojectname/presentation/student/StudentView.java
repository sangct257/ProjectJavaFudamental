package ra.yourprojectname.presentation.student;

import java.util.Scanner;

public class StudentView {

    public StudentView(Scanner scanner) {
        int choose = 0;
        boolean flag = true;
        while (flag) {
            System.out.println("============= MENU STUDENT ==========");
            System.out.println("1. Xem danh sách khoá học");
            System.out.println("2. Đăng ký khoá học");
            System.out.println("3. Xem khoá học đã đăng ký");
            System.out.println("4. Huỷ đăng ký (nếu chưa bắt đầu)");
            System.out.println("5. Dổi mật khẩu");
            System.out.println("6. Đăng xuất");
            System.out.println("===================================");
            while (true){
                try {
                    System.out.println("Mời chọn: ");
                    choose = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Bạn phải nhập là số");
                }
            }

            switch (choose) {
                case 1:

                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 6");
            }
        }
    }
}

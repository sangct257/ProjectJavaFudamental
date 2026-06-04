package ra.yourprojectname.presentation.admin;

import java.util.Scanner;

public class AdminView {

    public AdminView(Scanner scanner) {
        int choose = 0;
        boolean flag = true;
        while (flag) {
            System.out.println("============= MENU ADMIN ==========");
            System.out.println("1. Quản lý khóa học");
            System.out.println("2. Quản lý học viên");
            System.out.println("3. Quản lý đăng ký khóa học");
            System.out.println("4. Thống kê học viên theo khóa học");
            System.out.println("5. Đăng xuất");
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
                    new CourseForAdminView(scanner);
                    break;
                case 2:
                    new StudentForAdminView(scanner);
                    break;
                case 3:
                    new EnrollmentForAdminView(scanner);
                    break;
                case 4:
                    new StatisticalForAdminView(scanner);
                    break;
                case 5:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 5");
            }
        }

    }
}

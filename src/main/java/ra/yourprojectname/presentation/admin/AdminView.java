package ra.yourprojectname.presentation.admin;

import java.util.Scanner;

public class AdminView {

    public AdminView(Scanner scanner) {
        int choose = 0;
        boolean flag = true;
        while (flag) {
            System.out.println("\n============= MENU ADMIN ==========");
            System.out.println("""
            1. Quản lý khóa học
            2. Quản lý học viên
            3. Quản lý đăng ký khóa học
            4. Thống kê học viên theo khóa học
            5. Đăng xuất """);
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
                    new CourseView(scanner);
                    break;
                case 2:
                    new StudentView(scanner);
                    break;
                case 3:
                    new EnrollmentView(scanner);
                    break;
                case 4:
                    new StatisticalView(scanner);
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

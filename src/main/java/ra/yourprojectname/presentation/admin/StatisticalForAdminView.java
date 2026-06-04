package ra.yourprojectname.presentation.admin;

import java.util.Scanner;

public class StatisticalForAdminView {
    public StatisticalForAdminView(Scanner scanner) {
        boolean flag = true;
        int choose;
        while (flag){
            System.out.println("=============== MENU THỐNG KÊ =============");
            System.out.println("1. Thống kê tổng số lượng khoá học và học viên");
            System.out.println("2. Thống kê học viên theo từng khoá học");
            System.out.println("3. Top 5 khoá học đông học viên nhất");
            System.out.println("4. Liệt kê khoá học trên 10 học viên");
            System.out.println("5. Quay về menu chính");
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

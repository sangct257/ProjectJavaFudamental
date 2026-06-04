package ra.yourprojectname.presentation.admin;

import java.util.Scanner;

public class StudentForAdminView {
    public StudentForAdminView(Scanner scanner) {
        boolean flag = true;
        int choose;
        while (flag){
            System.out.println("=============== QUẢN LÝ HỌC VIÊN =============");
            System.out.println("1. Hiển thị danh sách học viên");
            System.out.println("2. Thêm mới học viên");
            System.out.println("3. Chỉnh sửa thông tin học viên (hiển thị menu chọn thuộc tính cần sửa)");
            System.out.println("4. Xóa học viên (xác nhận trước khi xoá)");
            System.out.println("5. Tìm kiếm theo tên,email hoặc id (tương đối)");
            System.out.println("6. Sắp xếp theo tên hoặc id (tăng/giảm dần)");
            System.out.println("7. Quay về menu chính");
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
                    break;
                case 6:
                    break;
                case 7:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 7");
            }
        }
    }
}

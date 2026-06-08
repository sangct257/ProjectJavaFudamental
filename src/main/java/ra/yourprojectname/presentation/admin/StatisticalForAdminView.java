package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.StatisticalService;
import ra.yourprojectname.business.impl.StatisticalServiceImpl;

import java.util.Map;
import java.util.Scanner;

public class StatisticalForAdminView {
    private final StatisticalService statisticalService = new StatisticalServiceImpl();
    private final int pageSize = 5;

    public StatisticalForAdminView(Scanner scanner) {
        boolean flag = true;
        int choose;
        while (flag) {
            System.out.println("\n===================== MENU THỐNG KÊ =====================");
            System.out.println("1. Thống kê tổng số lượng khoá học và học viên");
            System.out.println("2. Thống kê số lượng học viên theo từng khoá học");
            System.out.println("3. Top 5 khoá học đông học viên nhất");
            System.out.println("4. Liệt kê khoá học trên 10 học viên");
            System.out.println("5. Quay về menu chính");
            System.out.println("==========================================================");
            while (true) {
                System.out.print("Nhập lựa chọn: ");
                try {
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (Exception e) {
                    System.out.println("⚠️ Lỗi: Bạn phải nhập vào là số nguyên!");
                }
            }
            switch (choose) {
                case 1:
                    showGeneralStatistics();
                    break;
                case 2:
                    showStudentCountByCourse(scanner);
                    break;
                case 3:
                    showTop5PopularCourses();
                    break;
                case 4:
                    showCoursesWithMoreThan10Students();
                    break;
                case 5:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 5");
            }
        }
    }

    //Thống kê tổng số lượng khoá học và học viên
    private void showGeneralStatistics() {
        System.out.println("\n--- THỐNG KÊ TỔNG QUAN HỆ THỐNG ---");
        int totalCourses = statisticalService.getTotalCoursesCount();
        int totalStudents = statisticalService.getTotalStudentsCount();

        System.out.println("+" + "-".repeat(25) + "+" + "-".repeat(25) + "+");
        System.out.printf("| %-23s | %-23s |\n", "TỔNG SỐ KHÓA HỌC", "TỔNG SỐ HỌC VIÊN");
        System.out.println("+" + "-".repeat(25) + "+" + "-".repeat(25) + "+");
        System.out.printf("| %-23d | %-23d |\n", totalCourses, totalStudents);
        System.out.println("+" + "-".repeat(25) + "+" + "-".repeat(25) + "+");
    }

    // Thống kê học viên theo từng khoá học (Có áp dụng phân trang kẻo danh sách lớp quá dài)
    private void showStudentCountByCourse(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            // Hàm này trả về Map<String, Integer> chứa dữ liệu: Tên khóa học -> Số học viên tương ứng
            Map<String, Integer> stats = statisticalService.getStudentCountByCourse(currentPage, pageSize);
            int totalPages = (int) Math.ceil((double) statisticalService.getTotalCoursesCount() / pageSize);

            System.out.printf("\n--- SỐ LƯỢNG HỌC VIÊN THEO KHÓA HỌC (TRANG %d / %d) ---\n", currentPage, totalPages == 0 ? 1 : totalPages);

            if (stats == null || stats.isEmpty()) {
                System.out.println("Không có dữ liệu thống kê.");
            } else {
                System.out.println("+" + "-".repeat(45) + "+" + "-".repeat(20) + "+");
                System.out.printf("| %-43s | %-18s |\n", "TÊN KHÓA HỌC", "SỐ HỌC VIÊN ĐÃ ĐĂNG KÝ");
                System.out.println("+" + "-".repeat(45) + "+" + "-".repeat(20) + "+");
                for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                    System.out.printf("| %-43s | %-22d |\n", entry.getKey(), entry.getValue());
                }
                System.out.println("+" + "-".repeat(45) + "+" + "-".repeat(20) + "+");
            }

            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [E]: Thoát thống kê: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("E")) break;
            if (input.equals("N")) {
                if (currentPage < totalPages) currentPage++;
                else System.out.println("Đã ở trang cuối cùng.");
            }
            if (input.equals("P")) {
                if (currentPage > 1) currentPage--;
                else System.out.println("Đã ở trang đầu tiên.");
            }
        }
    }

    // Top 5 khoá học đông học viên nhất
    private void showTop5PopularCourses() {
        System.out.println("\n--- TOP 5 KHÓA HỌC ĐÔNG HỌC VIÊN NHẤT ---");
        Map<String, Integer> topCourses = statisticalService.getTop5PopularCourses();

        if (topCourses == null || topCourses.isEmpty()) {
            System.out.println("Hệ thống hiện tại chưa có học viên nào đăng ký học.");
            return;
        }

        System.out.println("+" + "-".repeat(8) + "+" + "-".repeat(45) + "+" + "-".repeat(20) + "+");
        System.out.printf("| %-6s | %-43s | %-18s |\n", "HẠNG", "TÊN KHÓA HỌC", "SỐ HỌC VIÊN");
        System.out.println("+" + "-".repeat(8) + "+" + "-".repeat(45) + "+" + "-".repeat(20) + "+");
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topCourses.entrySet()) {
            System.out.printf("| Hạng %-2d | %-43s | %-22d |\n", rank++, entry.getKey(), entry.getValue());
        }
        System.out.println("+" + "-".repeat(8) + "+" + "-".repeat(45) + "+" + "-".repeat(20) + "+");
    }

    // 4. Liệt kê khoá học trên 10 học viên
    private void showCoursesWithMoreThan10Students() {
        System.out.println("\n--- DANH SÁCH KHÓA HỌC CÓ TRÊN 10 HỌC VIÊN ---");
        Map<String, Integer> qualifiedCourses = statisticalService.getCoursesWithMoreThan10Students();

        if (qualifiedCourses == null || qualifiedCourses.isEmpty()) {
            System.out.println("Không có khóa học nào đạt tiêu chí (Đều dưới hoặc bằng 10 học viên).");
            return;
        }

        System.out.println("+" + "-".repeat(45) + "+" + "-".repeat(20) + "+");
        System.out.printf("| %-43s | %-18s |\n", "TÊN KHÓA HỌC", "SỐ HỌC VIÊN");
        System.out.println("+" + "-".repeat(45) + "+" + "-".repeat(20) + "+");
        for (Map.Entry<String, Integer> entry : qualifiedCourses.entrySet()) {
            System.out.printf("| %-43s | %-22d |\n", entry.getKey(), entry.getValue());
        }
        System.out.println("+" + "-".repeat(45) + "+" + "-".repeat(20) + "+");
    }
}
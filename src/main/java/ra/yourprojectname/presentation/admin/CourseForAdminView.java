package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.CourseService;
import ra.yourprojectname.business.impl.CourseServiceImpl;
import ra.yourprojectname.model.Course;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class CourseForAdminView {
    private final CourseService courseService = new CourseServiceImpl();
    private final int pageSize = 3; // Quản lý kích thước trang tập trung tại một nơi

    public CourseForAdminView(Scanner scanner) {
        while (true) {
            System.out.println("\n=============== QUẢN LÝ KHÓA HỌC =============");
            System.out.println("1. Hiển thị danh sách khóa học\n2. Thêm mới khóa học\n3. Chỉnh sửa thông tin\n4. Xóa khóa học\n5. Tìm kiếm theo tên\n6. Sắp xếp danh sách\n7. Quay về menu chính");
            System.out.println("==============================================");
            int choose = inputInt(scanner, "Nhập lựa chọn: ");

            switch (choose) {
                case 1: displayCourses(scanner); break;
                case 2: insertCourse(scanner); break;
                case 3: updateCourse(scanner); break;
                case 4: deleteCourse(scanner); break;
                case 5: searchCourse(scanner); break;
                case 6: sortCourse(scanner); break;
                case 7: return;
                default: System.out.println("Vui lòng chọn từ 1 đến 7!");
            }
        }
    }

    // ---HIỂN THỊ DANH SÁCH (PHÂN TRANG)---
    public void displayCourses(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalPages = courseService.getTotalPages(pageSize);
            List<Course> list = courseService.getAllCourse(currentPage, pageSize);

            if (checkEmpty(list)) return;

            System.out.printf("\n=== DANH SÁCH KHÓA HỌC (TRANG %d / %d) ===\n", currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTableFormat(list);

            currentPage = handlePagination(scanner, currentPage, totalPages);
            if (currentPage == -1) break;
        }
    }

    // ---TÌM KIẾM THEO TÊN (PHÂN TRANG)---
    public void searchCourse(Scanner scanner) {
        String keyword = inputNonEmpty(scanner, "Nhập từ khóa tên khóa học muốn tìm: ");
        int currentPage = 1;
        while (true) {
            int totalPages = courseService.countCoursesByName(keyword, pageSize);
            List<Course> list = courseService.findCourseByName(keyword, currentPage, pageSize);

            if (checkEmpty(list)) return;

            System.out.printf("\n=== KẾT QUẢ TÌM KIẾM [%s] (TRANG %d / %d) ===\n", keyword, currentPage, totalPages == 0 ? 1 : totalPages);
            printCourseTableFormat(list);

            currentPage = handlePagination(scanner, currentPage, totalPages);
            if (currentPage == -1) break;
        }
    }

    // ---SẮP XẾP DANH SÁCH (PHÂN TRANG) ---
    public void sortCourse(Scanner scanner) {
        String target = inputNonEmpty(scanner, "Chọn tiêu chí (1. Tên | 2. ID): ").equals("2") ? "id" : "name";
        String direction = inputNonEmpty(scanner, "Chiều (1. Tăng dần | 2. Giảm dần): ").equals("2") ? "DESC" : "ASC";

        int currentPage = 1;
        while (true) {
            int totalPages = courseService.getTotalPages(pageSize);
            List<Course> list = courseService.getAllSorted(target, direction, currentPage, pageSize);

            if (checkEmpty(list)) return;

            System.out.printf("\n=== DANH SÁCH SẮP XẾP [%s - %s] (TRANG %d / %d) ===\n", target, direction, currentPage, totalPages);
            printCourseTableFormat(list);

            currentPage = handlePagination(scanner, currentPage, totalPages);
            if (currentPage == -1) break;
        }
    }

    // ---HÀM PHÂN TRANG---
    private int handlePagination(Scanner scanner, int currentPage, int totalPages) {
        if (totalPages <= 1) return -1; // Chỉ có 1 trang thì kết thúc vòng lặp luôn, không hỏi điều hướng

        System.out.print("[P]: Trang trước | [N]: Trang kế | [E]: Thoát. Hành động: ");
        String action = scanner.nextLine().trim().toUpperCase();

        if (action.equals("N") && currentPage < totalPages) return currentPage + 1;
        if (action.equals("P") && currentPage > 1) return currentPage - 1;
        if (action.equals("E")) return -1;

        System.out.println("Lệnh không hợp lệ hoặc không thể chuyển trang!");
        return currentPage;
    }

    // ---THÊM MỚI KHÓA HỌC ---
    public void insertCourse(Scanner scanner) {
        System.out.println("\n--- THÊM MỚI KHÓA HỌC ---");
        String name = inputNonEmpty(scanner, "Nhập tên khoá học: ");
        int duration = inputPositiveInt(scanner, "Nhập thời lượng (giờ): ");
        String instructor = inputNonEmpty(scanner, "Nhập giảng viên phụ trách: ");

        Course course = new Course(0, name, duration, instructor, new Date());
        if (courseService.insertCourse(course)) {
            System.out.println("Thêm mới khóa học thành công!");
        } else {
            System.out.println("Thêm mới thất bại.");
        }
    }

    // ---CHỈNH SỬA THÔNG TIN KHÓA HỌC ---
    public void updateCourse(Scanner scanner) {
        System.out.println("\n--- CHỈNH SỬA THÔNG TIN KHÓA HỌC ---");
        int id = inputInt(scanner, "Nhập ID khóa học cần sửa: ");

        Course editCourse = courseService.getCourseById(id);

        if (editCourse == null) {
            System.err.println("Không tìm thấy khóa học mã ID = " + id);
            return;
        }

        Course cloneCourse = new Course(
                editCourse.getId(),
                editCourse.getName(),
                editCourse.getDuration(),
                editCourse.getInstructor(),
                editCourse.getCreate_at()
        );

        while (true) {
            System.out.printf("\nĐang sửa: %s | 1. Tên | 2. Thời lượng | 3. Giảng viên | 4. Lưu lại | 5. Hủy bỏ\n", cloneCourse.getName());
            int subChoose = inputInt(scanner, "Mời chọn thuộc tính cần sửa: ");

            if (subChoose == 4) {
                if (courseService.updateCourse(cloneCourse)) {
                    System.out.println("Cập nhật thông tin khóa học thành công!");
                } else {
                    System.out.println("Cập nhật thất bại.");
                }
                break;
            }
            if (subChoose == 5) {
                System.out.println("Đã hủy bỏ thao tác chỉnh sửa. Dữ liệu được giữ nguyên!");
                break;
            }

            switch (subChoose) {
                case 1: cloneCourse.setName(inputNonEmpty(scanner, "Nhập tên mới: ")); break;
                case 2: cloneCourse.setDuration(inputPositiveInt(scanner, "Nhập thời lượng mới: ")); break;
                case 3: cloneCourse.setInstructor(inputNonEmpty(scanner, "Nhập giảng viên mới: ")); break;
                default: System.out.println("Vui lòng chọn từ 1 đến 5!");
            }
        }
    }
    // --- XÓA KHÓA HỌC ---
    public void deleteCourse(Scanner scanner) {
        System.out.println("\n--- XÓA KHÓA HỌC HỆ THỐNG ---");
        int id = inputInt(scanner, "Nhập ID khoá học cần xoá: ");

        System.out.print("Bạn có chắc chắn muốn xóa? (Gõ 'Y' để xác nhận): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            if (courseService.deleteCourse(id)) {
                System.out.println("Xóa khóa học thành công!");
            } else {
                System.err.println("Không tìm thấy ID tương ứng.");
            }
        } else {
            System.out.println("Đã hủy thao tác xóa.");
        }
    }

    // ---HÀM NHẬP LIỆU & KIỂM TRA ---
    private boolean checkEmpty(List<Course> list) {
        if (list == null || list.isEmpty()) {
            System.err.println("Hiện tại không có dữ liệu để hiển thị!");
            return true;
        }
        return false;
    }

    private String inputNonEmpty(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.err.println("Lỗi: Không được để trống trường này!");
        }
    }

    private int inputInt(Scanner scanner, String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.err.println("Lỗi: Phải nhập vào một số nguyên hợp lệ!");
            }
        }
    }

    private int inputPositiveInt(Scanner scanner, String message) {
        while (true) {
            int val = inputInt(scanner, message);
            if (val > 0) return val;
            System.err.println("Lỗi: Giá trị nhập vào phải lớn hơn 0!");
        }
    }

    private void printCourseTableFormat(List<Course> list) {
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(15) + "+");
        System.out.printf("| %-4s | %-28s | %-13s | %-23s | %-13s |\n", "ID", "TÊN KHÓA HỌC", "THỜI LƯỢNG", "GIẢNG VIÊN", "NGÀY TẠO");
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(15) + "+");
        for (Course c : list) {
            System.out.printf("| %-4d | %-28s | %-10d giờ | %-23s | %-13s |\n", c.getId(), c.getName(), c.getDuration(), c.getInstructor(), c.getCreate_at().toString());
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(15) + "+");
    }
}
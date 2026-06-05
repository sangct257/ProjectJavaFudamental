package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.CourseService;
import ra.yourprojectname.business.impl.CourseServiceImpl;
import ra.yourprojectname.model.Course;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class CourseForAdminView {
    private final CourseService courseService = new CourseServiceImpl();

    public CourseForAdminView(Scanner scanner) {
        boolean flag = true;
        int choose;
        String menuError = "";
        while (flag) {
            System.out.println("=============== QUẢN LÝ KHÓA HỌC =============");
            System.out.println("1. Hiển thị danh sách khóa học");
            System.out.println("2. Thêm mới khóa học");
            System.out.println("3. Chỉnh sửa thông tin khóa học (hiển thị menu chọn thuộc tính cần sửa)");
            System.out.println("4. Xóa khóa học (xác nhận trước khi xoá)");
            System.out.println("5. Tìm kiếm theo tên (tương đối)");
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
                    displayCoursesWithPagination(scanner);
                    break;
                case 2:
                    menuError = "";
                    insertCourse(scanner);
                    break;
                case 3:
                    menuError = "";
                    updateCourse(scanner);
                    break;
                case 4:
                    menuError = "";
                    deleteCourse(scanner);
                    break;
                case 5:
                    menuError = "";
                    searchCourse(scanner);
                    break;
                case 6:
                    menuError = "";
                    sortCourse(scanner);
                    break;
                case 7:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 7");
            }
        }
    }

    // -HIỂN THỊ PHÂN TRANG ---
    public void displayCoursesWithPagination(Scanner scanner) {
        int currentPage = 1;
        int pageSize = 3;

        while (true) {
            int totalPages = courseService.getTotalPages(pageSize);
            List<Course> coursesPage = courseService.getCoursesByPage(currentPage, pageSize);

            if (coursesPage.isEmpty()) {
                System.err.println("Hiện tại danh sách khóa học đang trống!");
                return;
            }

            System.out.println("\n=== DANH SÁCH KHÓA HỌC HỆ THỐNG ===");
            System.out.printf("TRANG %d / %d\n", currentPage, totalPages);

            // Vẽ bảng hiển thị dữ liệu trang hiện hành
            printCourseTableFormat(coursesPage);

            if (totalPages > 1) {
                System.out.println("[N]: Trang kế tiếp  |  [P]: Trang trước đó  |  [E]: Thoát danh sách");
                System.out.print("Mời chọn hành động: ");
                String action = scanner.nextLine().trim().toUpperCase();

                if (action.equalsIgnoreCase("N")) {
                    if (currentPage < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("Bạn đang ở trang cuối cùng.");
                    }
                } else if (action.equalsIgnoreCase("P")) {
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("Bạn đang ở trang đầu tiên.");
                    }
                } else if (action.equalsIgnoreCase("E")) {
                    break;
                } else {
                    System.out.println("Lệnh điều hướng không hợp lệ!");
                }
            } else {
                break; // Chỉ có 1 trang đơn lẻ thì không cần thanh điều hướng
            }
        }
    }

    // Hàm vẽ bảng dữ liệu tĩnh dùng chung cho các tính năng khác
    private void printCourseTableFormat(List<Course> list) {
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(15) + "+");
        System.out.printf("| %-4s | %-28s | %-13s | %-23s | %-13s |\n", "ID", "TÊN KHÓA HỌC", "THỜI LƯỢNG", "GIẢNG VIÊN", "NGÀY TẠO");
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(15) + "+");
        for (Course c : list) {
            System.out.printf("| %-4d | %-28s | %-10d giờ | %-23s | %-13s |\n",
                    c.getId(), c.getName(), c.getDuration(), c.getInstructor(), c.getCreate_at().toString());
        }
        System.out.println("+" + "-".repeat(6) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(15) + "+");
    }


    public void insertCourse(Scanner scanner) {
        System.out.println("---THÊM MỚI KHOÁ HỌC----");
        System.out.println("Mời nhập tên khoá học: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.err.println("Tên khoá học không được để trống!");
            return;
        }


        int duration = 0;
        try {
            System.out.println("Mời nhập thời lượng khoá học (giờ): ");
            duration = Integer.parseInt(scanner.nextLine().trim());
            if (duration <= 0) {
                System.err.println("Thời lượng không được < 0!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Nhập một số nguyên");
            return;
        }

        System.out.println("Mời nhập giảng viên phụ trách: ");
        String instructor = scanner.nextLine().trim();
        if (instructor.isEmpty()) {
            System.err.println("Giảng viên phụ trách không được để trống!");
            return;
        }

        Course course = new Course(0, name, duration, instructor, new Date());
        if (courseService.insertCourse(course)) {
            System.out.println("Thêm mới khóa học thành công!");
        } else {
            System.out.println("Thêm mới khóa học thất bại.");
        }
    }

    public void updateCourse(Scanner scanner) {
        System.out.println("--- CHỈNH SỬA THÔNG TIN KHÓA HỌC ---");
        System.out.print("Nhập ID khóa học cần sửa: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID phải là số nguyên!");
            return;
        }

        int totalPages = courseService.getTotalPages(1000);
        List<Course> all = courseService.getCoursesByPage(1, totalPages * 1000 == 0 ? 1 : totalPages * 1000);

        Course editCourse = null;
        for (Course c : all) {
            if (c.getId() == id) {
                editCourse = c;
                break;
            }
        }
        if (editCourse == null) {
            System.out.println("Không tìm thấy khóa học nào có mã ID = " + id);
            return;
        }

        Course cloneCourse = new Course(
                editCourse.getId(),
                editCourse.getName(),
                editCourse.getDuration(),
                editCourse.getInstructor(),
                editCourse.getCreate_at()
        );

        boolean subFlag = true;
        boolean isSave = false;
        while (subFlag) {
            System.out.println("Khóa học đang chọn sửa: " + cloneCourse.getName());
            System.out.println("1. Sửa tên khóa học");
            System.out.println("2. Sửa thời lượng");
            System.out.println("3. Sửa giảng viên phụ trách");
            System.out.println("4. Lưu thông tin & Thoát");
            System.out.println("5. Hủy bỏ (Thoát và không lưu gì cả)");
            System.out.print("Nhập lựa chọn thuộc tính cần sửa: ");
            int subChoose;
            try {
                subChoose = Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.err.println("Vui lòng nhập số!");
                continue;
            }

            switch (subChoose) {
                case 1:
                    cloneCourse.setName(inputNonEmpty(scanner, "Nhập tên khóa học mới: "));
                    break;
                case 2:
                    cloneCourse.setDuration(inputPositiveInt(scanner, "Nhập thời lượng mới (giờ): "));
                    break;
                case 3:
                    cloneCourse.setInstructor(inputNonEmpty(scanner, "Nhập tên giảng viên mới: "));
                    break;
                case 4:
                    isSave = true;
                    subFlag = false;
                    break;
                case 5:
                    isSave = false;
                    subFlag = false;
                    break;
                default:
                    System.out.println("Lựa chọn từ 1 đến 5.");
            }
        }

        if (isSave) {
            if (courseService.updateCourse(cloneCourse)) {
                editCourse.setName(cloneCourse.getName());
                editCourse.setDuration(cloneCourse.getDuration());
                editCourse.setInstructor(cloneCourse.getInstructor());
                System.out.println("Cập nhật thông tin khóa học thành công!");
            } else {
                System.out.println("Cập nhật thông tin thất bại.");
            }
        } else {
            System.out.println("Đã hủy bỏ thao tác chỉnh sửa. Dữ liệu khóa học được giữ nguyên!");
        }
    }

    public void deleteCourse(Scanner scanner) {
        System.out.println("--- XÓA KHÓA HỌC HỆ THỐNG ---");
        int id;
        try {
            System.out.println("Nhập ID khoá học cần xoá: ");
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("ID phải là số nguyên!");
            return;
        }
        System.out.print("Bạn có chắc chắn muốn xóa khóa học này không? (Gõ 'Y' để xóa, phím bất kỳ để hủy): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y")) {
            try {
                if (courseService.deleteCourse(id)) {
                    System.out.println("Xóa khóa học thành công!");
                } else {
                    System.out.println("Không tìm thấy khóa học nào có ID bằng " + id + " để xóa.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Đã hủy thao tác xóa.");
        }
    }

    public void searchCourse(Scanner scanner) {
        System.out.println("\n--- TÌM KIẾM KHÓA HỌC THEO TÊN (PHÂN TRANG) ---");
        System.out.print("Nhập từ khóa tên khóa học muốn tìm: ");
        String keyword = scanner.nextLine().trim();

        int currentPage = 1;
        int pageSize = 3;

        while (true) {
            int totalPages = courseService.getTotalPagesForSearch(keyword, pageSize);
            List<Course> resultsPage = courseService.searchCoursesByPage(keyword, currentPage, pageSize);

            if (resultsPage.isEmpty()) {
                System.err.println("Không tìm thấy khóa học nào phù hợp với từ khóa: " + keyword);
                return;
            }

            System.out.println("\nKẾT QUẢ TÌM KIẾM CHO TIÊU CHÍ [" + keyword + "]");
            System.out.printf("TRANG %d / %d\n", currentPage, totalPages);
            printCourseTableFormat(resultsPage);

            if (totalPages > 1) {
                System.out.println("[N]: Trang kế tiếp  |  [P]: Trang trước đó  |  [E]: Thoát tìm kiếm");
                System.out.print("Mời chọn hành động: ");
                String action = scanner.nextLine().trim().toUpperCase();

                if (action.equalsIgnoreCase("N") && currentPage < totalPages) {
                    currentPage++;
                } else if (action.equalsIgnoreCase("P") && currentPage > 1) {
                    currentPage--;
                } else if (action.equalsIgnoreCase("E")) {
                    break;
                } else System.out.println("Lệnh điều hướng không hợp lệ!");
            } else {
                break;
            }
        }
    }

    public void sortCourse(Scanner scanner) {
        System.out.println("\n--- SẮP XẾP DANH SÁCH KHÓA HỌC (PHÂN TRANG) ---");
        System.out.println("1. Sắp xếp theo Tên khóa học");
        System.out.println("2. Sắp xếp theo ID khóa học");
        System.out.print("Chọn tiêu chí (1 hoặc 2): ");
        String target = scanner.nextLine().trim().equalsIgnoreCase("2") ? "id" : "name";

        System.out.println("1. Sắp xếp Tăng dần (A-Z)");
        System.out.println("2. Sắp xếp Giảm dần (Z-A)");
        System.out.print("Chọn chiều sắp xếp (1 hoặc 2): ");
        String direction = scanner.nextLine().trim().equalsIgnoreCase("2") ? "DESC" : "ASC";

        int currentPage = 1;
        int pageSize = 3; //

        while (true) {
            int totalPages = courseService.getTotalPages(pageSize);
            List<Course> sortedPage = courseService.getSortedCoursesByPage(target, direction, currentPage, pageSize);

            System.out.println("\nDANH SÁCH KHÓA HỌC SAU KHI SẮP XẾP");
            System.out.printf("TRANG %d / %d (Tiêu chí: %s - %s)\n", currentPage, totalPages, target, direction);
            printCourseTableFormat(sortedPage);

            if (totalPages > 1) {
                System.out.println("[N]: Trang kế tiếp  |  [P]: Trang trước đó  |  [E]: Thoát");
                System.out.print("Mời chọn hành động: ");
                String action = scanner.nextLine().trim().toUpperCase();

                if (action.equalsIgnoreCase("N") && currentPage < totalPages) {
                    currentPage++;
                } else if (action.equalsIgnoreCase("P") && currentPage > 1) {
                    currentPage--;
                } else if (action.equalsIgnoreCase("E")) {
                    break;
                } else System.out.println("Lệnh điều hướng không hợp lệ!");
            } else {
                break;
            }
        }
    }

    private String inputNonEmpty(Scanner scanner, String message) {
        String input;
        String err = "";
        while (true) {
            if (!err.isEmpty()) System.out.println(err);
            System.out.print(message);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                err = "Lỗi: Trường này bắt buộc nhập, không được bỏ trống!";
                continue;
            }
            break;
        }
        return input;
    }

    private int inputPositiveInt(Scanner scanner, String message) {
        int val;
        String err = "";
        while (true) {
            if (!err.isEmpty()) System.out.println(err);
            System.out.print(message);
            try {
                val = Integer.parseInt(scanner.nextLine().trim());
                if (val <= 0) {
                    err = "Lỗi: Giá trị nhập vào phải lớn hơn 0!";
                    continue;
                }
                break;
            } catch (Exception e) {
                err = "Lỗi: Vui lòng nhập đúng định dạng số nguyên!";
            }
        }
        return val;
    }
}

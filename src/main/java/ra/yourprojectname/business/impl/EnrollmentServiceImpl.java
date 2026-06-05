package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.EnrollmentService;
import ra.yourprojectname.dao.impl.CourseDAOImpl;
import ra.yourprojectname.dao.impl.EnrollmentDAOImpl;
import ra.yourprojectname.dao.impl.StudentDAOImpl;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Student;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnrollmentServiceImpl implements EnrollmentService {
    private final CourseDAOImpl courseDAO = new CourseDAOImpl();
    private final EnrollmentDAOImpl enrollmentDAO = new EnrollmentDAOImpl();
    private final StudentDAOImpl studentDAO = new StudentDAOImpl();

    @Override
    public Student getStudentByEmail(String email) {
        return studentDAO.getStudentByEmail(email);
    }

    @Override
    public boolean enrollCourse(int studentId, int courseId) {
        if (enrollmentDAO.isEnrolled(studentId, courseId)) {
            System.err.println("Bạn đã đăng ký khóa học này trước đó rồi!");
            return false;
        }
        return enrollmentDAO.insertEnrollment(studentId, courseId);
    }

    @Override
    public List<Course> getCoursesByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return courseDAO.findAllWithPagination(pageSize, offset);
    }

    @Override
    public int getTotalPages(int pageSize) {
        int totalCourses = courseDAO.countTotalCourses();
        return (int) Math.ceil((double) totalCourses / pageSize);
    }

    // Kết nối mượt mà với hàm `findCourseByNameWithPagination` có sẵn trong CourseDAO của bạn
    @Override
    public List<Course> searchCoursesByPage(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return courseDAO.findCourseByNameWithPagination(keyword.trim(), pageSize, offset);
    }

    // Kết nối mượt mà với hàm `countCoursesByName` có sẵn trong CourseDAO của bạn
    @Override
    public int getSearchCoursesTotalPages(String keyword, int pageSize) {
        int total = courseDAO.countCoursesByName(keyword.trim());
        return (int) Math.ceil((double) total / pageSize);
    }

    @Override
    public List<Course> getStudentCoursesByPage(int studentId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return enrollmentDAO.getCoursesByStudentIdWithPagination(studentId, pageSize, offset);
    }

    @Override
    public int getStudentCoursesTotalPages(int studentId, int pageSize) {
        int total = enrollmentDAO.countCoursesByStudentId(studentId);
        return (int) Math.ceil((double) total / pageSize);
    }

    @Override
    public List<Course> getStudentCoursesSortedByPage(int studentId, String column, String direction, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return enrollmentDAO.getCoursesByStudentIdSortedWithPagination(studentId, column, direction, pageSize, offset);
    }

    @Override
    public boolean cancelEnrolledCourse(int studentId, int courseId) {
        return enrollmentDAO.cancelEnrollmentByStudent(studentId, courseId);
    }

    @Override
    public boolean isCourseEnrolledByStudent(int studentId, int courseId) {
        return enrollmentDAO.isEnrolled(studentId, courseId);
    }

    @Override
    public List<Course> getRecommendedCourses(int studentId) {
        // BƯỚC 1: LẤY DỮ LIỆU ĐẦU VÀO TỪ DATABASE
        // Lấy tối đa 100 khóa học hiện có trên toàn hệ thống để làm kho dữ liệu phân tích
        List<Course> allCourses = courseDAO.findAllWithPagination(100, 0);

        // Lấy tối đa 100 khóa học mà học viên này đã đăng ký trước đó để tìm xu hướng sở thích
        List<Course> enrolledCourses = enrollmentDAO.getCoursesByStudentIdWithPagination(studentId, 100, 0);

        // Nếu học viên mới tinh (chưa đăng ký môn nào), hệ thống không có dữ liệu để phân tích
        // Trả về luôn danh sách gốc để hiển thị ngẫu nhiên các khóa học cho họ
        if (enrolledCourses == null || enrolledCourses.isEmpty()) {
            return allCourses;
        }

        // BƯỚC 2: PHÂN TÍCH XU HƯỚNG SỞ THÍCH (TÍNH TRỌNG SỐ TỪ KHÓA)
        // Set này dùng để lưu nhanh ID các khóa đã học, giúp lọc bỏ không gợi ý lại ở bước sau
        Set<Integer> enrolledIds = new HashSet<>();

        // Map này đóng vai trò là "Bảng điểm sở thích": Key là từ khóa, Value là số lần xuất hiện
        java.util.Map<String, Integer> keywordWeights = new java.util.HashMap<>();

        // Duyệt qua từng khóa học mà học viên đã đăng ký
        for (Course c : enrolledCourses) {
            // Lưu ID khóa học này vào Set
            enrolledIds.add(c.getId());

            // Chuyển toàn bộ tên khóa học thành chữ thường và cắt ra thành mảng các từ đơn lẻ dựa trên khoảng trắng
            String[] words = c.getName().toLowerCase().split("\\s+");

            // Duyệt qua từng từ đơn lẻ vừa cắt được
            for (String w : words) {
                // Lọc bỏ từ nhiễu (Stopwords): Từ phải dài từ 3 ký tự trở lên
                // và không phải các từ chung chung xuất hiện ở mọi nơi như "học", "trình"
                if (w.length() >= 3 && !w.equals("học") && !w.equals("trình")) {
                    // Nếu từ khóa đã có trong Map, tăng số đếm lên 1. Nếu chưa có, đặt mặc định là 0 rồi cộng 1
                    keywordWeights.put(w, keywordWeights.getOrDefault(w, 0) + 1);
                }
            }
        }

        // BƯỚC 3: CHẤM ĐIỂM TƯƠNG ĐỒNG CHO CÁC KHÓA HỌC CHƯA HỌC
        // Danh sách lưu trữ các khóa học kèm theo số điểm tương thích của nó
        List<CourseWithScore> scoredCourses = new ArrayList<>();

        // Duyệt qua toàn bộ danh sách khóa học có trên hệ thống
        for (Course c : allCourses) {
            // Điều kiện tiên quyết: Chỉ xét những khóa học mà học viên CHƯA TỪNG ĐĂNG KÝ
            if (!enrolledIds.contains(c.getId())) {
                int finalScore = 0; // Biến tích lũy điểm cho khóa học đang xét

                // Tách tên khóa học hệ thống này thành các từ đơn lẻ để đối chiếu
                String[] words = c.getName().toLowerCase().split("\\s+");
                for (String w : words) {
                    // Nếu từ khóa trong tên môn học này trùng khớp với từ khóa trong "Map sở thích" ở Bước 2
                    if (keywordWeights.containsKey(w)) {
                        // Cộng dồn điểm bằng chính trọng số (tần suất) mà học viên đã từng tiếp cận từ đó
                        finalScore += keywordWeights.get(w);
                    }
                }

                // Nếu khóa học này có chứa ít nhất một từ khóa mà học viên thích (điểm > 0)
                if (finalScore > 0) {
                    // Đóng gói Khóa học + Số điểm tương ứng vào chiếc "hộp bọc" CourseWithScore và lưu lại
                    scoredCourses.add(new CourseWithScore(c, finalScore));
                }
            }
        }

        // BƯỚC 4: SẮP XẾP THEO ĐIỂM SỐ VÀ LỌC LẤY TOP 3 ĐẦU BẢNG
        // Sắp xếp danh sách giảm dần theo thuộc tính `score` (Môn khớp sở thích nhất nằm lên trên cùng)
        scoredCourses.sort((a, b) -> Integer.compare(b.score, a.score));

        // Tạo danh sách sạch chứa đối tượng Course thuần túy để trả về cho Presentation hiển thị
        List<Course> recommendedList = new ArrayList<>();
        int count = 0;

        // Duyệt qua danh sách đã sắp xếp
        for (CourseWithScore cs : scoredCourses) {
            // Bóc tách lấy đối tượng Course từ trong chiếc hộp bọc ra
            recommendedList.add(cs.course);
            count++;
            // Ngắt vòng lặp ngay khi đã lấy đủ 3 khóa học phù hợp nhất
            if (count >= 3) break;
        }

        // Trả về danh sách chứa tối đa 3 khóa học gợi ý thông minh
        return recommendedList;
    }

    /**
     * Cấu trúc dữ liệu bổ trợ (Inner Class) đóng vai trò như một chiếc "hộp bọc".
     * Giúp gắn thêm thuộc tính điểm số (score) vào đối tượng Course trong quá trình chạy thuật toán,
     * do Class Course gốc trong Model không có thuộc tính chứa điểm này.
     */
    private static class CourseWithScore {
        Course course; // Đối tượng khóa học gốc
        int score;     // Điểm số tương thích được thuật toán chấm

        // Hàm khởi tạo (Constructor)
        CourseWithScore(Course course, int score) {
            this.course = course;
            this.score = score;
        }
    }
}
package ra.yourprojectname.model;

import java.time.LocalDateTime;

public class Enrollment {
    private int id;
    private Student student;
    private Course course;
    private LocalDateTime registeredAt;
    private EnrollmentStatus status;

    public Enrollment() {
    }

    public Enrollment(int id, Student student, Course course, LocalDateTime registeredAt, EnrollmentStatus status) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.registeredAt = registeredAt;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", student=" + student +
                ", course=" + course +
                ", registeredAt=" + registeredAt +
                ", status=" + status +
                '}';
    }
}

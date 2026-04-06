package com.college.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"enrollments", "hibernateLazyInitializer", "handler"})
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"enrollments", "hibernateLazyInitializer", "handler"})
    private Course course;

    @Column(nullable = false)
    private String status; // ENROLLED, COMPLETED, DROPPED, IN_PROGRESS

    @Column(name = "enrollment_date")
    private java.time.LocalDate enrollmentDate;

    @Column(name = "completion_date")
    private java.time.LocalDate completionDate;

    @Column
    private Integer grade; // Final grade (0-100)

    @Column
    private String gradeLetter; // A, B, C, D, F

    @Column
    private Double creditsEarned;

    @Column
    private String semester;

    @Column
    private String academicYear;

    @Column
    private String remarks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Enrollment() {}

    public Enrollment(Student student, Course course, String status, String semester, String academicYear) {
        this.student = student;
        this.course = course;
        this.status = status;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public java.time.LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(java.time.LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public java.time.LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(java.time.LocalDate completionDate) { this.completionDate = completionDate; }

    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }

    public String getGradeLetter() { return gradeLetter; }
    public void setGradeLetter(String gradeLetter) { this.gradeLetter = gradeLetter; }

    public Double getCreditsEarned() { return creditsEarned; }
    public void setCreditsEarned(Double creditsEarned) { this.creditsEarned = creditsEarned; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (enrollmentDate == null) {
            enrollmentDate = java.time.LocalDate.now();
        }
        if (status == null) {
            status = "ENROLLED";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isActive() {
        return "ENROLLED".equals(status) || "IN_PROGRESS".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isDropped() {
        return "DROPPED".equals(status);
    }
}

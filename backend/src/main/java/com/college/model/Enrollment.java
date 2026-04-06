package com.college.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

package com.college.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_registrations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExamRegistration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examination_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Examination examination;

    @Column(nullable = false)
    private String status; // REGISTERED, APPROVED, REJECTED, COMPLETED, ABSENT

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "exam_attendance")
    @Builder.Default
    private Boolean examAttendance = false; // Whether student actually attended

    @Column(name = "special_requirements")
    private String specialRequirements; // Special accommodations needed

    @Column(name = "approval_notes")
    private String approvalNotes; // Notes for approval/rejection

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
        if (status == null) {
            status = "REGISTERED";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business logic methods
    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean canAttend() {
        return isApproved() && examAttendance == null;
    }

    public boolean hasAttended() {
        return Boolean.TRUE.equals(examAttendance);
    }

    public boolean wasAbsent() {
        return Boolean.FALSE.equals(examAttendance);
    }
}

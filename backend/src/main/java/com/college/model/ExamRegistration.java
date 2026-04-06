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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"enrollments", "hibernateLazyInitializer", "handler"})
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", nullable = false)
    @JsonIgnoreProperties({"examRegistrations", "hibernateLazyInitializer", "handler"})
    private Examination examination;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "exam_attendance")
    @Builder.Default
    private Boolean examAttendance = false;

    @Column(name = "special_requirements")
    private String specialRequirements; // Special accommodations needed

    @Column(name = "approval_notes")
    private String approvalNotes; // Notes for approval/rejection

    @Column(nullable = false)
    private String status; // REGISTERED, APPROVED, REJECTED, COMPLETED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Boolean getExamAttendance() { 
        return examAttendance; 
    }
    public void setExamAttendance(Boolean examAttendance) { 
        this.examAttendance = examAttendance; 
    }

    public ExamRegistration(Student student, Examination examination, LocalDateTime registrationDate, 
                               Boolean examAttendance, String specialRequirements, String status) {
        this.student = student;
        this.examination = examination;
        this.registrationDate = registrationDate;
        this.examAttendance = examAttendance != null ? examAttendance : false;
        this.specialRequirements = specialRequirements;
        this.status = status;
    }

    public String getSpecialRequirements() { 
        return specialRequirements; 
    }
    public void setSpecialRequirements(String specialRequirements) { 
        this.specialRequirements = specialRequirements; 
    }

    public String getApprovalNotes() { 
        return approvalNotes; 
    }
    public void setApprovalNotes(String approvalNotes) { 
        this.approvalNotes = approvalNotes; 
    }

    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
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

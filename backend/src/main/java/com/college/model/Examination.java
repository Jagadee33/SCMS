package com.college.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "examinations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Examination {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Faculty faculty;

    @Column(name = "exam_date", nullable = false)
    private LocalDateTime examDate;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String duration;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "passing_marks", nullable = false)
    private Integer passingMarks;

    @Column(nullable = false)
    private String status; // Upcoming, Ongoing, Completed, Cancelled

    @Column(name = "exam_type", nullable = false)
    private String examType; // Midterm, Final, Practical, Quiz, Assignment

    @Column(name = "exam_mode")
    private String examMode; // Online, Offline, Hybrid

    @Column(name = "venue")
    private String venue; // Exam location/room

    @Column(name = "instructions")
    private String instructions; // Exam instructions for students

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;

    @Column(name = "result_date")
    private LocalDateTime resultDate;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "current_participants")
    private Integer currentParticipants;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "Upcoming";
        }
        if (currentParticipants == null) {
            currentParticipants = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business logic methods
    public boolean isRegistrationOpen() {
        return registrationDeadline != null && LocalDateTime.now().isBefore(registrationDeadline);
    }

    public boolean isFull() {
        return maxParticipants != null && currentParticipants >= maxParticipants;
    }

    public boolean canRegister() {
        return isActive && isRegistrationOpen() && !isFull();
    }

    public boolean isResultPublished() {
        return resultDate != null && LocalDateTime.now().isAfter(resultDate);
    }
}

package com.college.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Grade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examination_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Examination examination;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Faculty faculty;

    @Column(nullable = false)
    private String gradeType; // ASSIGNMENT, QUIZ, MIDTERM, FINAL, PROJECT, PRACTICAL

    @Column(name = "grade_title", nullable = false)
    private String gradeTitle; // e.g., "Assignment 1", "Quiz 2", "Midterm Exam"

    @Column(name = "max_marks", nullable = false)
    private Double maxMarks;

    @Column(name = "obtained_marks", nullable = false)
    private Double obtainedMarks;

    @Column(name = "percentage")
    private Double percentage; // Auto-calculated

    @Column(name = "grade_letter")
    private String gradeLetter; // A+, A, B+, B, C+, C, D, F

    @Column(name = "grade_points")
    private Double gradePoints; // 4.0, 3.7, 3.3, 3.0, etc.

    @Column(name = "weightage")
    private Double weightage; // Weight in final grade calculation (e.g., 0.20 for 20%)

    @Column(nullable = false)
    private String status; // PUBLISHED, DRAFT, PENDING

    @Column(name = "graded_date")
    private LocalDateTime gradedDate;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculatePercentage();
        calculateGradeLetterAndPoints();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculatePercentage();
        calculateGradeLetterAndPoints();
    }

    private void calculatePercentage() {
        if (maxMarks != null && maxMarks > 0 && obtainedMarks != null) {
            percentage = (obtainedMarks / maxMarks) * 100;
        }
    }

    private void calculateGradeLetterAndPoints() {
        if (percentage == null) return;

        if (percentage >= 95) {
            gradeLetter = "A+";
            gradePoints = 4.0;
        } else if (percentage >= 90) {
            gradeLetter = "A";
            gradePoints = 3.7;
        } else if (percentage >= 85) {
            gradeLetter = "B+";
            gradePoints = 3.3;
        } else if (percentage >= 80) {
            gradeLetter = "B";
            gradePoints = 3.0;
        } else if (percentage >= 75) {
            gradeLetter = "C+";
            gradePoints = 2.7;
        } else if (percentage >= 70) {
            gradeLetter = "C";
            gradePoints = 2.3;
        } else if (percentage >= 65) {
            gradeLetter = "D+";
            gradePoints = 2.0;
        } else if (percentage >= 60) {
            gradeLetter = "D";
            gradePoints = 1.7;
        } else {
            gradeLetter = "F";
            gradePoints = 0.0;
        }
    }
}

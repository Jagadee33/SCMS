package com.college.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "book_issues")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookIssue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "return_date")
    private LocalDateTime returnDate;
    
    @Column(name = "actual_return_date")
    private LocalDateTime actualReturnDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;
    
    @Column(name = "renewal_count")
    @Builder.Default
    private Integer renewalCount = 0;
    
    @Column(name = "max_renewals")
    @Builder.Default
    private Integer maxRenewals = 2;
    
    @Column(name = "fine_amount")
    @Builder.Default
    private Double fineAmount = 0.0;
    
    @Column(name = "fine_paid")
    @Builder.Default
    private Boolean finePaid = false;
    
    @Column(name = "fine_paid_date")
    private LocalDateTime finePaidDate;
    
    @Column(name = "issued_by")
    private Long issuedBy;
    
    @Column(name = "returned_to")
    private Long returnedTo;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "condition_issued")
    private String conditionIssued;
    
    @Column(name = "condition_returned")
    private String conditionReturned;
    
    @Column(name = "damage_description")
    private String damageDescription;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum IssueStatus {
        ISSUED, RETURNED, OVERDUE, LOST, DAMAGED, RENEWED
    }
}

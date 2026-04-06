package com.college.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "fees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String feeType; // Tuition, Library, Lab, Hostel, Transport, etc.

    @Column(nullable = false)
    private String description;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "paid_amount")
    private Double paidAmount;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method")
    private String paymentMethod; // Cash, Card, Bank Transfer, Online

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(nullable = false)
    private String status; // Pending, Paid, Partial, Overdue, Cancelled

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "semester")
    private String semester;

    @Column(name = "late_fee")
    private Double lateFee;

    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(name = "discount_reason")
    private String discountReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "Pending";
        }
        if (paidAmount == null) {
            paidAmount = 0.0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Double getRemainingAmount() {
        return amount - (paidAmount != null ? paidAmount : 0.0) + (discountAmount != null ? discountAmount : 0.0);
    }

    public boolean isPaid() {
        return "Paid".equals(status);
    }

    public boolean isOverdue() {
        return "Pending".equals(status) || "Partial".equals(status) && 
               dueDate != null && dueDate.isBefore(LocalDateTime.now());
    }
}

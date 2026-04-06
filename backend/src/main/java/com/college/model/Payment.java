package com.college.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fee_id", nullable = false)
    private Fee fee;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Cash, Card, Bank Transfer, Online, UPI

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "receipt_number", nullable = false)
    private String receiptNumber;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus; // Success, Failed, Pending, Refunded

    @Column(name = "gateway_response")
    private String gatewayResponse;

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    @Column(name = "gateway_name")
    private String gatewayName; // Razorpay, Stripe, PayPal, etc.

    @Column(name = "currency", nullable = false)
    private String currency = "INR";

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
        if (receiptNumber == null) {
            receiptNumber = "RCP" + System.currentTimeMillis();
        }
        if (paymentStatus == null) {
            paymentStatus = "Pending";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isSuccessful() {
        return "Success".equals(paymentStatus);
    }

    public boolean isPending() {
        return "Pending".equals(paymentStatus);
    }

    public boolean isFailed() {
        return "Failed".equals(paymentStatus);
    }

    public boolean isRefunded() {
        return "Refunded".equals(paymentStatus);
    }
}

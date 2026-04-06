package com.college.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
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
    
    @Column(name = "recipient_id")
    private Long recipientId;

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
    
    // Constructors
    public Payment() {}
    
    public Payment(Fee fee, Student student, Long recipientId, Double amount, String paymentMethod, 
                    String transactionId, String receiptNumber, LocalDateTime paymentDate, String paymentStatus, 
                    String gatewayResponse, String gatewayTransactionId, String gatewayName, String currency, 
                    Double refundAmount, LocalDateTime refundDate, String refundReason, 
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.fee = fee;
        this.student = student;
        this.recipientId = recipientId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.receiptNumber = receiptNumber;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
        this.gatewayResponse = gatewayResponse;
        this.gatewayTransactionId = gatewayTransactionId;
        this.gatewayName = gatewayName;
        this.currency = currency;
        this.refundAmount = refundAmount;
        this.refundDate = refundDate;
        this.refundReason = refundReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Fee getFee() { return fee; }
    public void setFee(Fee fee) { this.fee = fee; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getGatewayResponse() { return gatewayResponse; }
    public void setGatewayResponse(String gatewayResponse) { this.gatewayResponse = gatewayResponse; }
    
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public void setGatewayTransactionId(String gatewayTransactionId) { this.gatewayTransactionId = gatewayTransactionId; }
    
    public String getGatewayName() { return gatewayName; }
    public void setGatewayName(String gatewayName) { this.gatewayName = gatewayName; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }
    
    public LocalDateTime getRefundDate() { return refundDate; }
    public void setRefundDate(LocalDateTime refundDate) { this.refundDate = refundDate; }
    
    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

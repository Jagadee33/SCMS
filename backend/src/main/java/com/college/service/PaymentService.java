package com.college.service;

import com.college.model.PaymentTransaction;
import com.college.model.PaymentGateway;
import com.college.model.PaymentPlan;
import com.college.model.User;
import com.college.repository.PaymentTransactionRepository;
import com.college.repository.PaymentGatewayRepository;
import com.college.repository.PaymentPlanRepository;
import com.college.repository.FeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {
    
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final PaymentPlanRepository paymentPlanRepository;
    private final FeeRepository feeRepository;
    
    // Payment Gateway Management
    public List<PaymentGateway> getAllPaymentGateways() {
        return paymentGatewayRepository.findByIsActive(true);
    }
    
    public PaymentGateway getPaymentGatewayById(Long id) {
        return paymentGatewayRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment gateway not found with id: " + id));
    }
    
    public PaymentGateway createPaymentGateway(PaymentGateway gateway) {
        gateway.setCreatedAt(LocalDateTime.now());
        gateway.setUpdatedAt(LocalDateTime.now());
        return paymentGatewayRepository.save(gateway);
    }
    
    // Payment Plan Management
    public List<PaymentPlan> getAllPaymentPlans() {
        return paymentPlanRepository.findByIsActive(true);
    }
    
    public PaymentPlan getPaymentPlanById(Long id) {
        return paymentPlanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment plan not found with id: " + id));
    }
    
    public PaymentPlan createPaymentPlan(PaymentPlan plan) {
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        return paymentPlanRepository.save(plan);
    }
    
    // Payment Processing
    public PaymentTransaction processPayment(Long studentId, Long feeId, String paymentMethod, 
                                           BigDecimal amount, String gatewayName) {
        
        // Generate unique transaction ID
        String transactionId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create payment transaction record
        PaymentTransaction transaction = PaymentTransaction.builder()
            .student(createUser(studentId)) // Helper method
            .transactionId(transactionId)
            .paymentMethod(paymentMethod)
            .amount(amount)
            .paymentStatus(PaymentTransaction.PaymentStatus.PENDING)
            .paymentDate(LocalDateTime.now())
            .description("Payment processing")
            .build();
        
        PaymentTransaction savedTransaction = paymentTransactionRepository.save(transaction);
        
        try {
            // Here you would integrate with actual payment gateway
            // For demo, we'll simulate successful payment
            savedTransaction.setPaymentStatus(PaymentTransaction.PaymentStatus.COMPLETED);
            savedTransaction.setGatewayTransactionId("GATEWAY_" + transactionId);
            savedTransaction.setGatewayResponse("{\"status\": \"success\", \"message\": \"Payment processed successfully\"}");
            savedTransaction.setReceiptNumber("REC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            savedTransaction.setUpdatedAt(LocalDateTime.now());
            
            return paymentTransactionRepository.save(savedTransaction);
            
        } catch (Exception e) {
            log.error("Payment processing failed for transaction: {}", transactionId, e);
            savedTransaction.setPaymentStatus(PaymentTransaction.PaymentStatus.FAILED);
            savedTransaction.setGatewayResponse("{\"status\": \"failed\", \"error\": \"" + e.getMessage() + "\"}");
            savedTransaction.setUpdatedAt(LocalDateTime.now());
            
            return paymentTransactionRepository.save(savedTransaction);
        }
    }

    public PaymentTransaction refundPayment(Long transactionId, BigDecimal refundAmount, String reason) {
        PaymentTransaction transaction = paymentTransactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        
        if (transaction.getPaymentStatus() != PaymentTransaction.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed transactions can be refunded");
        }
        
        transaction.setPaymentStatus(PaymentTransaction.PaymentStatus.REFUNDED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setDescription("Refund: " + reason);
        
        return paymentTransactionRepository.save(transaction);
    }
    
    // Transaction History and Analytics
    public List<PaymentTransaction> getStudentPaymentHistory(Long studentId) {
        return paymentTransactionRepository.findByStudentIdOrderByPaymentDateDesc(studentId);
    }
    
    public Map<String, Object> getPaymentStatistics() {
        List<Object[]> methodStats = paymentTransactionRepository.getPaymentMethodStatistics();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("paymentMethods", methodStats);
        
        BigDecimal totalRevenue = paymentTransactionRepository.getTotalByPaymentMethod("CREDIT_CARD")
                .add(paymentTransactionRepository.getTotalByPaymentMethod("DEBIT_CARD"))
                .add(paymentTransactionRepository.getTotalByPaymentMethod("UPI"))
                .add(paymentTransactionRepository.getTotalByPaymentMethod("NET_BANKING"))
                .add(paymentTransactionRepository.getTotalByPaymentMethod("CASH"));
        
        statistics.put("totalRevenue", totalRevenue);
        
        List<Object[]> statusStats = paymentTransactionRepository.getTransactionCountByStatus();
        statistics.put("transactionStatuses", statusStats);
        
        return statistics;
    }
    
    public List<PaymentTransaction> getRecentTransactions() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return paymentTransactionRepository.getRecentTransactions(thirtyDaysAgo);
    }
    
    public List<PaymentTransaction> getFailedTransactions() {
        return paymentTransactionRepository.getFailedTransactions();
    }
    
    public List<PaymentTransaction> getRefundedTransactions() {
        return paymentTransactionRepository.getRefundTransactions();
    }
    
    // Helper method to create User object
    private User createUser(Long studentId) {
        User user = new User();
        user.setId(studentId);
        return user;
    }
}

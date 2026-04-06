package com.college.repository;

import com.college.model.PaymentTransaction;
import com.college.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    
    // Find transactions by student
    List<PaymentTransaction> findByStudentIdOrderByPaymentDateDesc(Long studentId);
    
    // Find transactions by student with pagination
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.student.id = :studentId ORDER BY pt.paymentDate DESC")
    List<PaymentTransaction> findByStudentIdWithPagination(@Param("studentId") Long studentId, 
                                                     org.springframework.data.domain.Pageable pageable);
    
    // Find transaction by transaction ID
    Optional<PaymentTransaction> findByTransactionId(String transactionId);
    
    // Find transactions by payment status
    List<PaymentTransaction> findByPaymentStatus(PaymentTransaction.PaymentStatus paymentStatus);
    
    // Find transactions by payment method
    List<PaymentTransaction> findByPaymentMethod(String paymentMethod);
    
    // Find transactions by date range
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.paymentDate BETWEEN :startDate AND :endDate ORDER BY pt.paymentDate DESC")
    List<PaymentTransaction> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    // Get total amount paid by student
    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.student.id = :studentId AND pt.paymentStatus = 'COMPLETED'")
    BigDecimal getTotalPaidByStudent(@Param("studentId") Long studentId);
    
    // Get total amount by payment method
    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.paymentMethod = :paymentMethod AND pt.paymentStatus = 'COMPLETED'")
    BigDecimal getTotalByPaymentMethod(@Param("paymentMethod") String paymentMethod);
    
    // Get transaction statistics
    @Query("SELECT pt.paymentMethod, COUNT(pt), SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.paymentStatus = 'COMPLETED' GROUP BY pt.paymentMethod")
    List<Object[]> getPaymentMethodStatistics();
    
    // Get daily transaction summary
    @Query("SELECT DATE(pt.paymentDate) as date, COUNT(pt) as count, SUM(pt.amount) as total FROM PaymentTransaction pt WHERE pt.paymentStatus = 'COMPLETED' AND pt.paymentDate >= :startDate GROUP BY DATE(pt.paymentDate) ORDER BY date DESC")
    List<Object[]> getDailyTransactionSummary(@Param("startDate") LocalDateTime startDate);
    
    // Get failed transactions
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.paymentStatus = 'FAILED' ORDER BY pt.paymentDate DESC")
    List<PaymentTransaction> getFailedTransactions();
    
    // Get refund transactions
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.paymentStatus IN ('REFUNDED', 'PARTIALLY_REFUNDED') ORDER BY pt.paymentDate DESC")
    List<PaymentTransaction> getRefundTransactions();
    
    // Count transactions by status
    @Query("SELECT pt.paymentStatus, COUNT(pt) FROM PaymentTransaction pt GROUP BY pt.paymentStatus")
    List<Object[]> getTransactionCountByStatus();
    
    // Find recent transactions (last 30 days)
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.paymentStatus = 'COMPLETED' AND pt.paymentDate >= :startDate ORDER BY pt.paymentDate DESC")
    List<PaymentTransaction> getRecentTransactions(@Param("startDate") LocalDateTime startDate);
}

package com.college.repository;

import com.college.model.Payment;
import com.college.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStudentId(Long studentId);

    List<Payment> findByStudentIdOrderByPaymentDateDesc(Long studentId);

    List<Payment> findByFeeId(Long feeId);

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByReceiptNumber(String receiptNumber);

    List<Payment> findByPaymentStatus(String paymentStatus);

    List<Payment> findByPaymentMethod(String paymentMethod);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId AND p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByStudentIdAndPaymentDateBetween(@Param("studentId") Long studentId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = :status AND p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentStatusAndDateRange(@Param("status") String status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = 'Success'")
    Long countSuccessfulPayments();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = 'Failed'")
    Long countFailedPayments();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = 'Pending'")
    Long countPendingPayments();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'Success'")
    Double getTotalSuccessfulPayments();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.student.id = :studentId AND p.paymentStatus = 'Success'")
    Double getTotalPaymentsByStudent(@Param("studentId") Long studentId);

    @Query("SELECT p FROM Payment p WHERE p.gatewayTransactionId = :gatewayTransactionId")
    Optional<Payment> findByGatewayTransactionId(@Param("gatewayTransactionId") String gatewayTransactionId);

    @Query("SELECT p FROM Payment p WHERE p.gatewayName = :gatewayName")
    List<Payment> findByGatewayName(@Param("gatewayName") String gatewayName);
}

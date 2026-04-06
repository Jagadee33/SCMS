package com.college.repository;

import com.college.model.BookReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookReservationRepository extends JpaRepository<BookReservation, Long> {
    
    // Find reservations by student
    List<BookReservation> findByStudentId(Long studentId);
    
    // Find reservations by book
    List<BookReservation> findByBookId(Long bookId);
    
    // Find reservations by status
    List<BookReservation> findByStatus(BookReservation.ReservationStatus status);
    
    // Find active reservations for a student
    @Query("SELECT br FROM BookReservation br WHERE br.student.id = :studentId AND br.status = 'ACTIVE'")
    List<BookReservation> findActiveReservationsByStudent(@Param("studentId") Long studentId);
    
    // Find active reservations for a book
    @Query("SELECT br FROM BookReservation br WHERE br.book.id = :bookId AND br.status = 'ACTIVE' ORDER BY br.createdAt ASC")
    List<BookReservation> findActiveReservationsByBook(@Param("bookId") Long bookId);
    
    // Find expired reservations
    @Query("SELECT br FROM BookReservation br WHERE br.status = 'ACTIVE' AND br.expiryDate < :currentDate")
    List<BookReservation> findExpiredReservations(@Param("currentDate") LocalDateTime currentDate);
    
    // Find reservations expiring soon (within next 24 hours)
    @Query("SELECT br FROM BookReservation br WHERE br.status = 'ACTIVE' AND br.expiryDate BETWEEN :startDate AND :endDate")
    List<BookReservation> findReservationsExpiringSoon(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Check if student has active reservation for a book
    @Query("SELECT br FROM BookReservation br WHERE br.book.id = :bookId AND br.student.id = :studentId AND br.status = 'ACTIVE'")
    BookReservation findActiveReservation(@Param("bookId") Long bookId, @Param("studentId") Long studentId);
    
    // Count active reservations for a student
    @Query("SELECT COUNT(br) FROM BookReservation br WHERE br.student.id = :studentId AND br.status = 'ACTIVE'")
    Long countActiveReservationsByStudent(@Param("studentId") Long studentId);
    
    // Count active reservations for a book
    @Query("SELECT COUNT(br) FROM BookReservation br WHERE br.book.id = :bookId AND br.status = 'ACTIVE'")
    Long countActiveReservationsByBook(@Param("bookId") Long bookId);
    
    // Find reservations by date range
    @Query("SELECT br FROM BookReservation br WHERE br.createdAt BETWEEN :startDate AND :endDate")
    List<BookReservation> findReservationsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get reservation statistics
    @Query("SELECT COUNT(br) FROM BookReservation br WHERE br.status = 'ACTIVE'")
    Long countActiveReservations();
    
    @Query("SELECT COUNT(br) FROM BookReservation br WHERE br.status = 'FULFILLED'")
    Long countFulfilledReservations();
    
    @Query("SELECT COUNT(br) FROM BookReservation br WHERE br.status = 'EXPIRED'")
    Long countExpiredReservations();
    
    // Find reservations that need notification
    @Query("SELECT br FROM BookReservation br WHERE br.status = 'ACTIVE' AND br.notificationSent = false")
    List<BookReservation> findReservationsNeedingNotification();
    
    // Find reservations by priority
    List<BookReservation> findByPriorityOrderByCreatedAtAsc(Integer priority);
}

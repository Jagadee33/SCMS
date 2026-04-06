package com.college.repository;

import com.college.model.BookIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    
    // Find issues by student
    List<BookIssue> findByStudentId(Long studentId);
    
    // Find issues by book
    List<BookIssue> findByBookId(Long bookId);
    
    // Find issues by status
    List<BookIssue> findByStatus(BookIssue.IssueStatus status);
    
    // Find currently issued books (not returned)
    @Query("SELECT bi FROM BookIssue bi WHERE bi.status = 'ISSUED' AND bi.student.id = :studentId")
    List<BookIssue> findCurrentlyIssuedByStudent(@Param("studentId") Long studentId);
    
    // Find overdue books
    @Query("SELECT bi FROM BookIssue bi WHERE bi.status = 'ISSUED' AND bi.dueDate < :currentDate")
    List<BookIssue> findOverdueBooks(@Param("currentDate") LocalDate currentDate);
    
    // Find overdue books for a specific student
    @Query("SELECT bi FROM BookIssue bi WHERE bi.student.id = :studentId AND bi.status = 'ISSUED' AND bi.dueDate < :currentDate")
    List<BookIssue> findOverdueBooksByStudent(@Param("studentId") Long studentId, @Param("currentDate") LocalDate currentDate);
    
    // Find books due soon (within next 3 days)
    @Query("SELECT bi FROM BookIssue bi WHERE bi.status = 'ISSUED' AND bi.dueDate BETWEEN :startDate AND :endDate")
    List<BookIssue> findBooksDueSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find issue history for a student
    @Query("SELECT bi FROM BookIssue bi WHERE bi.student.id = :studentId ORDER BY bi.issueDate DESC")
    List<BookIssue> findIssueHistoryByStudent(@Param("studentId") Long studentId);
    
    // Find issue history for a book
    @Query("SELECT bi FROM BookIssue bi WHERE bi.book.id = :bookId ORDER BY bi.issueDate DESC")
    List<BookIssue> findIssueHistoryByBook(@Param("bookId") Long bookId);
    
    // Count currently issued books for a student
    @Query("SELECT COUNT(bi) FROM BookIssue bi WHERE bi.student.id = :studentId AND bi.status = 'ISSUED'")
    Long countCurrentlyIssuedByStudent(@Param("studentId") Long studentId);
    
    // Check if a book is currently issued to a student
    @Query("SELECT bi FROM BookIssue bi WHERE bi.book.id = :bookId AND bi.student.id = :studentId AND bi.status = 'ISSUED'")
    BookIssue findCurrentIssue(@Param("bookId") Long bookId, @Param("studentId") Long studentId);
    
    // Find issues by date range
    @Query("SELECT bi FROM BookIssue bi WHERE bi.issueDate BETWEEN :startDate AND :endDate")
    List<BookIssue> findIssuesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find returns by date range
    @Query("SELECT bi FROM BookIssue bi WHERE bi.returnDate BETWEEN :startDate AND :endDate")
    List<BookIssue> findReturnsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get statistics
    @Query("SELECT COUNT(bi) FROM BookIssue bi WHERE bi.status = 'ISSUED'")
    Long countCurrentlyIssued();
    
    @Query("SELECT COUNT(bi) FROM BookIssue bi WHERE bi.status = 'OVERDUE'")
    Long countOverdueBooks();
    
    @Query("SELECT COUNT(bi) FROM BookIssue bi WHERE bi.status = 'RETURNED' AND bi.returnDate >= :startDate")
    Long countReturnsSince(@Param("startDate") LocalDateTime startDate);
    
    // Get total fine amount
    @Query("SELECT COALESCE(SUM(bi.fineAmount), 0) FROM BookIssue bi WHERE bi.finePaid = false")
    Double getTotalUnpaidFines();
    
    // Get fine amount for student
    @Query("SELECT COALESCE(SUM(bi.fineAmount), 0) FROM BookIssue bi WHERE bi.student.id = :studentId AND bi.finePaid = false")
    Double getUnpaidFinesForStudent(@Param("studentId") Long studentId);
    
    // Find most active borrowers
    @Query("SELECT bi.student.id, COUNT(bi) as borrowCount FROM BookIssue bi " +
           "WHERE bi.status = 'ISSUED' OR bi.status = 'RETURNED' " +
           "GROUP BY bi.student.id ORDER BY borrowCount DESC")
    List<Object[]> findMostActiveBorrowers();
    
    // Find most popular books (by issue count)
    @Query("SELECT bi.book.id, COUNT(bi) as issueCount FROM BookIssue bi " +
           "GROUP BY bi.book.id ORDER BY issueCount DESC")
    List<Object[]> findMostPopularBooksByIssues();
}

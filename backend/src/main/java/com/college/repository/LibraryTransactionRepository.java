package com.college.repository;

import com.college.model.LibraryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LibraryTransactionRepository extends JpaRepository<LibraryTransaction, Long> {
    
    // Find transactions by student
    List<LibraryTransaction> findByStudentId(Long studentId);
    
    // Find transactions by librarian
    List<LibraryTransaction> findByLibrarianId(Long librarianId);
    
    // Find transactions by book
    List<LibraryTransaction> findByBookId(Long bookId);
    
    // Find transactions by type
    List<LibraryTransaction> findByTransactionType(LibraryTransaction.TransactionType transactionType);
    
    // Find transactions by date range
    @Query("SELECT lt FROM LibraryTransaction lt WHERE lt.transactionDate BETWEEN :startDate AND :endDate")
    List<LibraryTransaction> findTransactionsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find transactions for a student by date range
    @Query("SELECT lt FROM LibraryTransaction lt WHERE lt.student.id = :studentId AND lt.transactionDate BETWEEN :startDate AND :endDate")
    List<LibraryTransaction> findTransactionsByStudentAndDateRange(@Param("studentId") Long studentId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find recent transactions
    @Query("SELECT lt FROM LibraryTransaction lt ORDER BY lt.transactionDate DESC")
    List<LibraryTransaction> findRecentTransactions();
    
    // Find recent transactions for a student
    @Query("SELECT lt FROM LibraryTransaction lt WHERE lt.student.id = :studentId ORDER BY lt.transactionDate DESC")
    List<LibraryTransaction> findRecentTransactionsByStudent(@Param("studentId") Long studentId);
    
    // Get transaction statistics by type
    @Query("SELECT lt.transactionType, COUNT(lt) FROM LibraryTransaction lt GROUP BY lt.transactionType")
    List<Object[]> getTransactionStatisticsByType();
    
    // Get daily transaction statistics
    @Query("SELECT DATE(lt.transactionDate), COUNT(lt) FROM LibraryTransaction lt " +
           "WHERE lt.transactionDate >= :startDate GROUP BY DATE(lt.transactionDate)")
    List<Object[]> getDailyTransactionStatistics(@Param("startDate") LocalDateTime startDate);
    
    // Get monthly transaction statistics
    @Query("SELECT YEAR(lt.transactionDate), MONTH(lt.transactionDate), COUNT(lt) FROM LibraryTransaction lt " +
           "WHERE lt.transactionDate >= :startDate GROUP BY YEAR(lt.transactionDate), MONTH(lt.transactionDate)")
    List<Object[]> getMonthlyTransactionStatistics(@Param("startDate") LocalDateTime startDate);
    
    // Get total fine collected
    @Query("SELECT COALESCE(SUM(lt.fineAmount), 0) FROM LibraryTransaction lt WHERE lt.transactionType IN ('PAY_FINE', 'DAMAGE_FINE', 'LOST_FINE') AND lt.finePaid = true")
    Double getTotalFineCollected();
    
    // Get fine collected by date range
    @Query("SELECT COALESCE(SUM(lt.fineAmount), 0) FROM LibraryTransaction lt " +
           "WHERE lt.transactionType IN ('PAY_FINE', 'DAMAGE_FINE', 'LOST_FINE') AND lt.finePaid = true " +
           "AND lt.transactionDate BETWEEN :startDate AND :endDate")
    Double getFineCollectedByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get most active students (by transaction count)
    @Query("SELECT lt.student.id, COUNT(lt) as transactionCount FROM LibraryTransaction lt " +
           "GROUP BY lt.student.id ORDER BY transactionCount DESC")
    List<Object[]> findMostActiveStudents();
    
    // Get most issued books
    @Query("SELECT lt.book.id, COUNT(lt) as issueCount FROM LibraryTransaction lt " +
           "WHERE lt.transactionType = 'ISSUE' GROUP BY lt.book.id ORDER BY issueCount DESC")
    List<Object[]> findMostIssuedBooks();
    
    // Count transactions by type
    @Query("SELECT COUNT(lt) FROM LibraryTransaction lt WHERE lt.transactionType = :transactionType")
    Long countTransactionsByType(@Param("transactionType") LibraryTransaction.TransactionType transactionType);
    
    // Get today's transactions
    @Query("SELECT lt FROM LibraryTransaction lt WHERE DATE(lt.transactionDate) = CURRENT_DATE")
    List<LibraryTransaction> getTodaysTransactions();
    
    // Get unpaid fines
    @Query("SELECT lt FROM LibraryTransaction lt WHERE lt.fineAmount > 0 AND lt.finePaid = false")
    List<LibraryTransaction> getUnpaidFines();
    
    // Get unpaid fines for student
    @Query("SELECT lt FROM LibraryTransaction lt WHERE lt.student.id = :studentId AND lt.fineAmount > 0 AND lt.finePaid = false")
    List<LibraryTransaction> getUnpaidFinesForStudent(@Param("studentId") Long studentId);
}

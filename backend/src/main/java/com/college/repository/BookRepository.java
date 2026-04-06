package com.college.repository;

import com.college.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Find books by various criteria
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    List<Book> findByIsbn(String isbn);
    
    List<Book> findByCategory(String category);
    
    List<Book> findBySubcategory(String subcategory);
    
    List<Book> findByLanguage(String language);
    
    List<Book> findByPublisher(String publisher);
    
    // Search books by multiple criteria
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.publisher) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Book> searchBooks(@Param("search") String search);
    
    // Advanced search with multiple filters
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:category IS NULL OR b.category = :category) AND " +
           "(:subcategory IS NULL OR b.subcategory = :subcategory) AND " +
           "(:language IS NULL OR b.language = :language) AND " +
           "(:publisher IS NULL OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :publisher, '%')))")
    List<Book> advancedSearch(
        @Param("title") String title,
        @Param("author") String author,
        @Param("category") String category,
        @Param("subcategory") String subcategory,
        @Param("language") String language,
        @Param("publisher") String publisher
    );
    
    // Find available books
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 AND b.status = 'AVAILABLE'")
    List<Book> findAvailableBooks();
    
    // Find books by status
    List<Book> findByStatus(Book.BookStatus status);
    
    // Find digital books
    List<Book> findByIsDigitalTrue();
    
    // Find physical books
    List<Book> findByIsDigitalFalse();
    
    // Get books with low stock
    @Query("SELECT b FROM Book b WHERE b.availableCopies <= :threshold AND b.totalCopies > 0")
    List<Book> findLowStockBooks(@Param("threshold") Integer threshold);
    
    // Get most popular books (based on issue count)
    @Query("SELECT b, COUNT(bi) as issueCount FROM Book b " +
           "LEFT JOIN BookIssue bi ON b.id = bi.book.id " +
           "GROUP BY b.id ORDER BY issueCount DESC")
    List<Object[]> findMostPopularBooks();
    
    // Get recently added books
    @Query("SELECT b FROM Book b ORDER BY b.addedDate DESC")
    List<Book> findRecentlyAddedBooks();
    
    // Get books by location
    List<Book> findByBookLocation(String bookLocation);
    
    List<Book> findByRackNumber(String rackNumber);
    
    List<Book> findByShelfNumber(String shelfNumber);
    
    // Count books by category
    @Query("SELECT b.category, COUNT(b) FROM Book b WHERE b.category IS NOT NULL GROUP BY b.category")
    List<Object[]> countBooksByCategory();
    
    // Count books by status
    @Query("SELECT b.status, COUNT(b) FROM Book b GROUP BY b.status")
    List<Object[]> countBooksByStatus();
    
    // Check if ISBN exists
    boolean existsByIsbn(String isbn);
    
    // Get total books count
    @Query("SELECT COUNT(b) FROM Book b")
    Long getTotalBooksCount();
    
    // Get available books count
    @Query("SELECT COUNT(b) FROM Book b WHERE b.availableCopies > 0 AND b.status = 'AVAILABLE'")
    Long getAvailableBooksCount();
    
    // Get digital books count
    @Query("SELECT COUNT(b) FROM Book b WHERE b.isDigital = true")
    Long getDigitalBooksCount();
}

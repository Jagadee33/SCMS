package com.college.service;

import com.college.model.*;
import com.college.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LibraryService {
    
    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;
    private final BookReservationRepository bookReservationRepository;
    private final LibraryTransactionRepository libraryTransactionRepository;
    
    private static final Integer DEFAULT_ISSUE_PERIOD_DAYS = 14;
    private static final Integer MAX_BOOKS_PER_STUDENT = 5;
    private static final Double FINE_PER_DAY = 5.0;
    
    // Book Management
    public Book addBook(Book book) {
        book.setAddedDate(LocalDateTime.now());
        book.setLastUpdated(LocalDateTime.now());
        book.setAvailableCopies(book.getTotalCopies());
        book.setStatus(Book.BookStatus.AVAILABLE);
        
        Book savedBook = bookRepository.save(book);
        log.info("Added new book: {} (ISBN: {})", savedBook.getTitle(), savedBook.getIsbn());
        return savedBook;
    }
    
    public Book updateBook(Long bookId, Book bookDetails) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setPublisher(bookDetails.getPublisher());
        book.setCategory(bookDetails.getCategory());
        book.setSubcategory(bookDetails.getSubcategory());
        book.setLanguage(bookDetails.getLanguage());
        book.setEdition(bookDetails.getEdition());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setPages(bookDetails.getPages());
        book.setDescription(bookDetails.getDescription());
        book.setCoverImageUrl(bookDetails.getCoverImageUrl());
        book.setBookLocation(bookDetails.getBookLocation());
        book.setRackNumber(bookDetails.getRackNumber());
        book.setShelfNumber(bookDetails.getShelfNumber());
        book.setIsDigital(bookDetails.getIsDigital());
        book.setDigitalUrl(bookDetails.getDigitalUrl());
        book.setFileFormat(bookDetails.getFileFormat());
        book.setFileSize(bookDetails.getFileSize());
        book.setLastUpdated(LocalDateTime.now());
        
        Book updatedBook = bookRepository.save(book);
        log.info("Updated book: {} (ID: {})", updatedBook.getTitle(), updatedBook.getId());
        return updatedBook;
    }
    
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        // Check if book has any active issues
        List<BookIssue> activeIssues = bookIssueRepository.findByBookId(bookId);
        boolean hasActiveIssues = activeIssues.stream()
            .anyMatch(issue -> issue.getStatus() == BookIssue.IssueStatus.ISSUED);
        
        if (hasActiveIssues) {
            throw new RuntimeException("Cannot delete book with active issues");
        }
        
        bookRepository.delete(book);
        log.info("Deleted book: {} (ID: {})", book.getTitle(), bookId);
    }
    
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailableBooks();
    }
    
    public List<Book> searchBooks(String search) {
        return bookRepository.searchBooks(search);
    }
    
    public List<Book> advancedSearch(String title, String author, String category, 
                                String subcategory, String language, String publisher) {
        return bookRepository.advancedSearch(title, author, category, subcategory, language, publisher);
    }
    
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
    }
    
    // Book Issue Management
    public BookIssue issueBook(Long bookId, Long studentId, Long librarianId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        // Check if book is available
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Book is not available for issue");
        }
        
        // Check if student has reached maximum book limit
        Long currentIssues = bookIssueRepository.countCurrentlyIssuedByStudent(studentId);
        if (currentIssues >= MAX_BOOKS_PER_STUDENT) {
            throw new RuntimeException("Student has reached maximum book limit");
        }
        
        // Check if student already has this book
        BookIssue existingIssue = bookIssueRepository.findCurrentIssue(bookId, studentId);
        if (existingIssue != null) {
            throw new RuntimeException("Student already has this book issued");
        }
        
        // Create book issue
        User student = new User();
        student.setId(studentId);
        
        BookIssue bookIssue = BookIssue.builder()
            .book(book)
            .student(student)
            .issueDate(LocalDateTime.now())
            .dueDate(LocalDate.now().plusDays(DEFAULT_ISSUE_PERIOD_DAYS))
            .status(BookIssue.IssueStatus.ISSUED)
            .renewalCount(0)
            .maxRenewals(2)
            .fineAmount(0.0)
            .finePaid(false)
            .issuedBy(librarianId)
            .conditionIssued("Good")
            .createdAt(LocalDateTime.now())
            .build();
        
        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) {
            book.setStatus(Book.BookStatus.ISSUED);
        }
        bookRepository.save(book);
        
        BookIssue savedIssue = bookIssueRepository.save(bookIssue);
        
        // Create transaction record
        createTransaction(book.getId(), studentId, librarianId, LibraryTransaction.TransactionType.ISSUE, 
                        LocalDateTime.now(), LocalDateTime.now().plusDays(DEFAULT_ISSUE_PERIOD_DAYS), 0.0, null);
        
        log.info("Issued book {} to student {}", book.getTitle(), studentId);
        return savedIssue;
    }
    
    public BookIssue returnBook(Long issueId, Long librarianId, String conditionReturned) {
        BookIssue bookIssue = bookIssueRepository.findById(issueId)
            .orElseThrow(() -> new RuntimeException("Book issue not found with id: " + issueId));
        
        if (bookIssue.getStatus() != BookIssue.IssueStatus.ISSUED) {
            throw new RuntimeException("Book is not currently issued");
        }
        
        LocalDateTime returnDate = LocalDateTime.now();
        
        // Calculate fine if overdue
        Double fineAmount = 0.0;
        if (returnDate.toLocalDate().isAfter(bookIssue.getDueDate())) {
            long overdueDays = returnDate.toLocalDate().toEpochDay() - bookIssue.getDueDate().toEpochDay();
            fineAmount = overdueDays * FINE_PER_DAY;
        }
        
        // Update book issue
        bookIssue.setReturnDate(returnDate);
        bookIssue.setActualReturnDate(returnDate);
        bookIssue.setStatus(BookIssue.IssueStatus.RETURNED);
        bookIssue.setFineAmount(fineAmount);
        bookIssue.setConditionReturned(conditionReturned);
        bookIssue.setReturnedTo(librarianId);
        bookIssue.setUpdatedAt(LocalDateTime.now());
        
        BookIssue savedIssue = bookIssueRepository.save(bookIssue);
        
        // Update book availability
        Book book = bookIssue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        if (book.getAvailableCopies() > 0) {
            book.setStatus(Book.BookStatus.AVAILABLE);
        }
        bookRepository.save(book);
        
        // Create transaction record
        createTransaction(book.getId(), bookIssue.getStudent().getId(), librarianId, 
                        LibraryTransaction.TransactionType.RETURN, returnDate, returnDate, fineAmount, null);
        
        // Process reservations if any
        processReservations(book.getId());
        
        log.info("Returned book {} with fine amount {}", book.getTitle(), fineAmount);
        return savedIssue;
    }
    
    public BookIssue renewBook(Long issueId, Long librarianId) {
        BookIssue bookIssue = bookIssueRepository.findById(issueId)
            .orElseThrow(() -> new RuntimeException("Book issue not found with id: " + issueId));
        
        if (bookIssue.getStatus() != BookIssue.IssueStatus.ISSUED) {
            throw new RuntimeException("Book is not currently issued");
        }
        
        if (bookIssue.getRenewalCount() >= bookIssue.getMaxRenewals()) {
            throw new RuntimeException("Maximum renewal limit reached");
        }
        
        // Check if book has reservations
        List<BookReservation> activeReservations = bookReservationRepository.findActiveReservationsByBook(bookIssue.getBook().getId());
        if (!activeReservations.isEmpty()) {
            throw new RuntimeException("Cannot renew book with active reservations");
        }
        
        // Update book issue
        bookIssue.setDueDate(bookIssue.getDueDate().plusDays(DEFAULT_ISSUE_PERIOD_DAYS));
        bookIssue.setRenewalCount(bookIssue.getRenewalCount() + 1);
        bookIssue.setStatus(BookIssue.IssueStatus.RENEWED);
        bookIssue.setUpdatedAt(LocalDateTime.now());
        
        BookIssue savedIssue = bookIssueRepository.save(bookIssue);
        
        // Create transaction record
        createTransaction(bookIssue.getBook().getId(), bookIssue.getStudent().getId(), librarianId, 
                        LibraryTransaction.TransactionType.RENEW, LocalDateTime.now(), 
                        bookIssue.getDueDate().atStartOfDay(), 0.0, null);
        
        log.info("Renewed book {} for student {}", bookIssue.getBook().getTitle(), bookIssue.getStudent().getId());
        return savedIssue;
    }
    
    // Book Reservation Management
    public BookReservation reserveBook(Long bookId, Long studentId, String notes) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        // Check if student already has active reservation for this book
        BookReservation existingReservation = bookReservationRepository.findActiveReservation(bookId, studentId);
        if (existingReservation != null) {
            throw new RuntimeException("Student already has an active reservation for this book");
        }
        
        // Check if student has too many active reservations
        Long activeReservations = bookReservationRepository.countActiveReservationsByStudent(studentId);
        if (activeReservations >= 3) {
            throw new RuntimeException("Student has reached maximum reservation limit");
        }
        
        // Create reservation
        User student = new User();
        student.setId(studentId);
        
        BookReservation reservation = BookReservation.builder()
            .book(book)
            .student(student)
            .reservationDate(LocalDateTime.now())
            .expiryDate(LocalDateTime.now().plusDays(7))
            .status(BookReservation.ReservationStatus.ACTIVE)
            .priority(1)
            .notes(notes)
            .notificationSent(false)
            .createdAt(LocalDateTime.now())
            .build();
        
        BookReservation savedReservation = bookReservationRepository.save(reservation);
        
        // Create transaction record
        createTransaction(bookId, studentId, null, LibraryTransaction.TransactionType.RESERVE, 
                        LocalDateTime.now(), null, 0.0, notes);
        
        log.info("Reserved book {} for student {}", book.getTitle(), studentId);
        return savedReservation;
    }
    
    public void cancelReservation(Long reservationId, Long studentId) {
        BookReservation reservation = bookReservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));
        
        if (!reservation.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Cannot cancel reservation of another student");
        }
        
        if (reservation.getStatus() != BookReservation.ReservationStatus.ACTIVE) {
            throw new RuntimeException("Reservation is not active");
        }
        
        reservation.setStatus(BookReservation.ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());
        bookReservationRepository.save(reservation);
        
        // Create transaction record
        createTransaction(reservation.getBook().getId(), studentId, null, 
                        LibraryTransaction.TransactionType.CANCEL_RESERVATION, LocalDateTime.now(), null, 0.0, null);
        
        log.info("Cancelled reservation {} for book {}", reservationId, reservation.getBook().getTitle());
    }
    
    // Helper Methods
    private void createTransaction(Long bookId, Long studentId, Long librarianId, 
                              LibraryTransaction.TransactionType transactionType, LocalDateTime transactionDate,
                              LocalDateTime dueDate, Double fineAmount, String notes) {
        
        Book book = null;
        if (bookId != null) {
            book = new Book();
            book.setId(bookId);
        }
        
        User student = null;
        if (studentId != null) {
            student = new User();
            student.setId(studentId);
        }
        
        User librarian = null;
        if (librarianId != null) {
            librarian = new User();
            librarian.setId(librarianId);
        }
        
        LibraryTransaction transaction = LibraryTransaction.builder()
            .book(book)
            .student(student)
            .librarian(librarian)
            .transactionType(transactionType)
            .transactionDate(transactionDate)
            .dueDate(dueDate)
            .fineAmount(fineAmount)
            .finePaid(fineAmount == null || fineAmount == 0.0)
            .notes(notes)
            .createdAt(LocalDateTime.now())
            .build();
        
        libraryTransactionRepository.save(transaction);
    }
    
    private void processReservations(Long bookId) {
        List<BookReservation> activeReservations = bookReservationRepository.findActiveReservationsByBook(bookId);
        
        if (!activeReservations.isEmpty()) {
            BookReservation firstReservation = activeReservations.get(0);
            
            // Mark reservation as fulfilled
            firstReservation.setStatus(BookReservation.ReservationStatus.FULFILLED);
            firstReservation.setUpdatedAt(LocalDateTime.now());
            bookReservationRepository.save(firstReservation);
            
            log.info("Fulfilled reservation {} for book {}", firstReservation.getId(), bookId);
        }
    }
    
    // Statistics and Reports
    public LibraryStatistics getLibraryStatistics() {
        LibraryStatistics stats = new LibraryStatistics();
        
        stats.setTotalBooks(bookRepository.getTotalBooksCount());
        stats.setAvailableBooks(bookRepository.getAvailableBooksCount());
        stats.setDigitalBooks(bookRepository.getDigitalBooksCount());
        stats.setCurrentlyIssued(bookIssueRepository.countCurrentlyIssued());
        stats.setOverdueBooks(bookIssueRepository.countOverdueBooks());
        stats.setActiveReservations(bookReservationRepository.countActiveReservations());
        stats.setTotalUnpaidFines(bookIssueRepository.getTotalUnpaidFines());
        
        return stats;
    }
    
    public List<BookIssue> getStudentCurrentIssues(Long studentId) {
        return bookIssueRepository.findCurrentlyIssuedByStudent(studentId);
    }
    
    public List<BookIssue> getStudentIssueHistory(Long studentId) {
        return bookIssueRepository.findIssueHistoryByStudent(studentId);
    }
    
    public List<BookReservation> getStudentReservations(Long studentId) {
        return bookReservationRepository.findActiveReservationsByStudent(studentId);
    }
    
    public List<BookIssue> getOverdueBooks() {
        return bookIssueRepository.findOverdueBooks(LocalDate.now());
    }
    
    public List<Book> getLowStockBooks(Integer threshold) {
        return bookRepository.findLowStockBooks(threshold);
    }
    
    public List<Book> getRecentlyAddedBooks() {
        return bookRepository.findRecentlyAddedBooks();
    }
    
    public List<Object[]> getMostPopularBooks() {
        return bookRepository.findMostPopularBooks();
    }
    
    // Data class for statistics
    public static class LibraryStatistics {
        private Long totalBooks;
        private Long availableBooks;
        private Long digitalBooks;
        private Long currentlyIssued;
        private Long overdueBooks;
        private Long activeReservations;
        private Double totalUnpaidFines;
        
        // Getters and setters
        public Long getTotalBooks() { return totalBooks; }
        public void setTotalBooks(Long totalBooks) { this.totalBooks = totalBooks; }
        public Long getAvailableBooks() { return availableBooks; }
        public void setAvailableBooks(Long availableBooks) { this.availableBooks = availableBooks; }
        public Long getDigitalBooks() { return digitalBooks; }
        public void setDigitalBooks(Long digitalBooks) { this.digitalBooks = digitalBooks; }
        public Long getCurrentlyIssued() { return currentlyIssued; }
        public void setCurrentlyIssued(Long currentlyIssued) { this.currentlyIssued = currentlyIssued; }
        public Long getOverdueBooks() { return overdueBooks; }
        public void setOverdueBooks(Long overdueBooks) { this.overdueBooks = overdueBooks; }
        public Long getActiveReservations() { return activeReservations; }
        public void setActiveReservations(Long activeReservations) { this.activeReservations = activeReservations; }
        public Double getTotalUnpaidFines() { return totalUnpaidFines; }
        public void setTotalUnpaidFines(Double totalUnpaidFines) { this.totalUnpaidFines = totalUnpaidFines; }
    }
}

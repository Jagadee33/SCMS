package com.college.controller;

import com.college.model.*;
import com.college.service.LibraryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class LibraryController {
    
    private final LibraryService libraryService;
    
    // Book Management Endpoints
    @GetMapping("/books")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = libraryService.getAllBooks();
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/books/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        List<Book> books = libraryService.getAvailableBooks();
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/books/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = libraryService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    
    @GetMapping("/books/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String query) {
        List<Book> books = libraryService.searchBooks(query);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/books/advanced-search")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<List<Book>> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subcategory,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String publisher) {
        
        List<Book> books = libraryService.advancedSearch(title, author, category, subcategory, language, publisher);
        return ResponseEntity.ok(books);
    }
    
    @PostMapping("/books")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        Book savedBook = libraryService.addBook(book);
        return ResponseEntity.ok(savedBook);
    }
    
    @PutMapping("/books/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        Book updatedBook = libraryService.updateBook(id, bookDetails);
        return ResponseEntity.ok(updatedBook);
    }
    
    @DeleteMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        libraryService.deleteBook(id);
        return ResponseEntity.ok().build();
    }
    
    // Book Issue Management Endpoints
    @PostMapping("/books/{bookId}/issue")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<BookIssue> issueBook(
            @PathVariable Long bookId,
            @RequestParam Long studentId,
            @RequestParam(required = false) Long librarianId) {
        BookIssue bookIssue = libraryService.issueBook(bookId, studentId, librarianId);
        return ResponseEntity.ok(bookIssue);
    }
    
    @PostMapping("/issues/{issueId}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<BookIssue> returnBook(
            @PathVariable Long issueId,
            @RequestParam Long librarianId,
            @RequestParam(required = false) String conditionReturned) {
        BookIssue bookIssue = libraryService.returnBook(issueId, librarianId, conditionReturned);
        return ResponseEntity.ok(bookIssue);
    }
    
    @PostMapping("/issues/{issueId}/renew")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<BookIssue> renewBook(
            @PathVariable Long issueId,
            @RequestParam Long librarianId) {
        BookIssue bookIssue = libraryService.renewBook(issueId, librarianId);
        return ResponseEntity.ok(bookIssue);
    }
    
    @GetMapping("/issues/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<List<BookIssue>> getStudentCurrentIssues(@PathVariable Long studentId) {
        List<BookIssue> issues = libraryService.getStudentCurrentIssues(studentId);
        return ResponseEntity.ok(issues);
    }
    
    @GetMapping("/issues/student/{studentId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<List<BookIssue>> getStudentIssueHistory(@PathVariable Long studentId) {
        List<BookIssue> issues = libraryService.getStudentIssueHistory(studentId);
        return ResponseEntity.ok(issues);
    }
    
    @GetMapping("/issues/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<BookIssue>> getOverdueBooks() {
        List<BookIssue> overdueBooks = libraryService.getOverdueBooks();
        return ResponseEntity.ok(overdueBooks);
    }
    
    // Book Reservation Management Endpoints
    @PostMapping("/books/{bookId}/reserve")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<BookReservation> reserveBook(
            @PathVariable Long bookId,
            @RequestParam Long studentId,
            @RequestParam(required = false) String notes) {
        BookReservation reservation = libraryService.reserveBook(bookId, studentId, notes);
        return ResponseEntity.ok(reservation);
    }
    
    @PostMapping("/reservations/{reservationId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long reservationId,
            @RequestParam Long studentId) {
        libraryService.cancelReservation(reservationId, studentId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/reservations/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<List<BookReservation>> getStudentReservations(@PathVariable Long studentId) {
        List<BookReservation> reservations = libraryService.getStudentReservations(studentId);
        return ResponseEntity.ok(reservations);
    }
    
    // Statistics and Reports Endpoints
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<LibraryService.LibraryStatistics> getLibraryStatistics() {
        LibraryService.LibraryStatistics stats = libraryService.getLibraryStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/books/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<Book>> getLowStockBooks(@RequestParam(defaultValue = "3") Integer threshold) {
        List<Book> lowStockBooks = libraryService.getLowStockBooks(threshold);
        return ResponseEntity.ok(lowStockBooks);
    }
    
    @GetMapping("/books/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<List<Book>> getRecentlyAddedBooks() {
        List<Book> recentBooks = libraryService.getRecentlyAddedBooks();
        return ResponseEntity.ok(recentBooks);
    }
    
    @GetMapping("/books/popular")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<List<Map<String, Object>>> getMostPopularBooks() {
        List<Object[]> popularBooks = libraryService.getMostPopularBooks();
        
        List<Map<String, Object>> result = popularBooks.stream()
            .map(row -> {
                Map<String, Object> bookData = new HashMap<>();
                bookData.put("book", row[0]);
                bookData.put("issueCount", row[1]);
                return bookData;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }
    
    // Dashboard Data for Students
    @GetMapping("/dashboard/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> getStudentLibraryDashboard(@PathVariable Long studentId) {
        List<BookIssue> currentIssues = libraryService.getStudentCurrentIssues(studentId);
        List<BookReservation> reservations = libraryService.getStudentReservations(studentId);
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("currentIssues", currentIssues);
        dashboard.put("reservations", reservations);
        dashboard.put("totalBooksIssued", currentIssues.size());
        dashboard.put("totalReservations", reservations.size());
        
        return ResponseEntity.ok(dashboard);
    }
    
    // Error Handling
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("Library operation error: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}

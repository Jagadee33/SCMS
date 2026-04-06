package com.college.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(nullable = false)
    private String isbn;
    
    @Column(nullable = false)
    private String publisher;
    
    private String edition;
    
    private Integer publicationYear;
    
    private String category;
    
    private String subcategory;
    
    @Column(nullable = false)
    private String language;
    
    private Integer pages;
    
    private String description;
    
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    
    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;
    
    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;
    
    @Column(name = "book_location")
    private String bookLocation;
    
    @Column(name = "rack_number")
    private String rackNumber;
    
    @Column(name = "shelf_number")
    private String shelfNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;
    
    @Column(name = "added_by")
    private Long addedBy;
    
    @Column(name = "added_date", nullable = false)
    private LocalDateTime addedDate;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @Column(name = "is_digital", nullable = false)
    @Builder.Default
    private Boolean isDigital = false;
    
    @Column(name = "digital_url")
    private String digitalUrl;
    
    @Column(name = "file_format")
    private String fileFormat;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    public enum BookStatus {
        AVAILABLE, ISSUED, RESERVED, DAMAGED, LOST, UNDER_REPAIR, DIGITAL_ONLY
    }
}

package com.college.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.model.Fee;

import java.time.LocalDateTime;
import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, Long> {
    
    List<Fee> findByStudentId(Long studentId);
    
    List<Fee> findByStudentIdAndStatus(Long studentId, String status);
    
    List<Fee> findByStatus(String status);
    
    List<Fee> findByFeeType(String feeType);
    
    List<Fee> findByDueDateBefore(LocalDateTime date);
    
    List<Fee> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Fee> findByAcademicYear(String academicYear);
    
    List<Fee> findByAcademicYearAndSemester(String academicYear, String semester);
    
    List<Fee> findByStudentIdAndAcademicYear(Long studentId, String academicYear);
}

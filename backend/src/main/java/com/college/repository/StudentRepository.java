package com.college.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.college.model.Student;
import java.time.LocalDate;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = :status")
    long countByStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.enrollmentDate >= :date")
    long countRecentEnrollments(@Param("date") LocalDate date);
}

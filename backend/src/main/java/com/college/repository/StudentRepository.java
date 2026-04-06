package com.college.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.college.model.Student;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = :status")
    long countByStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.enrollmentDate >= :date")
    long countRecentEnrollments(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.role = :role")
    long countByRole(@Param("role") String role);
    
    @Query("SELECT s FROM Student s WHERE s.role = :role")
    List<Student> findByRole(@Param("role") String role);
}

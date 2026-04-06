package com.college.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.model.Examination;
import com.college.model.Course;
import com.college.model.Faculty;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {
    
    List<Examination> findByCourseId(Long courseId);
    
    List<Examination> findByFacultyId(Long facultyId);
    
    List<Examination> findByStatus(String status);
    
    List<Examination> findByExamDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Examination> findByCourseIdAndExamDateBetween(Long courseId, LocalDateTime startDate, LocalDateTime endDate);
}

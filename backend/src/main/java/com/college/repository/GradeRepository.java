package com.college.repository;

import com.college.model.Grade;
import com.college.model.Student;
import com.college.model.Course;
import com.college.model.Faculty;
import com.college.model.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Find grades by student
    List<Grade> findByStudent(Student student);
    
    // Find grades by course
    List<Grade> findByCourse(Course course);
    
    // Find grades by faculty
    List<Grade> findByFaculty(Faculty faculty);
    
    // Find grades by examination
    List<Grade> findByExamination(Examination examination);
    
    // Find grades by student and course
    List<Grade> findByStudentAndCourse(Student student, Course course);
    
    // Find grades by student and course and grade type
    List<Grade> findByStudentAndCourseAndGradeType(Student student, Course course, String gradeType);
    
    // Find published grades by student
    List<Grade> findByStudentAndStatus(Student student, String status);
    
    // Find grades by student and date range
    @Query("SELECT g FROM Grade g WHERE g.student = :student AND g.gradedDate BETWEEN :startDate AND :endDate")
    List<Grade> findByStudentAndDateRange(@Param("student") Student student, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    // Find grades by course and grade type
    List<Grade> findByCourseAndGradeType(Course course, String gradeType);
    
    // Find grades by course and status
    List<Grade> findByCourseAndStatus(Course course, String status);
    
    // Find grades by faculty and status
    List<Grade> findByFacultyAndStatus(Faculty faculty, String status);
    
    // Calculate student's GPA for a specific semester/academic year
    @Query("SELECT AVG(g.gradePoints) FROM Grade g WHERE g.student = :student AND g.status = 'PUBLISHED'")
    Double calculateStudentGPA(@Param("student") Student student);
    
    // Calculate student's GPA for a specific course
    @Query("SELECT AVG(g.gradePoints) FROM Grade g WHERE g.student = :student AND g.course = :course AND g.status = 'PUBLISHED'")
    Double calculateCourseGPA(@Param("student") Student student, @Param("course") Course course);
    
    // Get student's grade statistics for a course
    @Query("SELECT g.gradeType, COUNT(g), AVG(g.obtainedMarks), AVG(g.percentage) FROM Grade g " +
           "WHERE g.student = :student AND g.course = :course AND g.status = 'PUBLISHED' " +
           "GROUP BY g.gradeType")
    List<Object[]> getStudentGradeStatsByCourse(@Param("student") Student student, @Param("course") Course course);
    
    // Get course grade statistics
    @Query("SELECT g.gradeLetter, COUNT(g) FROM Grade g WHERE g.course = :course AND g.status = 'PUBLISHED' GROUP BY g.gradeLetter")
    List<Object[]> getCourseGradeDistribution(@Param("course") Course course);
    
    // Get faculty's grade statistics
    @Query("SELECT COUNT(g), AVG(g.obtainedMarks), AVG(g.percentage) FROM Grade g WHERE g.faculty = :faculty AND g.status = 'PUBLISHED'")
    List<Object[]> getFacultyGradeStats(@Param("faculty") Faculty faculty);
    
    // Find grades with feedback for student
    @Query("SELECT g FROM Grade g WHERE g.student = :student AND g.feedback IS NOT NULL AND g.feedback != '' AND g.status = 'PUBLISHED'")
    List<Grade> findGradesWithFeedback(@Param("student") Student student);
    
    // Count grades by status
    long countByStatus(String status);
    
    // Find recent grades for student (last N grades)
    @Query("SELECT g FROM Grade g WHERE g.student = :student AND g.status = 'PUBLISHED' ORDER BY g.gradedDate DESC")
    List<Grade> findRecentGradesByStudent(@Param("student") Student student);
    
    // Find pending grades for faculty
    List<Grade> findByFacultyAndStatusAndGradeType(Faculty faculty, String status, String gradeType);
    
    // Check if grade exists for student, course, and grade type
    boolean existsByStudentAndCourseAndGradeType(Student student, Course course, String gradeType);
}

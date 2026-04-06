package com.college.repository;

import com.college.model.ExamRegistration;
import com.college.model.Student;
import com.college.model.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRegistrationRepository extends JpaRepository<ExamRegistration, Long> {

    // Find by student
    List<ExamRegistration> findByStudent(Student student);
    
    // Find by examination
    List<ExamRegistration> findByExamination(Examination examination);
    
    // Find by student and examination
    Optional<ExamRegistration> findByStudentAndExamination(Student student, Examination examination);
    
    // Find by status
    List<ExamRegistration> findByStatus(String status);
    
    // Find by student and status
    List<ExamRegistration> findByStudentAndStatus(Student student, String status);
    
    // Find by examination and status
    List<ExamRegistration> findByExaminationAndStatus(Examination examination, String status);
    
    // Find by exam attendance
    List<ExamRegistration> findByExamAttendance(Boolean examAttendance);
    
    // Find by registration date range
    @Query("SELECT er FROM ExamRegistration er WHERE er.registrationDate BETWEEN :startDate AND :endDate")
    List<ExamRegistration> findByRegistrationDateRange(@Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    // Find upcoming exams for student
    @Query("SELECT er FROM ExamRegistration er WHERE er.student = :student AND er.examination.examDate > :now AND er.status = 'APPROVED'")
    List<ExamRegistration> findUpcomingExamsForStudent(@Param("student") Student student, @Param("now") LocalDateTime now);
    
    // Find completed exams for student
    @Query("SELECT er FROM ExamRegistration er WHERE er.student = :student AND er.examination.examDate < :now AND er.status IN ('COMPLETED', 'ABSENT')")
    List<ExamRegistration> findCompletedExamsForStudent(@Param("student") Student student, @Param("now") LocalDateTime now);
    
    // Count registrations by examination and status
    long countByExaminationAndStatus(Examination examination, String status);
    
    // Count registrations by examination
    long countByExamination(Examination examination);
    
    // Find registrations requiring approval
    List<ExamRegistration> findByStatusAndExaminationRequiresApproval(String status, boolean requiresApproval);
    
    // Check if student is registered for examination
    boolean existsByStudentAndExamination(Student student, Examination examination);
    
    // Get exam statistics for examination
    @Query("SELECT er.status, COUNT(er) FROM ExamRegistration er WHERE er.examination = :examination GROUP BY er.status")
    List<Object[]> getExamStatisticsByExamination(@Param("examination") Examination examination);
    
    // Get attendance statistics for examination
    @Query("SELECT COUNT(er), SUM(CASE WHEN er.examAttendance = true THEN 1 ELSE 0 END), SUM(CASE WHEN er.examAttendance = false THEN 1 ELSE 0 END) " +
           "FROM ExamRegistration er WHERE er.examination = :examination AND er.status = 'COMPLETED'")
    List<Object[]> getAttendanceStatisticsByExamination(@Param("examination") Examination examination);
    
    // Find students who attended exam
    @Query("SELECT er.student FROM ExamRegistration er WHERE er.examination = :examination AND er.examAttendance = true")
    List<Student> findStudentsWhoAttended(@Param("examination") Examination examination);
    
    // Find students who were absent
    @Query("SELECT er.student FROM ExamRegistration er WHERE er.examination = :examination AND er.examAttendance = false")
    List<Student> findStudentsWhoWereAbsent(@Param("examination") Examination examination);
    
    // Find by examination ID
    @Query("SELECT er FROM ExamRegistration er WHERE er.examination.id = :examinationId")
    List<ExamRegistration> findByExaminationId(Long examinationId);
    
    // Check if student is registered for examination by IDs
    @Query("SELECT CASE WHEN COUNT(er) > 0 THEN true ELSE false END FROM ExamRegistration er WHERE er.student.id = :studentId AND er.examination.id = :examinationId")
    boolean existsByStudentIdAndExaminationId(Long studentId, Long examinationId);
}

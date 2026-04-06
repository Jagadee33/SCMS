package com.college.repository;

import com.college.model.Enrollment;
import com.college.model.Student;
import com.college.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Find enrollments by student
    List<Enrollment> findByStudent(Student student);

    // Find enrollments by course
    List<Enrollment> findByCourse(Course course);

    // Find enrollments by student and status
    List<Enrollment> findByStudentAndStatus(Student student, String status);

    // Find enrollments by course and status
    List<Enrollment> findByCourseAndStatus(Course course, String status);

    // Find specific enrollment by student and course
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);

    // Find active enrollments for a student
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND (e.status = 'ENROLLED' OR e.status = 'IN_PROGRESS')")
    List<Enrollment> findActiveEnrollmentsByStudent(@Param("student") Student student);

    // Find completed enrollments for a student
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND e.status = 'COMPLETED'")
    List<Enrollment> findCompletedEnrollmentsByStudent(@Param("student") Student student);

    // Count enrollments by course
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course = :course AND (e.status = 'ENROLLED' OR e.status = 'IN_PROGRESS')")
    Long countActiveEnrollmentsByCourse(@Param("course") Course course);

    // Find enrollments by semester and academic year
    List<Enrollment> findBySemesterAndAcademicYear(String semester, String academicYear);

    // Find enrollments by enrollment date range
    @Query("SELECT e FROM Enrollment e WHERE e.enrollmentDate BETWEEN :startDate AND :endDate")
    List<Enrollment> findByEnrollmentDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Check if student is enrolled in course
    boolean existsByStudentAndCourse(Student student, Course course);

    // Get enrollment statistics for a course
    @Query("SELECT e.status, COUNT(e) FROM Enrollment e WHERE e.course = :course GROUP BY e.status")
    List<Object[]> getEnrollmentStatsByCourse(@Param("course") Course course);

    // Get student's GPA
    @Query("SELECT AVG(e.grade) FROM Enrollment e WHERE e.student = :student AND e.status = 'COMPLETED' AND e.grade IS NOT NULL")
    Double calculateStudentGPA(@Param("student") Student student);

    // Get credits earned by student
    @Query("SELECT COALESCE(SUM(e.creditsEarned), 0) FROM Enrollment e WHERE e.student = :student AND e.status = 'COMPLETED'")
    Double calculateCreditsEarned(@Param("student") Student student);

    // Count enrollments by course (all statuses)
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course = :course")
    Long countByCourse(@Param("course") Course course);

    // Count enrollments by date range
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.enrollmentDate BETWEEN :startDate AND :endDate")
    Long countByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

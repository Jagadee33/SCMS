package com.college.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.model.Attendance;
import com.college.model.Student;
import com.college.model.Course;
import com.college.model.Faculty;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByStudentId(Long studentId);
    
    List<Attendance> findByCourseId(Long courseId);
    
    List<Attendance> findByFacultyId(Long facultyId);
    
    List<Attendance> findByStudentIdAndAttendanceDateBetween(Long studentId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Attendance> findByCourseIdAndAttendanceDateBetween(Long courseId, LocalDateTime startDate, LocalDateTime endDate);
    
    Optional<Attendance> findByStudentIdAndCourseIdAndAttendanceDate(Long studentId, Long courseId, LocalDateTime attendanceDate);
}

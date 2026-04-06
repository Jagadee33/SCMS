package com.college.service;

import com.college.model.Attendance;
import com.college.model.Student;
import com.college.model.Course;
import com.college.model.Faculty;
import com.college.repository.AttendanceRepository;
import com.college.repository.StudentRepository;
import com.college.repository.CourseRepository;
import com.college.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public List<Attendance> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public List<Attendance> getAttendanceByCourse(Long courseId) {
        return attendanceRepository.findByCourseId(courseId);
    }

    public List<Attendance> getAttendanceByFaculty(Long facultyId) {
        return attendanceRepository.findByFacultyId(facultyId);
    }

    public Attendance getAttendanceById(Long id) {
        Optional<Attendance> attendance = attendanceRepository.findById(id);
        return attendance.orElse(null);
    }

    public Attendance markAttendance(Attendance attendance) {
        // Validate that student, course, and faculty exist
        if (attendance.getStudent() == null || attendance.getStudent().getId() == null) {
            throw new RuntimeException("Student is required");
        }
        if (attendance.getCourse() == null || attendance.getCourse().getId() == null) {
            throw new RuntimeException("Course is required");
        }
        if (attendance.getFaculty() == null || attendance.getFaculty().getId() == null) {
            throw new RuntimeException("Faculty is required");
        }

        // Set default values
        if (attendance.getStatus() == null) {
            attendance.setStatus("Present");
        }
        if (attendance.getAttendanceDate() == null) {
            attendance.setAttendanceDate(LocalDateTime.now());
        }

        return attendanceRepository.save(attendance);
    }

    public Attendance updateAttendance(Long id, Attendance attendanceDetails) {
        Optional<Attendance> optionalAttendance = attendanceRepository.findById(id);
        if (optionalAttendance.isPresent()) {
            Attendance attendance = optionalAttendance.get();
            attendance.setStudent(attendanceDetails.getStudent());
            attendance.setCourse(attendanceDetails.getCourse());
            attendance.setFaculty(attendanceDetails.getFaculty());
            attendance.setAttendanceDate(attendanceDetails.getAttendanceDate());
            attendance.setCheckInTime(attendanceDetails.getCheckInTime());
            attendance.setCheckOutTime(attendanceDetails.getCheckOutTime());
            attendance.setStatus(attendanceDetails.getStatus());
            attendance.setRemarks(attendanceDetails.getRemarks());
            return attendanceRepository.save(attendance);
        }
        return null;
    }

    public boolean deleteAttendance(Long id) {
        if (attendanceRepository.existsById(id)) {
            attendanceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Attendance> getStudentAttendanceReport(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return attendanceRepository.findByStudentIdAndAttendanceDateBetween(studentId, startDate, endDate);
    }

    public List<Attendance> getCourseAttendanceReport(Long courseId, LocalDateTime startDate, LocalDateTime endDate) {
        return attendanceRepository.findByCourseIdAndAttendanceDateBetween(courseId, startDate, endDate);
    }
}

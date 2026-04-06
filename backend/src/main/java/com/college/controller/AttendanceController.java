package com.college.controller;

import com.college.model.Attendance;
import com.college.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
@CrossOrigin(origins = "http://localhost:3000")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        List<Attendance> attendance = attendanceService.getAllAttendance();
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Long id) {
        Attendance attendance = attendanceService.getAttendanceById(id);
        if (attendance != null) {
            return ResponseEntity.ok(attendance);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getStudentAttendance(@PathVariable Long studentId) {
        List<Attendance> attendance = attendanceService.getAttendanceByStudent(studentId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Attendance>> getCourseAttendance(@PathVariable Long courseId) {
        List<Attendance> attendance = attendanceService.getAttendanceByCourse(courseId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<Attendance>> getFacultyAttendance(@PathVariable Long facultyId) {
        List<Attendance> attendance = attendanceService.getAttendanceByFaculty(facultyId);
        return ResponseEntity.ok(attendance);
    }

    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(@RequestBody Attendance attendance) {
        try {
            Attendance markedAttendance = attendanceService.markAttendance(attendance);
            return ResponseEntity.ok(markedAttendance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/mark-batch")
    public ResponseEntity<List<Attendance>> markBatchAttendance(@RequestBody List<Attendance> attendanceList) {
        try {
            List<Attendance> markedAttendance = attendanceList.stream()
                .map(attendanceService::markAttendance)
                .collect(Collectors.toList());
            return ResponseEntity.ok(markedAttendance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(
            @PathVariable Long id, 
            @RequestBody Attendance attendance) {
        Attendance updatedAttendance = attendanceService.updateAttendance(id, attendance);
        if (updatedAttendance != null) {
            return ResponseEntity.ok(updatedAttendance);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        boolean deleted = attendanceService.deleteAttendance(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/report/student/{studentId}")
    public ResponseEntity<List<Attendance>> getStudentAttendanceReport(
            @PathVariable Long studentId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();
        
        List<Attendance> attendance = attendanceService.getStudentAttendanceReport(studentId, start, end);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/report/course/{courseId}")
    public ResponseEntity<List<Attendance>> getCourseAttendanceReport(
            @PathVariable Long courseId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();
        
        List<Attendance> attendance = attendanceService.getCourseAttendanceReport(courseId, start, end);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAttendanceStats() {
        List<Attendance> allAttendance = attendanceService.getAllAttendance();
        
        long total = allAttendance.size();
        long present = allAttendance.stream().filter(a -> "Present".equals(a.getStatus())).count();
        long absent = allAttendance.stream().filter(a -> "Absent".equals(a.getStatus())).count();
        long late = allAttendance.stream().filter(a -> "Late".equals(a.getStatus())).count();
        long excused = allAttendance.stream().filter(a -> "Excused".equals(a.getStatus())).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("present", present);
        stats.put("absent", absent);
        stats.put("late", late);
        stats.put("excused", excused);
        stats.put("attendanceRate", total > 0 ? (double) present / total * 100 : 0);
        
        return ResponseEntity.ok(stats);
    }
}

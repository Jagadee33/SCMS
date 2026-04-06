package com.college.controller;

import com.college.model.ExamRegistration;
import com.college.service.ExamRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam-registrations")
@CrossOrigin(origins = "http://localhost:3000")
public class ExamRegistrationController {

    @Autowired
    private ExamRegistrationService examRegistrationService;

    // Basic CRUD operations
    @GetMapping
    public ResponseEntity<List<ExamRegistration>> getAllExamRegistrations() {
        List<ExamRegistration> registrations = examRegistrationService.getAllExamRegistrations();
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamRegistration> getExamRegistrationById(@PathVariable Long id) {
        ExamRegistration registration = examRegistrationService.getExamRegistrationById(id);
        if (registration != null) {
            return ResponseEntity.ok(registration);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStudentForExam(
            @RequestParam Long studentId,
            @RequestParam Long examinationId,
            @RequestParam(required = false) String specialRequirements) {
        try {
            ExamRegistration registration = examRegistrationService.registerStudentForExam(studentId, examinationId, specialRequirements);
            return ResponseEntity.ok(registration);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ExamRegistration> approveRegistration(
            @PathVariable Long id,
            @RequestParam(required = false) String approvalNotes) {
        try {
            ExamRegistration registration = examRegistrationService.approveRegistration(id, approvalNotes);
            return ResponseEntity.ok(registration);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ExamRegistration> rejectRegistration(
            @PathVariable Long id,
            @RequestParam(required = false) String approvalNotes) {
        try {
            ExamRegistration registration = examRegistrationService.rejectRegistration(id, approvalNotes);
            return ResponseEntity.ok(registration);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/attendance")
    public ResponseEntity<ExamRegistration> markAttendance(
            @PathVariable Long id,
            @RequestParam Boolean attended) {
        try {
            ExamRegistration registration = examRegistrationService.markAttendance(id, attended);
            return ResponseEntity.ok(registration);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long id) {
        boolean deleted = examRegistrationService.deleteRegistration(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Student exam operations
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ExamRegistration>> getStudentExamRegistrations(@PathVariable Long studentId) {
        List<ExamRegistration> registrations = examRegistrationService.getStudentExamRegistrations(studentId);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/student/{studentId}/upcoming")
    public ResponseEntity<List<ExamRegistration>> getUpcomingExamsForStudent(@PathVariable Long studentId) {
        List<ExamRegistration> registrations = examRegistrationService.getUpcomingExamsForStudent(studentId);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/student/{studentId}/completed")
    public ResponseEntity<List<ExamRegistration>> getCompletedExamsForStudent(@PathVariable Long studentId) {
        List<ExamRegistration> registrations = examRegistrationService.getCompletedExamsForStudent(studentId);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/student/{studentId}/report")
    public ResponseEntity<Map<String, Object>> getStudentExamReport(@PathVariable Long studentId) {
        Map<String, Object> report = examRegistrationService.getStudentExamReport(studentId);
        if (report != null) {
            return ResponseEntity.ok(report);
        }
        return ResponseEntity.notFound().build();
    }

    // Examination operations
    @GetMapping("/examination/{examinationId}")
    public ResponseEntity<List<ExamRegistration>> getExaminationRegistrations(@PathVariable Long examinationId) {
        List<ExamRegistration> registrations = examRegistrationService.getExaminationRegistrations(examinationId);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/examination/{examinationId}/statistics")
    public ResponseEntity<Map<String, Object>> getExaminationStatistics(@PathVariable Long examinationId) {
        Map<String, Object> statistics = examRegistrationService.getExaminationStatistics(examinationId);
        if (statistics != null) {
            return ResponseEntity.ok(statistics);
        }
        return ResponseEntity.notFound().build();
    }

    // Faculty operations
    @GetMapping("/faculty/{facultyId}/pending")
    public ResponseEntity<List<ExamRegistration>> getPendingApprovalsForFaculty(@PathVariable Long facultyId) {
        List<ExamRegistration> registrations = examRegistrationService.getPendingApprovalsForFaculty(facultyId);
        return ResponseEntity.ok(registrations);
    }

    // Batch operations
    @PutMapping("/batch/approve")
    public ResponseEntity<List<ExamRegistration>> approveMultipleRegistrations(
            @RequestBody List<Long> registrationIds,
            @RequestParam(required = false) String approvalNotes) {
        try {
            List<ExamRegistration> registrations = examRegistrationService.approveMultipleRegistrations(registrationIds, approvalNotes);
            return ResponseEntity.ok(registrations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/batch/attendance")
    public ResponseEntity<List<ExamRegistration>> markMultipleAttendances(@RequestBody Map<Long, Boolean> attendanceData) {
        try {
            List<ExamRegistration> registrations = examRegistrationService.markMultipleAttendances(attendanceData);
            return ResponseEntity.ok(registrations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Utility endpoints
    @GetMapping("/check-registration")
    public ResponseEntity<Boolean> checkRegistration(
            @RequestParam Long studentId,
            @RequestParam Long examinationId) {
        boolean isRegistered = examRegistrationService.isStudentRegistered(studentId, examinationId);
        return ResponseEntity.ok(isRegistered);
    }

    @GetMapping("/examination/{examinationId}/attended")
    public ResponseEntity<List<Object>> getStudentsWhoAttended(@PathVariable Long examinationId) {
        List<Object> students = (List<Object>) (Object) examRegistrationService.getStudentsWhoAttended(examinationId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/examination/{examinationId}/absent")
    public ResponseEntity<List<Object>> getStudentsWhoWereAbsent(@PathVariable Long examinationId) {
        List<Object> students = (List<Object>) (Object) examRegistrationService.getStudentsWhoWereAbsent(examinationId);
        return ResponseEntity.ok(students);
    }

    // Statistics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getRegistrationStatistics() {
        List<ExamRegistration> allRegistrations = examRegistrationService.getAllExamRegistrations();
        
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalRegistrations", allRegistrations.size());
        
        // Count by status
        long registeredCount = allRegistrations.stream().filter(r -> "REGISTERED".equals(r.getStatus())).count();
        long approvedCount = allRegistrations.stream().filter(r -> "APPROVED".equals(r.getStatus())).count();
        long rejectedCount = allRegistrations.stream().filter(r -> "REJECTED".equals(r.getStatus())).count();
        long completedCount = allRegistrations.stream().filter(r -> "COMPLETED".equals(r.getStatus())).count();
        long absentCount = allRegistrations.stream().filter(r -> "ABSENT".equals(r.getStatus())).count();
        
        stats.put("registered", registeredCount);
        stats.put("approved", approvedCount);
        stats.put("rejected", rejectedCount);
        stats.put("completed", completedCount);
        stats.put("absent", absentCount);
        
        // Attendance statistics
        long attendedCount = allRegistrations.stream().mapToLong(r -> r.hasAttended() ? 1 : 0).sum();
        double attendanceRate = (completedCount + absentCount) > 0 ? 
            (double) attendedCount / (completedCount + absentCount) * 100 : 0;
        stats.put("attendanceRate", attendanceRate);
        
        return ResponseEntity.ok(stats);
    }
}

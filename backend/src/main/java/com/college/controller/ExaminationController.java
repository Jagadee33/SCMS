package com.college.controller;

import com.college.model.Examination;
import com.college.service.ExaminationService;
import com.college.service.ExamRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/examinations")
@CrossOrigin(origins = "http://localhost:3000")
public class ExaminationController {

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private ExamRegistrationService examRegistrationService;

    @GetMapping
    public ResponseEntity<List<Examination>> getAllExaminations() {
        List<Examination> examinations = examinationService.getAllExaminations();
        return ResponseEntity.ok(examinations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Examination> getExaminationById(@PathVariable Long id) {
        Examination examination = examinationService.getExaminationById(id);
        if (examination != null) {
            return ResponseEntity.ok(examination);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Examination> createExamination(@RequestBody Examination examination) {
        try {
            Examination createdExamination = examinationService.createExamination(examination);
            return ResponseEntity.ok(createdExamination);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Examination> updateExamination(
            @PathVariable Long id, 
            @RequestBody Examination examination) {
        Examination updatedExamination = examinationService.updateExamination(id, examination);
        if (updatedExamination != null) {
            return ResponseEntity.ok(updatedExamination);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamination(@PathVariable Long id) {
        try {
            boolean deleted = examinationService.deleteExamination(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Examination>> getExaminationsByCourse(@PathVariable Long courseId) {
        List<Examination> examinations = examinationService.getExaminationsByCourse(courseId);
        return ResponseEntity.ok(examinations);
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<Examination>> getExaminationsByFaculty(@PathVariable Long facultyId) {
        List<Examination> examinations = examinationService.getExaminationsByFaculty(facultyId);
        return ResponseEntity.ok(examinations);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Examination>> getStudentExaminations(@PathVariable Long studentId) {
        // Get available examinations for student to register
        List<Examination> availableExams = examinationService.getAvailableExaminationsForStudent(studentId);
        return ResponseEntity.ok(availableExams);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Examination>> getExaminationsByStatus(@PathVariable String status) {
        List<Examination> examinations = examinationService.getExaminationsByStatus(status);
        return ResponseEntity.ok(examinations);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Examination>> getExaminationsByDateRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();
        
        List<Examination> examinations = examinationService.getExaminationsByDateRange(start, end);
        return ResponseEntity.ok(examinations);
    }

    @GetMapping("/course/{courseId}/date-range")
    public ResponseEntity<List<Examination>> getCourseExaminationsByDateRange(
            @PathVariable Long courseId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();
        
        List<Examination> examinations = examinationService.getCourseExaminationsByDateRange(courseId, start, end);
        return ResponseEntity.ok(examinations);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getExaminationStats() {
        List<Examination> allExaminations = examinationService.getAllExaminations();
        
        long total = allExaminations.size();
        long upcoming = allExaminations.stream().filter(e -> "Upcoming".equals(e.getStatus())).count();
        long ongoing = allExaminations.stream().filter(e -> "Ongoing".equals(e.getStatus())).count();
        long completed = allExaminations.stream().filter(e -> "Completed".equals(e.getStatus())).count();
        long cancelled = allExaminations.stream().filter(e -> "Cancelled".equals(e.getStatus())).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("upcoming", upcoming);
        stats.put("ongoing", ongoing);
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);
        stats.put("completionRate", total > 0 ? (double) completed / total * 100 : 0);
        
        return ResponseEntity.ok(stats);
    }

    // Enhanced endpoints for exam management
    @GetMapping("/upcoming")
    public ResponseEntity<List<Examination>> getUpcomingExaminations() {
        List<Examination> examinations = examinationService.getUpcomingExaminations();
        return ResponseEntity.ok(examinations);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<Examination>> getOngoingExaminations() {
        List<Examination> examinations = examinationService.getOngoingExaminations();
        return ResponseEntity.ok(examinations);
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<Map<String, Object>> getExaminationStatistics(@PathVariable Long id) {
        Map<String, Object> statistics = examinationService.getExaminationStatistics(id);
        if (statistics != null) {
            return ResponseEntity.ok(statistics);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/faculty/{facultyId}/statistics")
    public ResponseEntity<Map<String, Object>> getFacultyExaminationStatistics(@PathVariable Long facultyId) {
        Map<String, Object> statistics = examinationService.getFacultyExaminationStatistics(facultyId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<Map<String, Object>>> getExaminationCalendar(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(3);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now().plusMonths(3);
        
        List<Map<String, Object>> calendar = examinationService.getExaminationCalendar(start, end);
        return ResponseEntity.ok(calendar);
    }

    @PostMapping("/update-statuses")
    public ResponseEntity<Void> updateExaminationStatuses() {
        examinationService.updateExaminationStatuses();
        return ResponseEntity.ok().build();
    }

    // Registration-related endpoints
    @GetMapping("/{id}/available")
    public ResponseEntity<Map<String, Object>> checkExaminationAvailability(@PathVariable Long id) {
        Examination examination = examinationService.getExaminationById(id);
        if (examination == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> availability = new HashMap<>();
        availability.put("canRegister", examination.canRegister());
        availability.put("isFull", examination.isFull());
        availability.put("isRegistrationOpen", examination.isRegistrationOpen());
        availability.put("currentParticipants", examination.getCurrentParticipants());
        availability.put("maxParticipants", examination.getMaxParticipants());
        availability.put("registrationDeadline", examination.getRegistrationDeadline());
        
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/{id}/registrations")
    public ResponseEntity<Map<String, Object>> getExaminationRegistrations(@PathVariable Long id) {
        Map<String, Object> statistics = examRegistrationService.getExaminationStatistics(id);
        if (statistics != null) {
            return ResponseEntity.ok(statistics);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/attended")
    public ResponseEntity<List<Object>> getStudentsWhoAttended(@PathVariable Long id) {
        List<Object> students = (List<Object>) (Object) examRegistrationService.getStudentsWhoAttended(id);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}/absent")
    public ResponseEntity<List<Object>> getStudentsWhoWereAbsent(@PathVariable Long id) {
        List<Object> students = (List<Object>) (Object) examRegistrationService.getStudentsWhoWereAbsent(id);
        return ResponseEntity.ok(students);
    }
}

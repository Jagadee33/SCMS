package com.college.controller;

import com.college.service.SmartAttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/smart-attendance")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class SmartAttendanceController {
    
    private final SmartAttendanceService smartAttendanceService;
    
    // Mark attendance with facial recognition
    @PostMapping("/mark-facial")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<?> markAttendanceWithFacialRecognition(
            @RequestParam Long studentId,
            @RequestParam String courseId,
            @RequestBody byte[] facialData) {
        try {
            var attendance = smartAttendanceService.markAttendanceWithFacialRecognition(studentId, courseId, facialData);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Attendance marked successfully",
                "data", attendance
            ));
        } catch (Exception e) {
            log.error("Error marking attendance with facial recognition", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get attendance predictions for a student
    @GetMapping("/predictions/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<?> getAttendancePredictions(@PathVariable Long studentId) {
        try {
            var predictions = smartAttendanceService.predictAttendancePatterns(studentId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", predictions
            ));
        } catch (Exception e) {
            log.error("Error getting attendance predictions", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get all at-risk students
    @GetMapping("/at-risk-students")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<?> getAtRiskStudents() {
        try {
            var atRiskStudents = smartAttendanceService.identifyAtRiskStudents();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", atRiskStudents,
                "count", atRiskStudents.size()
            ));
        } catch (Exception e) {
            log.error("Error identifying at-risk students", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get smart notifications for a student
    @GetMapping("/notifications/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<?> getSmartNotifications(@PathVariable Long studentId) {
        try {
            var notifications = smartAttendanceService.generateSmartNotifications(studentId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", notifications
            ));
        } catch (Exception e) {
            log.error("Error generating smart notifications", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get attendance analytics for all students
    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<?> getAttendanceAnalytics() {
        try {
            // This would aggregate data from all students
            var atRiskStudents = smartAttendanceService.identifyAtRiskStudents();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                    "totalAtRiskStudents", atRiskStudents.size(),
                    "atRiskStudents", atRiskStudents,
                    "highRiskCount", atRiskStudents.stream()
                        .mapToInt(s -> "HIGH".equals(s.get("riskLevel")) ? 1 : 0)
                        .sum(),
                    "criticalRiskCount", atRiskStudents.stream()
                        .mapToInt(s -> "CRITICAL".equals(s.get("riskLevel")) ? 1 : 0)
                        .sum(),
                    "generatedAt", java.time.LocalDateTime.now()
                )
            ));
        } catch (Exception e) {
            log.error("Error getting attendance analytics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get facial recognition status (mock endpoint for demo)
    @GetMapping("/facial-recognition-status")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<?> getFacialRecognitionStatus() {
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", Map.of(
                "facialRecognitionEnabled", true,
                "confidenceThreshold", 0.85,
                "supportedFormats", List.of("JPEG", "PNG", "WEBP"),
                "maxFileSize", "5MB",
                "status", "ACTIVE"
            )
        ));
    }
}

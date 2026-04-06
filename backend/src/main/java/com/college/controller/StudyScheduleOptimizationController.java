package com.college.controller;

import com.college.service.StudyScheduleOptimizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/study-optimization")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class StudyScheduleOptimizationController {
    
    private final StudyScheduleOptimizationService studyScheduleOptimizationService;
    
    // Generate optimized study schedule for a student
    @GetMapping("/schedule/{studentId}")
    public ResponseEntity<?> getOptimizedStudySchedule(@PathVariable Long studentId) {
        try {
            var optimizedSchedule = studyScheduleOptimizationService.generateOptimizedStudySchedule(studentId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", optimizedSchedule
            ));
        } catch (Exception e) {
            log.error("Error generating optimized study schedule", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get study optimization recommendations for a student
    @GetMapping("/recommendations/{studentId}")
    public ResponseEntity<?> getStudyRecommendations(@PathVariable Long studentId) {
        try {
            var schedule = studyScheduleOptimizationService.generateOptimizedStudySchedule(studentId);
            @SuppressWarnings("unchecked")
            var recommendations = (Map<String, Object>) schedule.get("recommendations");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", recommendations
            ));
        } catch (Exception e) {
            log.error("Error getting study recommendations", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get optimization metrics for a student
    @GetMapping("/metrics/{studentId}")
    public ResponseEntity<?> getOptimizationMetrics(@PathVariable Long studentId) {
        try {
            var schedule = studyScheduleOptimizationService.generateOptimizedStudySchedule(studentId);
            @SuppressWarnings("unchecked")
            var metrics = (Map<String, Object>) schedule.get("optimizationMetrics");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", metrics
            ));
        } catch (Exception e) {
            log.error("Error getting optimization metrics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get student performance profile
    @GetMapping("/profile/{studentId}")
    public ResponseEntity<?> getStudentPerformanceProfile(@PathVariable Long studentId) {
        try {
            var schedule = studyScheduleOptimizationService.generateOptimizedStudySchedule(studentId);
            @SuppressWarnings("unchecked")
            var profile = (Map<String, Object>) schedule.get("performanceProfile");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", profile
            ));
        } catch (Exception e) {
            log.error("Error getting student performance profile", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Batch optimization for multiple students
    @PostMapping("/batch-optimize")
    public ResponseEntity<?> batchOptimizeSchedules(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            var studentIds = (java.util.List<Long>) request.get("studentIds");
            
            if (studentIds == null || studentIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Student IDs are required"
                ));
            }
            
            java.util.Map<Long, Object> results = new java.util.HashMap<>();
            for (Long studentId : studentIds) {
                try {
                    var optimizedSchedule = studyScheduleOptimizationService.generateOptimizedStudySchedule(studentId);
                    results.put(studentId, optimizedSchedule);
                } catch (Exception e) {
                    results.put(studentId, Map.of(
                        "error", "Optimization failed: " + e.getMessage()
                    ));
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", results,
                "processed", studentIds.size()
            ));
        } catch (Exception e) {
            log.error("Error in batch optimization", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}

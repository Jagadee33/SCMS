package com.college.controller;

import com.college.service.PerformancePredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance-prediction")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PerformancePredictionController {
    
    private final PerformancePredictionService performancePredictionService;
    
    // Get performance prediction for a student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentPerformancePrediction(@PathVariable Long studentId) {
        try {
            var prediction = performancePredictionService.predictStudentPerformance(studentId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", prediction
            ));
        } catch (Exception e) {
            log.error("Error getting student performance prediction", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get all at-risk students
    @GetMapping("/at-risk-students")
    public ResponseEntity<?> getAtRiskStudents() {
        try {
            var atRiskStudents = performancePredictionService.identifyAtRiskStudents();
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
    
    // Get class performance analytics
    @GetMapping("/class-analytics/{courseId}")
    public ResponseEntity<?> getClassPerformanceAnalytics(@PathVariable String courseId) {
        try {
            var analytics = performancePredictionService.getClassPerformanceAnalytics(courseId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", analytics
            ));
        } catch (Exception e) {
            log.error("Error getting class performance analytics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get performance summary for dashboard
    @GetMapping("/dashboard-summary")
    public ResponseEntity<?> getPerformanceDashboardSummary() {
        try {
            var atRiskStudents = performancePredictionService.identifyAtRiskStudents();
            
            // Calculate summary statistics
            long totalAtRisk = atRiskStudents.size();
            long criticalRisk = atRiskStudents.stream()
                .mapToInt(s -> "CRITICAL".equals(s.get("riskLevel")) ? 1 : 0)
                .sum();
            long highRisk = atRiskStudents.stream()
                .mapToInt(s -> "HIGH".equals(s.get("riskLevel")) ? 1 : 0)
                .sum();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                    "totalAtRiskStudents", totalAtRisk,
                    "criticalRiskCount", criticalRisk,
                    "highRiskCount", highRisk,
                    "mediumRiskCount", totalAtRisk - criticalRisk - highRisk,
                    "atRiskStudents", atRiskStudents.subList(0, Math.min(10, atRiskStudents.size())),
                    "generatedAt", java.time.LocalDateTime.now()
                )
            ));
        } catch (Exception e) {
            log.error("Error getting performance dashboard summary", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get AI insights for a student
    @GetMapping("/insights/{studentId}")
    public ResponseEntity<?> getStudentInsights(@PathVariable Long studentId) {
        try {
            var prediction = performancePredictionService.predictStudentPerformance(studentId);
            
            if (prediction.containsKey("error")) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                        "message", "Insufficient data for AI insights",
                        "recommendation", "Complete more assignments and maintain good attendance for better insights"
                    )
                ));
            }
            
            // Generate additional insights
            Map<String, Object> insights = generateAdditionalInsights(prediction);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", insights
            ));
        } catch (Exception e) {
            log.error("Error getting student insights", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Helper method to generate additional insights
    private Map<String, Object> generateAdditionalInsights(Map<String, Object> prediction) {
        double currentGPA = (Double) prediction.get("currentGPA");
        double attendanceRate = (Double) prediction.get("attendanceRate");
        String riskLevel = (String) prediction.get("riskLevel");
        
        Map<String, Object> insights = new HashMap<>();
        insights.putAll(prediction);
        
        // Generate personalized recommendations
        if (currentGPA >= 3.5) {
            insights.put("achievement", "Excellent Performance");
            insights.put("nextStep", "Consider advanced coursework or research opportunities");
        } else if (currentGPA >= 2.5) {
            insights.put("achievement", "Good Performance");
            insights.put("nextStep", "Focus on maintaining consistency and explore leadership roles");
        } else if (currentGPA >= 2.0) {
            insights.put("achievement", "Satisfactory Performance");
            insights.put("nextStep", "Seek academic support and improve study habits");
        } else {
            insights.put("achievement", "Needs Improvement");
            insights.put("nextStep", "Immediate academic intervention required");
        }
        
        // Attendance insights
        if (attendanceRate >= 95) {
            insights.put("attendanceInsight", "Perfect attendance - excellent commitment!");
        } else if (attendanceRate >= 85) {
            insights.put("attendanceInsight", "Good attendance - keep it up!");
        } else if (attendanceRate >= 75) {
            insights.put("attendanceInsight", "Satisfactory attendance - room for improvement");
        } else {
            insights.put("attendanceInsight", "Poor attendance - needs immediate attention");
        }
        
        // Success prediction
        Map<String, Object> predictions = (Map<String, Object>) prediction.get("predictions");
        if (predictions != null) {
            double graduationProb = (Double) predictions.get("graduationProbability");
            if (graduationProb >= 0.9) {
                insights.put("successPrediction", "On track for successful graduation");
            } else if (graduationProb >= 0.7) {
                insights.put("successPrediction", "Good progress towards graduation");
            } else {
                insights.put("successPrediction", "At risk - requires immediate action");
            }
        }
        
        return insights;
    }
}

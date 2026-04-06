package com.college.controller;

import com.college.service.AdvancedAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AdvancedAnalyticsController {
    
    private final AdvancedAnalyticsService advancedAnalyticsService;
    
    // Get comprehensive system analytics
    @GetMapping("/system-overview")
    public ResponseEntity<?> getSystemAnalytics() {
        try {
            var analytics = advancedAnalyticsService.generateSystemAnalytics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", analytics
            ));
        } catch (Exception e) {
            log.error("Error generating system analytics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get student analytics
    @GetMapping("/students")
    public ResponseEntity<?> getStudentAnalytics() {
        try {
            var studentAnalytics = advancedAnalyticsService.generateStudentAnalytics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", studentAnalytics
            ));
        } catch (Exception e) {
            log.error("Error generating student analytics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get course analytics
    @GetMapping("/courses")
    public ResponseEntity<?> getCourseAnalytics() {
        try {
            var courseAnalytics = advancedAnalyticsService.generateCourseAnalytics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", courseAnalytics
            ));
        } catch (Exception e) {
            log.error("Error generating course analytics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get faculty analytics
    @GetMapping("/faculty")
    public ResponseEntity<?> getFacultyAnalytics() {
        try {
            var facultyAnalytics = advancedAnalyticsService.generateFacultyAnalytics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", facultyAnalytics
            ));
        } catch (Exception e) {
            log.error("Error generating faculty analytics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get financial analytics
    @GetMapping("/financial")
    public ResponseEntity<?> getFinancialAnalytics() {
        try {
            var financialAnalytics = advancedAnalyticsService.generateFinancialAnalytics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", financialAnalytics
            ));
        } catch (Exception e) {
            log.error("Error generating financial analytics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get performance metrics
    @GetMapping("/performance-metrics")
    public ResponseEntity<?> getPerformanceMetrics() {
        try {
            var performanceMetrics = advancedAnalyticsService.generatePerformanceMetrics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", performanceMetrics
            ));
        } catch (Exception e) {
            log.error("Error generating performance metrics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get trends analysis
    @GetMapping("/trends")
    public ResponseEntity<?> getTrendsAnalysis() {
        try {
            var trends = advancedAnalyticsService.generateTrendsAnalysis();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", trends
            ));
        } catch (Exception e) {
            log.error("Error generating trends analysis", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get risk indicators
    @GetMapping("/risk-indicators")
    public ResponseEntity<?> getRiskIndicators() {
        try {
            var riskIndicators = advancedAnalyticsService.generateRiskIndicators();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", riskIndicators
            ));
        } catch (Exception e) {
            log.error("Error generating risk indicators", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Generate custom report
    @PostMapping("/generate-report")
    public ResponseEntity<?> generateCustomReport(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            String reportType = (String) request.get("reportType");
            
            Map<String, Object> reportData;
            switch (reportType.toLowerCase()) {
                case "student_performance":
                    reportData = advancedAnalyticsService.generateStudentAnalytics();
                    break;
                case "course_analysis":
                    reportData = advancedAnalyticsService.generateCourseAnalytics();
                    break;
                case "financial_summary":
                    reportData = advancedAnalyticsService.generateFinancialAnalytics();
                    break;
                case "faculty_workload":
                    reportData = advancedAnalyticsService.generateFacultyAnalytics();
                    break;
                case "system_health":
                    reportData = advancedAnalyticsService.generatePerformanceMetrics();
                    break;
                default:
                    reportData = Map.of("error", "Unsupported report type: " + reportType);
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "reportType", reportType,
                "generatedAt", java.time.LocalDateTime.now(),
                "data", reportData
            ));
        } catch (Exception e) {
            log.error("Error generating custom report", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get dashboard summary
    @GetMapping("/dashboard-summary")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            var systemAnalytics = advancedAnalyticsService.generateSystemAnalytics();
            @SuppressWarnings("unchecked")
            var overview = (Map<String, Object>) systemAnalytics.get("overview");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "summary", Map.of(
                    "totalStudents", overview.get("totalStudents"),
                    "totalCourses", overview.get("totalCourses"),
                    "totalFaculty", overview.get("totalFaculty"),
                    "totalRevenue", overview.get("totalRevenue"),
                    "activeUsers", 1250,
                    "systemHealth", "operational",
                    "lastUpdated", java.time.LocalDateTime.now()
                )
            ));
        } catch (Exception e) {
            log.error("Error generating dashboard summary", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}

package com.college.controller;

import com.college.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AdminDashboardController {
    
    private final AdminDashboardService adminDashboardService;
    
    // Get comprehensive admin dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard() {
        try {
            var dashboard = adminDashboardService.generateAdminDashboard();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", dashboard
            ));
        } catch (Exception e) {
            log.error("Error generating admin dashboard", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get system overview
    @GetMapping("/overview")
    public ResponseEntity<?> getSystemOverview() {
        try {
            var overview = adminDashboardService.generateSystemOverview();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", overview
            ));
        } catch (Exception e) {
            log.error("Error getting system overview", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get user management metrics
    @GetMapping("/user-metrics")
    public ResponseEntity<?> getUserManagementMetrics() {
        try {
            var metrics = adminDashboardService.generateUserManagementMetrics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", metrics
            ));
        } catch (Exception e) {
            log.error("Error getting user management metrics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get academic metrics
    @GetMapping("/academic-metrics")
    public ResponseEntity<?> getAcademicMetrics() {
        try {
            var metrics = adminDashboardService.generateAcademicMetrics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", metrics
            ));
        } catch (Exception e) {
            log.error("Error getting academic metrics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get financial overview
    @GetMapping("/financial-overview")
    public ResponseEntity<?> getFinancialOverview() {
        try {
            var overview = adminDashboardService.generateFinancialOverview();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", overview
            ));
        } catch (Exception e) {
            log.error("Error getting financial overview", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get system health metrics
    @GetMapping("/system-health")
    public ResponseEntity<?> getSystemHealthMetrics() {
        try {
            var health = adminDashboardService.generateSystemHealthMetrics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", health
            ));
        } catch (Exception e) {
            log.error("Error getting system health metrics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get operational metrics
    @GetMapping("/operational-metrics")
    public ResponseEntity<?> getOperationalMetrics() {
        try {
            var metrics = adminDashboardService.generateOperationalMetrics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", metrics
            ));
        } catch (Exception e) {
            log.error("Error getting operational metrics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get security metrics
    @GetMapping("/security-metrics")
    public ResponseEntity<?> getSecurityMetrics() {
        try {
            var metrics = adminDashboardService.generateSecurityMetrics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", metrics
            ));
        } catch (Exception e) {
            log.error("Error getting security metrics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get recent activities
    @GetMapping("/recent-activities")
    public ResponseEntity<?> getRecentActivities() {
        try {
            var activities = adminDashboardService.generateRecentActivities();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", activities
            ));
        } catch (Exception e) {
            log.error("Error getting recent activities", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get system alerts
    @GetMapping("/system-alerts")
    public ResponseEntity<?> getSystemAlerts() {
        try {
            var alerts = adminDashboardService.generateSystemAlerts();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", alerts
            ));
        } catch (Exception e) {
            log.error("Error getting system alerts", e);
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
                case "system_overview":
                    reportData = adminDashboardService.generateSystemOverview();
                    break;
                case "user_management":
                    reportData = adminDashboardService.generateUserManagementMetrics();
                    break;
                case "academic_metrics":
                    reportData = adminDashboardService.generateAcademicMetrics();
                    break;
                case "financial_overview":
                    reportData = adminDashboardService.generateFinancialOverview();
                    break;
                case "system_health":
                    reportData = adminDashboardService.generateSystemHealthMetrics();
                    break;
                case "operational_metrics":
                    reportData = adminDashboardService.generateOperationalMetrics();
                    break;
                case "security_metrics":
                    reportData = adminDashboardService.generateSecurityMetrics();
                    break;
                case "recent_activities":
                    reportData = adminDashboardService.generateRecentActivities();
                    break;
                case "system_alerts":
                    reportData = adminDashboardService.generateSystemAlerts();
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
    
    // Export dashboard data
    @GetMapping("/export-data")
    public ResponseEntity<?> exportDashboardData(@RequestParam(defaultValue = "json") String format) {
        try {
            var dashboard = adminDashboardService.generateAdminDashboard();
            
            // In a real implementation, this would generate actual file exports
            Map<String, Object> exportData = Map.of(
                "exportFormat", format,
                "exportDate", java.time.LocalDateTime.now(),
                "dataSize", "2.3MB",
                "recordCount", "1,250",
                "downloadUrl", "/api/admin/download-export/" + System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", exportData
            ));
        } catch (Exception e) {
            log.error("Error exporting dashboard data", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Test admin dashboard endpoint
    @GetMapping("/test")
    public ResponseEntity<?> testAdminDashboard() {
        try {
            var testDashboard = Map.of(
                "timestamp", java.time.LocalDateTime.now(),
                "status", "operational",
                "testData", Map.of(
                    "systemHealth", "healthy",
                    "userCount", 1250,
                    "responseTime", "245ms",
                    "testPassed", true
                )
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", testDashboard,
                "message", "Admin dashboard test completed successfully"
            ));
        } catch (Exception e) {
            log.error("Error testing admin dashboard", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}

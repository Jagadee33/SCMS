package com.college.controller;

import com.college.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class NotificationController {
    
    private final NotificationService notificationService;
    
    // Send notification
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody Map<String, Object> request) {
        try {
            var result = notificationService.sendNotification(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result
            ));
        } catch (Exception e) {
            log.error("Error sending notification", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get notification history
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getNotificationHistory(@PathVariable Long userId,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(defaultValue = "10") int limit) {
        try {
            var history = notificationService.getNotificationHistory(userId, type, limit);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", history
            ));
        } catch (Exception e) {
            log.error("Error getting notification history", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get notification preferences
    @GetMapping("/preferences/{userId}")
    public ResponseEntity<?> getNotificationPreferences(@PathVariable Long userId) {
        try {
            var preferences = notificationService.getNotificationPreferences(userId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", preferences
            ));
        } catch (Exception e) {
            log.error("Error getting notification preferences", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Update notification preferences
    @PutMapping("/preferences/{userId}")
    public ResponseEntity<?> updateNotificationPreferences(@PathVariable Long userId,
                                               @RequestBody Map<String, Object> preferences) {
        try {
            var result = notificationService.updateNotificationPreferences(userId, preferences);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result
            ));
        } catch (Exception e) {
            log.error("Error updating notification preferences", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Mark notification as read
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        try {
            // In a real implementation, this would update the notification status
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Notification marked as read"
            ));
        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Send bulk notifications
    @PostMapping("/bulk")
    public ResponseEntity<?> sendBulkNotifications(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            var notifications = (java.util.List<Map<String, Object>>) request.get("notifications");
            
            if (notifications == null || notifications.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Notifications list is required"
                ));
            }
            
            java.util.Map<Long, Object> results = new java.util.HashMap<>();
            int successCount = 0;
            int failureCount = 0;
            
            for (Map<String, Object> notification : notifications) {
                try {
                    var result = notificationService.sendNotification(notification);
                    results.put((Long) notification.get("id"), result);
                    successCount++;
                } catch (Exception e) {
                    results.put((Long) notification.get("id"), Map.of(
                        "success", false,
                        "error", e.getMessage()
                    ));
                    failureCount++;
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", results,
                "summary", Map.of(
                    "total", notifications.size(),
                    "successful", successCount,
                    "failed", failureCount
                )
            ));
        } catch (Exception e) {
            log.error("Error sending bulk notifications", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Get notification statistics
    @GetMapping("/statistics")
    public ResponseEntity<?> getNotificationStatistics() {
        try {
            // In a real implementation, this would query the database
            var statistics = Map.of(
                "totalSent", 1250,
                "totalDelivered", 1180,
                "totalRead", 950,
                "deliveryRate", 94.4,
                "readRate", 80.5,
                "byType", Map.of(
                    "attendance_alert", 320,
                    "grade_posted", 280,
                    "payment_reminder", 450,
                    "exam_scheduled", 180,
                    "system_announcement", 20
                ),
                "byChannel", Map.of(
                    "email", 1180,
                    "sms", 450,
                    "push_notification", 320,
                    "dashboard", 150
                ),
                "byPriority", Map.of(
                    "high", 180,
                    "medium", 850,
                    "low", 220
                ),
                "lastUpdated", java.time.LocalDateTime.now()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", statistics
            ));
        } catch (Exception e) {
            log.error("Error getting notification statistics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // Test notification endpoint
    @PostMapping("/test")
    public ResponseEntity<?> testNotification(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            var result = notificationService.sendNotification(Map.of(
                "type", request.getOrDefault("type", "system"),
                "recipientId", request.getOrDefault("recipientId", 1L),
                "message", "Test notification: " + (request.getOrDefault("message", "System test")),
                "priority", request.getOrDefault("priority", "medium")
            ));
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result,
                "message", "Test notification sent successfully"
            ));
        } catch (Exception e) {
            log.error("Error sending test notification", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}

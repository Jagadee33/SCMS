package com.college.service;

import com.college.model.*;
import com.college.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class NotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final ExaminationRepository examinationRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    public NotificationService(StudentRepository studentRepository, GradeRepository gradeRepository, 
                           AttendanceRepository attendanceRepository,
                           PaymentRepository paymentRepository, ExaminationRepository examinationRepository,
                           EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
        this.examinationRepository = examinationRepository;
        this.enrollmentRepository = enrollmentRepository;
    }
    
    // Send real-time notifications
    public Map<String, Object> sendNotification(Map<String, Object> notificationRequest) {
        try {
            String type = (String) notificationRequest.get("type");
            Long recipientId = (Long) notificationRequest.get("recipientId");
            String message = (String) notificationRequest.get("message");
            String priority = (String) notificationRequest.getOrDefault("priority", "medium");
            
            // Validate notification request
            Map<String, Object> validationResult = validateNotificationRequest(notificationRequest);
            if (!(Boolean) validationResult.get("valid")) {
                return Map.of(
                    "success", false,
                    "error", validationResult.get("error")
                );
            }
            
            // Create notification record
            Notification notification = createNotification(type, recipientId, message, priority);
            
            // Process notification based on type
            Map<String, Object> result = processNotificationByType(notification, notificationRequest);
            
            return Map.of(
                "success", true,
                "notificationId", notification.getId(),
                "message", result.get("message"),
                "deliveredAt", LocalDateTime.now(),
                "channels", result.get("channels")
            );
        } catch (Exception e) {
            log.error("Error sending notification", e);
            return Map.of("error", "Notification failed: " + e.getMessage());
        }
    }
    
    private Map<String, Object> validateNotificationRequest(Map<String, Object> request) {
        String type = (String) request.get("type");
        Long recipientId = (Long) request.get("recipientId");
        String message = (String) request.get("message");
        
        // Basic validation
        if (type == null || type.trim().isEmpty()) {
            return Map.of("valid", false, "error", "Notification type is required");
        }
        
        if (recipientId == null) {
            return Map.of("valid", false, "error", "Recipient ID is required");
        }
        
        if (message == null || message.trim().isEmpty()) {
            return Map.of("valid", false, "error", "Message content is required");
        }
        
        // Validate recipient exists
        if (type.equals("student") && !studentRepository.existsById(recipientId)) {
            return Map.of("valid", false, "error", "Student not found");
        }
        
        return Map.of("valid", true);
    }
    
    private Notification createNotification(String type, Long recipientId, String message, String priority) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setRecipientId(recipientId);
        notification.setRecipientType(determineRecipientType(type));
        notification.setMessage(message);
        notification.setPriority(priority);
        notification.setStatus("PENDING");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setExpiresAt(calculateExpirationTime(priority));
        
        // In a real implementation, this would be saved to database
        log.info("Created notification: {}", notification);
        return notification;
    }
    
    private String determineRecipientType(String notificationType) {
        switch (notificationType.toLowerCase()) {
            case "student":
            case "parent":
            case "faculty":
            case "admin":
                return notificationType.toUpperCase();
            default:
                return "USER";
        }
    }
    
    private LocalDateTime calculateExpirationTime(String priority) {
        LocalDateTime now = LocalDateTime.now();
        switch (priority.toLowerCase()) {
            case "high":
                return now.plusDays(7);
            case "medium":
                return now.plusDays(14);
            case "low":
                return now.plusDays(30);
            default:
                return now.plusDays(14);
        }
    }
    
    private Map<String, Object> processNotificationByType(Notification notification, Map<String, Object> request) {
        String type = notification.getType();
        Long recipientId = notification.getRecipientId();
        
        switch (type.toLowerCase()) {
            case "attendance_alert":
                return processAttendanceNotification(notification, recipientId);
            case "grade_posted":
                return processGradeNotification(notification, recipientId);
            case "payment_reminder":
                return processPaymentNotification(notification, recipientId);
            case "exam_scheduled":
                return processExamNotification(notification, recipientId);
            case "course_announcement":
                return processCourseNotification(notification, recipientId);
            case "system_maintenance":
                return processSystemNotification(notification, recipientId);
            case "academic_warning":
                return processAcademicWarningNotification(notification, recipientId);
            default:
                return processGenericNotification(notification, recipientId);
        }
    }
    
    private Map<String, Object> processAttendanceNotification(Notification notification, Long studentId) {
        try {
            // Get attendance data
            double attendanceRate = calculateStudentAttendanceRate(studentId);
            
            // Determine alert level
            String alertLevel = attendanceRate < 70.0 ? "critical" : 
                                   attendanceRate < 80.0 ? "warning" : "info";
            
            // Send through multiple channels
            List<String> channels = new ArrayList<>();
            channels.add("email");
            channels.add("sms");
            if (alertLevel.equals("critical")) {
                channels.add("push_notification");
                channels.add("dashboard_alert");
            }
            
            // Create follow-up actions
            List<String> actions = new ArrayList<>();
            actions.add("View attendance dashboard");
            actions.add("Contact academic advisor");
            if (attendanceRate < 75.0) {
                actions.add("Schedule attendance improvement meeting");
            }
            
            return Map.of(
                "message", String.format("Attendance Alert: Your current attendance is %.1f%%", attendanceRate),
                "alertLevel", alertLevel,
                "channels", channels,
                "actions", actions,
                "requiresImmediateAction", alertLevel.equals("critical")
            );
        } catch (Exception e) {
            log.error("Error processing attendance notification", e);
            return Map.of("error", "Failed to process attendance notification");
        }
    }
    
    private Map<String, Object> processGradeNotification(Notification notification, Long studentId) {
        try {
            // Get recent grades
            List<Grade> recentGrades = gradeRepository.findByStudentId(studentId)
                    .stream()
                    .sorted((g1, g2) -> g2.getCreatedAt().compareTo(g1.getCreatedAt()))
                    .limit(5)
                    .collect(Collectors.toList());
            
            double averageGrade = recentGrades.stream()
                    .mapToDouble(g -> g.getGradePoints() != null ? g.getGradePoints() : 2.0)
                    .average()
                    .orElse(2.0);
            
            String performance = averageGrade >= 3.5 ? "excellent" :
                                 averageGrade >= 3.0 ? "good" :
                                 averageGrade >= 2.5 ? "average" :
                                 averageGrade >= 2.0 ? "below_average" : "poor";
            
            List<String> channels = new ArrayList<>();
            channels.add("email");
            channels.add("dashboard");
            
            return Map.of(
                "message", String.format("Grade Posted: %s (%.2f) - Performance: %s", 
                    recentGrades.get(0).getCourse() != null ? recentGrades.get(0).getCourse().getName() : "Course",
                    averageGrade, performance),
                "channels", channels,
                "recentGrades", recentGrades.stream()
                    .map(g -> Map.of(
                        "course", g.getCourse() != null ? g.getCourse().getName() : "Course",
                        "grade", g.getGradePoints(),
                        "gradeLetter", g.getGradeLetter()
                    ))
                    .collect(Collectors.toList())
            );
        } catch (Exception e) {
            log.error("Error processing grade notification", e);
            return Map.of("error", "Failed to process grade notification");
        }
    }
    
    private Map<String, Object> processPaymentNotification(Notification notification, Long recipientId) {
        try {
            // Get payment details
            List<Payment> recentPayments = paymentRepository.findByRecipientId(recipientId)
                    .stream()
                    .sorted((p1, p2) -> p2.getPaymentDate().compareTo(p1.getPaymentDate()))
                    .limit(3)
                    .collect(Collectors.toList());
            
            double totalDue = recentPayments.stream()
                    .filter(p -> p.getFee() != null && p.getFee().getDueDate() != null && p.getFee().getDueDate().isAfter(LocalDateTime.now()))
                    .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                    .sum();
            
            List<String> channels = new ArrayList<>();
            channels.add("email");
            channels.add("sms");
            if (totalDue > 0) {
                channels.add("push_notification");
            }
            
            return Map.of(
                "message", String.format("Payment Reminder: $%.2f due soon", totalDue),
                "channels", channels,
                "totalDue", totalDue,
                "upcomingPayments", recentPayments.stream()
                    .filter(p -> p.getFee() != null && p.getFee().getDueDate() != null && 
                               p.getFee().getDueDate().isAfter(LocalDateTime.now()) && 
                               p.getFee().getDueDate().isBefore(LocalDateTime.now().plusDays(7)))
                    .map(p -> Map.of(
                        "amount", p.getAmount(),
                        "dueDate", p.getFee().getDueDate(),
                        "daysUntilDue", java.time.Duration.between(LocalDateTime.now(), p.getFee().getDueDate()).toDays()
                    ))
                    .collect(Collectors.toList())
            );
        } catch (Exception e) {
            log.error("Error processing payment notification", e);
            return Map.of("error", "Failed to process payment notification");
        }
    }
    
    private Map<String, Object> processExamNotification(Notification notification, Long recipientId) {
        try {
            // Get upcoming exams
            List<Examination> upcomingExams = examinationRepository.findByStudentId(recipientId)
                    .stream()
                    .filter(exam -> exam.getExamDate() != null && exam.getExamDate().isAfter(LocalDateTime.now()))
                    .sorted((e1, e2) -> e1.getExamDate().compareTo(e2.getExamDate()))
                    .limit(3)
                    .collect(Collectors.toList());
            
            List<String> channels = new ArrayList<>();
            channels.add("email");
            channels.add("calendar");
            if (!upcomingExams.isEmpty()) {
                channels.add("sms");
            }
            
            return Map.of(
                "message", String.format("You have %d upcoming exams", upcomingExams.size()),
                "channels", channels,
                "upcomingExams", upcomingExams.stream()
                    .map(exam -> Map.of(
                        "course", exam.getCourse() != null ? exam.getCourse().getName() : "Exam",
                        "examDate", exam.getExamDate(),
                        "examType", exam.getExamType(),
                        "location", exam.getVenue(),
                        "daysUntilExam", java.time.Duration.between(LocalDateTime.now(), exam.getExamDate()).toDays()
                    ))
                    .collect(Collectors.toList())
            );
        } catch (Exception e) {
            log.error("Error processing exam notification", e);
            return Map.of("error", "Failed to process exam notification");
        }
    }
    
    private Map<String, Object> processCourseNotification(Notification notification, Long recipientId) {
        try {
            // Get course details
            Student student = studentRepository.findById(recipientId).orElse(null);
            if (student == null) return Map.of("error", "Student not found");
            List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
            
            List<String> channels = new ArrayList<>();
            channels.add("email");
            channels.add("dashboard");
            
            return Map.of(
                "message", "Course announcement: Check your course updates",
                "channels", channels,
                "enrolledCourses", enrollments.stream()
                    .map(e -> Map.of(
                        "courseId", e.getCourse().getId(),
                        "courseName", e.getCourse().getName(),
                        "enrollmentDate", e.getEnrollmentDate()
                    ))
                    .collect(Collectors.toList())
            );
        } catch (Exception e) {
            log.error("Error processing course notification", e);
            return Map.of("error", "Failed to process course notification");
        }
    }
    
    private Map<String, Object> processSystemNotification(Notification notification, Long recipientId) {
        try {
            String message = notification.getMessage();
            
            List<String> channels = new ArrayList<>();
            channels.add("email");
            channels.add("dashboard");
            channels.add("banner");
            
            // System notifications often require immediate attention
            boolean requiresImmediateAction = message.toLowerCase().contains("urgent") || 
                                             message.toLowerCase().contains("maintenance") ||
                                             message.toLowerCase().contains("security");
            
            if (requiresImmediateAction) {
                channels.add("sms");
                channels.add("push_notification");
            }
            
            return Map.of(
                "message", message,
                "channels", channels,
                "requiresImmediateAction", requiresImmediateAction,
                "priority", requiresImmediateAction ? "high" : "medium"
            );
        } catch (Exception e) {
            log.error("Error processing system notification", e);
            return Map.of("error", "Failed to process system notification");
        }
    }
    
    private Map<String, Object> processAcademicWarningNotification(Notification notification, Long studentId) {
        try {
            // Get student performance data
            double gpa = calculateStudentGPA(studentId);
            double attendanceRate = calculateStudentAttendanceRate(studentId);
            
            // Determine risk factors
            List<String> riskFactors = new ArrayList<>();
            if (gpa < 2.0) riskFactors.add("Low GPA");
            if (attendanceRate < 75.0) riskFactors.add("Poor Attendance");
            if (gpa < 2.5 && attendanceRate < 80.0) riskFactors.add("Academic Probation Risk");
            
            List<String> recommendations = new ArrayList<>();
            recommendations.add("Schedule meeting with academic advisor");
            recommendations.add("Consider tutoring services");
            recommendations.add("Review study habits");
            recommendations.add("Utilize campus resources");
            
            List<String> channels = new ArrayList<>();
            channels.add("email");
            channels.add("dashboard");
            channels.add("sms");
            
            return Map.of(
                "message", String.format("Academic Warning: GPA=%.2f, Attendance=%.1f%%", gpa, attendanceRate),
                "riskLevel", gpa < 1.5 ? "critical" : gpa < 2.0 ? "high" : "medium",
                "riskFactors", riskFactors,
                "recommendations", recommendations,
                "channels", channels,
                "requiresImmediateAction", gpa < 2.0
            );
        } catch (Exception e) {
            log.error("Error processing academic warning notification", e);
            return Map.of("error", "Failed to process academic warning");
        }
    }
    
    private Map<String, Object> processGenericNotification(Notification notification, Long recipientId) {
        try {
            List<String> channels = new ArrayList<>();
            channels.add("email");
            
            // Add additional channels based on priority
            if ("high".equals(notification.getPriority())) {
                channels.add("sms");
                channels.add("push_notification");
            }
            
            return Map.of(
                "message", notification.getMessage(),
                "channels", channels,
                "deliveredVia", "email"
            );
        } catch (Exception e) {
            log.error("Error processing generic notification", e);
            return Map.of("error", "Failed to process notification");
        }
    }
    
    // Get notification history for a user
    public Map<String, Object> getNotificationHistory(Long userId, String type, int limit) {
        try {
            // In a real implementation, this would query the database
            List<Map<String, Object>> history = generateMockNotificationHistory(userId, type, limit);
            
            return Map.of(
                "success", true,
                "notifications", history,
                "total", history.size(),
                "unreadCount", history.stream()
                    .mapToInt(n -> ((Boolean) n.getOrDefault("read", false)) ? 0 : 1)
                    .sum()
            );
        } catch (Exception e) {
            log.error("Error getting notification history", e);
            return Map.of("error", "Failed to retrieve notification history");
        }
    }
    
    // Get notification preferences for a user
    public Map<String, Object> getNotificationPreferences(Long userId) {
        try {
            // In a real implementation, this would be stored in database
            Map<String, Object> preferences = Map.of(
                "emailNotifications", true,
                "smsNotifications", true,
                "pushNotifications", true,
                "attendanceAlerts", true,
                "gradeUpdates", true,
                "paymentReminders", true,
                "examReminders", true,
                "systemAnnouncements", true,
                "quietHours", Map.of(
                    "start", "22:00",
                    "end", "07:00"
                ),
                "frequency", Map.of(
                    "immediate", "critical",
                    "high", "2 hours",
                    "medium", "6 hours",
                    "low", "24 hours"
                )
            );
            
            return Map.of(
                "success", true,
                "preferences", preferences
            );
        } catch (Exception e) {
            log.error("Error getting notification preferences", e);
            return Map.of("error", "Failed to retrieve notification preferences");
        }
    }
    
    // Update notification preferences
    public Map<String, Object> updateNotificationPreferences(Long userId, Map<String, Object> preferences) {
        try {
            // In a real implementation, this would update the database
            return Map.of(
                "success", true,
                "message", "Notification preferences updated successfully",
                "updatedPreferences", preferences
            );
        } catch (Exception e) {
            log.error("Error updating notification preferences", e);
            return Map.of("error", "Failed to update notification preferences");
        }
    }
    
    // Helper methods
    private double calculateStudentGPA(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return grades.stream()
                .mapToDouble(grade -> grade.getGradePoints() != null ? grade.getGradePoints() : 2.0)
                .average()
                .orElse(2.0);
    }
    
    private double calculateStudentAttendanceRate(Long studentId) {
        List<Attendance> attendances = attendanceRepository.findByStudentId(studentId);
        if (attendances.isEmpty()) return 0.0;
        
        long presentCount = attendances.stream()
                .filter(a -> "PRESENT".equals(a.getStatus()))
                .count();
        
        return attendances.size() > 0 ? (double) presentCount / attendances.size() * 100 : 0.0;
    }
    
    private List<Map<String, Object>> generateMockNotificationHistory(Long userId, String type, int limit) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // Generate mock notification history
        for (int i = 0; i < Math.min(limit, 10); i++) {
            String notificationType = type != null ? type : "general";
            
            history.add(Map.of(
                "id", "notification_" + (i + 1),
                "type", notificationType,
                "message", generateMockNotificationMessage(notificationType, i),
                "createdAt", LocalDateTime.now().minusHours(i * 2),
                "read", i % 3 == 0,
                "priority", i % 4 == 0 ? "high" : "medium"
            ));
        }
        
        return history;
    }
    
    private String generateMockNotificationMessage(String type, int index) {
        switch (type) {
            case "attendance_alert":
                return List.of("Your attendance is below 80%", "Please attend classes regularly", "Attendance recorded for today").get(index % 3);
            case "grade_posted":
                return List.of("New grade posted", "You received a B+ in Mathematics", "Check your grade details online").get(index % 3);
            case "payment_reminder":
                return List.of("Payment due soon", "Your tuition payment is due in 3 days", "Payment received", "Payment processed successfully").get(index % 2);
            case "exam_scheduled":
                return List.of("Exam reminder", "Math exam tomorrow at 10 AM", "Physics exam next week").get(index % 2);
            default:
                return List.of("System notification", "Campus WiFi maintenance scheduled", "New course registration open").get(index % 1);
        }
    }
}

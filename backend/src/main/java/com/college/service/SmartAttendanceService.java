package com.college.service;

import com.college.model.Attendance;
import com.college.model.Student;
import com.college.repository.AttendanceRepository;
import com.college.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SmartAttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    
    // Facial Recognition Attendance
    public Attendance markAttendanceWithFacialRecognition(Long studentId, String courseId, byte[] facialData) {
        try {
            Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
            
            // Simulate facial recognition processing
            boolean isVerified = verifyFacialRecognition(studentId, facialData);
            
            if (isVerified) {
                Attendance attendance = new Attendance();
                attendance.setStudent(student);
                attendance.setCourse(null); // Will be set properly
                attendance.setAttendanceDate(LocalDateTime.now());
                attendance.setCheckInTime(LocalDateTime.now());
                attendance.setStatus("PRESENT");
                attendance.setRemarks("AI_FACIAL_RECOGNITION");
                
                return attendanceRepository.save(attendance);
            } else {
                throw new RuntimeException("Facial verification failed");
            }
        } catch (Exception e) {
            log.error("Error in facial recognition attendance", e);
            throw new RuntimeException("Attendance marking failed");
        }
    }
    
    // Predictive Attendance Analytics
    public Map<String, Object> predictAttendancePatterns(Long studentId) {
        try {
            List<Attendance> attendances = attendanceRepository.findByStudentId(studentId);
            
            if (attendances.size() < 5) {
                return Map.of("error", "Insufficient data for prediction");
            }
            
            // Calculate attendance patterns
            double attendanceRate = calculateAttendanceRate(attendances);
            Map<String, Double> dayPatterns = calculateDayPatterns(attendances);
            List<Double> weeklyTrends = calculateWeeklyTrends(attendances);
            
            // Predict next week's attendance
            double predictedAttendance = predictNextWeekAttendance(weeklyTrends);
            
            return Map.of(
                "currentAttendanceRate", attendanceRate,
                "dayPatterns", dayPatterns,
                "weeklyTrends", weeklyTrends,
                "predictedNextWeek", predictedAttendance,
                "riskLevel", calculateRiskLevel(attendanceRate),
                "recommendations", generateAttendanceRecommendations(attendanceRate, dayPatterns)
            );
        } catch (Exception e) {
            log.error("Error predicting attendance patterns", e);
            return Map.of("error", "Prediction failed");
        }
    }
    
    // At-Risk Student Identification
    public List<Map<String, Object>> identifyAtRiskStudents() {
        try {
            List<Student> allStudents = studentRepository.findAll();
            List<Map<String, Object>> atRiskStudents = new ArrayList<>();
            
            for (Student student : allStudents) {
                List<Attendance> attendances = attendanceRepository.findByStudentId(student.getId());
                
                if (attendances.size() >= 5) {
                    double attendanceRate = calculateAttendanceRate(attendances);
                    double recentTrend = calculateRecentTrend(attendances);
                    
                    if (attendanceRate < 75.0 || recentTrend < -10.0) {
                        Map<String, Object> riskData = new HashMap<>();
                        riskData.put("studentId", student.getId());
                        riskData.put("studentName", student.getFirstName() + " " + student.getLastName());
                        riskData.put("attendanceRate", attendanceRate);
                        riskData.put("recentTrend", recentTrend);
                        riskData.put("riskLevel", calculateRiskLevel(attendanceRate));
                        riskData.put("recommendations", generateInterventionRecommendations(attendanceRate, recentTrend));
                        
                        atRiskStudents.add(riskData);
                    }
                }
            }
            
            return atRiskStudents.stream()
                .sorted((a, b) -> Double.compare((Double) b.get("attendanceRate"), (Double) a.get("attendanceRate")))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error identifying at-risk students", e);
            return Collections.emptyList();
        }
    }
    
    // Smart Attendance Notifications
    public Map<String, Object> generateSmartNotifications(Long studentId) {
        try {
            List<Attendance> attendances = attendanceRepository.findByStudentId(studentId);
            double attendanceRate = calculateAttendanceRate(attendances);
            
            List<Map<String, Object>> notifications = new ArrayList<>();
            
            // Attendance threshold warnings
            if (attendanceRate < 60.0) {
                notifications.add(Map.of(
                    "type", "CRITICAL",
                    "message", "Your attendance is critically low. Immediate action required.",
                    "priority", "HIGH",
                    "action", "Contact academic advisor immediately"
                ));
            } else if (attendanceRate < 75.0) {
                notifications.add(Map.of(
                    "type", "WARNING",
                    "message", "Your attendance is below the required threshold.",
                    "priority", "MEDIUM",
                    "action", "Improve attendance to avoid academic consequences"
                ));
            }
            
            // Pattern-based notifications
            Map<String, Double> dayPatterns = calculateDayPatterns(attendances);
            String weakestDay = findWeakestDay(dayPatterns);
            
            if (dayPatterns.get(weakestDay) < 50.0) {
                notifications.add(Map.of(
                    "type", "PATTERN",
                    "message", "You frequently miss classes on " + weakestDay,
                    "priority", "LOW",
                    "action", "Review your schedule for " + weakestDay
                ));
            }
            
            return Map.of(
                "studentId", studentId,
                "attendanceRate", attendanceRate,
                "notifications", notifications,
                "generatedAt", LocalDateTime.now()
            );
        } catch (Exception e) {
            log.error("Error generating smart notifications", e);
            return Map.of("error", "Notification generation failed");
        }
    }
    
    // Helper methods
    private boolean verifyFacialRecognition(Long studentId, byte[] facialData) {
        // Simulate facial recognition verification
        // In real implementation, this would use OpenCV and deep learning models
        return Math.random() > 0.1; // 90% success rate for demo
    }
    
    private double calculateConfidenceScore(byte[] facialData) {
        // Simulate confidence calculation
        return 0.85 + (Math.random() * 0.15); // 85-100% confidence
    }
    
    private double calculateAttendanceRate(List<Attendance> attendances) {
        long totalClasses = attendances.size();
        long presentClasses = attendances.stream()
            .filter(a -> "PRESENT".equals(a.getStatus()))
            .count();
        
        return totalClasses > 0 ? (double) presentClasses / totalClasses * 100 : 0.0;
    }
    
    private Map<String, Double> calculateDayPatterns(List<Attendance> attendances) {
        Map<String, List<Double>> dayAttendance = new HashMap<>();
        
        for (Attendance attendance : attendances) {
            String day = attendance.getAttendanceDate().getDayOfWeek().toString();
            dayAttendance.computeIfAbsent(day, k -> new ArrayList<>())
                .add("PRESENT".equals(attendance.getStatus()) ? 1.0 : 0.0);
        }
        
        Map<String, Double> dayPatterns = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : dayAttendance.entrySet()) {
            double avg = entry.getValue().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0) * 100;
            dayPatterns.put(entry.getKey(), avg);
        }
        
        return dayPatterns;
    }
    
    private List<Double> calculateWeeklyTrends(List<Attendance> attendances) {
        // Simplified trend calculation
        List<Double> trends = new ArrayList<>();
        int weekSize = 7;
        
        for (int i = 0; i < attendances.size(); i += weekSize) {
            int endIndex = Math.min(i + weekSize, attendances.size());
            List<Attendance> week = attendances.subList(i, endIndex);
            
            double weekRate = calculateAttendanceRate(week);
            trends.add(weekRate);
        }
        
        return trends;
    }
    
    private double predictNextWeekAttendance(List<Double> weeklyTrends) {
        if (weeklyTrends.size() < 3) {
            return weeklyTrends.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(75.0);
        }
        
        // Simple linear regression for prediction
        int n = weeklyTrends.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += weeklyTrends.get(i);
            sumXY += i * weeklyTrends.get(i);
            sumX2 += i * i;
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        
        return slope * n + intercept;
    }
    
    private String calculateRiskLevel(double attendanceRate) {
        if (attendanceRate >= 90) return "LOW";
        if (attendanceRate >= 75) return "MEDIUM";
        if (attendanceRate >= 60) return "HIGH";
        return "CRITICAL";
    }
    
    private List<String> generateAttendanceRecommendations(double attendanceRate, Map<String, Double> dayPatterns) {
        List<String> recommendations = new ArrayList<>();
        
        if (attendanceRate < 75) {
            recommendations.add("Set up automatic reminders for classes");
            recommendations.add("Review your schedule and identify conflicts");
        }
        
        String weakestDay = findWeakestDay(dayPatterns);
        if (dayPatterns.get(weakestDay) < 60) {
            recommendations.add("Pay special attention to " + weakestDay + " classes");
        }
        
        return recommendations;
    }
    
    private String findWeakestDay(Map<String, Double> dayPatterns) {
        return dayPatterns.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("MONDAY");
    }
    
    private double calculateRecentTrend(List<Attendance> attendances) {
        if (attendances.size() < 10) return 0.0;
        
        List<Attendance> recent = attendances.subList(0, Math.min(10, attendances.size()));
        List<Attendance> previous = attendances.subList(
            Math.min(10, attendances.size()), 
            Math.min(20, attendances.size())
        );
        
        double recentRate = calculateAttendanceRate(recent);
        double previousRate = calculateAttendanceRate(previous);
        
        return recentRate - previousRate;
    }
    
    private List<String> generateInterventionRecommendations(double attendanceRate, double trend) {
        List<String> recommendations = new ArrayList<>();
        
        if (attendanceRate < 60) {
            recommendations.add("Immediate academic advisor meeting required");
            recommendations.add("Consider academic probation warning");
        } else if (attendanceRate < 75) {
            recommendations.add("Schedule counseling session");
            recommendations.add("Review academic support services");
        }
        
        if (trend < -10) {
            recommendations.add("Analyze recent attendance decline");
            recommendations.add("Check for personal or academic issues");
        }
        
        return recommendations;
    }
}

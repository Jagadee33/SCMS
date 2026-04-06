package com.college.service;

import com.college.model.Grade;
import com.college.model.Student;
import com.college.model.Attendance;
import com.college.repository.GradeRepository;
import com.college.repository.StudentRepository;
import com.college.repository.AttendanceRepository;
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
public class PerformancePredictionService {
    
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final AIModelCache aiModelCache;
    
    // Predict student performance based on historical data
    public Map<String, Object> predictStudentPerformance(Long studentId) {
        try {
            // Check cache first
            Object cachedPrediction = aiModelCache.getCachedPrediction(studentId);
            if (cachedPrediction != null && aiModelCache.isCacheFresh(studentId, 30)) {
                log.info("Returning cached prediction for student: {}", studentId);
                return (Map<String, Object>) cachedPrediction;
            }
            
            Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
            
            // Get historical grades
            List<Grade> grades = gradeRepository.findByStudent(student);
            
            // Get attendance data
            List<Attendance> attendances = attendanceRepository.findByStudentId(studentId);
            
            if (grades.size() < 3 || attendances.size() < 5) {
                Map<String, Object> insufficientDataResponse = Map.of(
                    "error", "Insufficient data for prediction",
                    "message", "Need at least 3 grades and 5 attendance records",
                    "dataPoints", grades.size(),
                    "attendancePoints", attendances.size()
                );
                aiModelCache.putCachedPrediction(studentId, insufficientDataResponse);
                return insufficientDataResponse;
            }
            
            // Calculate performance metrics
            double currentGPA = calculateGPA(grades);
            double attendanceRate = calculateAttendanceRate(attendances);
            double gradeTrend = calculateGradeTrend(grades);
            double attendanceTrend = calculateAttendanceTrend(attendances);
            
            // Predict future performance
            Map<String, Object> predictions = generatePredictions(
                currentGPA, attendanceRate, gradeTrend, attendanceTrend
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("studentId", studentId);
            result.put("studentName", student.getFirstName() + " " + student.getLastName());
            result.put("currentGPA", currentGPA);
            result.put("attendanceRate", attendanceRate);
            result.put("gradeTrend", gradeTrend);
            result.put("attendanceTrend", attendanceTrend);
            result.put("predictions", predictions);
            result.put("recommendations", generateRecommendations(currentGPA, attendanceRate, gradeTrend));
            result.put("riskLevel", calculateRiskLevel(currentGPA, attendanceRate));
            result.put("generatedAt", LocalDateTime.now());
            result.put("cacheStatus", "FRESH_CALCULATION");
            
            // Cache the result
            aiModelCache.putCachedPrediction(studentId, result);
            
            return result;
        } catch (Exception e) {
            log.error("Error predicting student performance", e);
            return Map.of("error", "Prediction failed: " + e.getMessage());
        }
    }
    
    // Get at-risk students based on performance prediction
    public List<Map<String, Object>> identifyAtRiskStudents() {
        try {
            List<Student> allStudents = studentRepository.findAll();
            List<Map<String, Object>> atRiskStudents = new ArrayList<>();
            
            for (Student student : allStudents) {
                Map<String, Object> performance = predictStudentPerformance(student.getId());
                
                if (!performance.containsKey("error")) {
                    String riskLevel = (String) performance.get("riskLevel");
                    double currentGPA = (Double) performance.get("currentGPA");
                    double attendanceRate = (Double) performance.get("attendanceRate");
                    
                    if (("HIGH".equals(riskLevel) || "CRITICAL".equals(riskLevel)) || 
                        currentGPA < 2.0 || attendanceRate < 75.0) {
                        
                        Map<String, Object> riskData = new HashMap<>();
                        riskData.put("studentId", student.getId());
                        riskData.put("studentName", student.getFirstName() + " " + student.getLastName());
                        riskData.put("currentGPA", currentGPA);
                        riskData.put("attendanceRate", attendanceRate);
                        riskData.put("riskLevel", riskLevel);
                        riskData.put("gradeTrend", performance.get("gradeTrend"));
                        riskData.put("attendanceTrend", performance.get("attendanceTrend"));
                        riskData.put("recommendations", performance.get("recommendations"));
                        
                        atRiskStudents.add(riskData);
                    }
                }
            }
            
            return atRiskStudents.stream()
                .sorted((a, b) -> {
                    String levelA = (String) a.get("riskLevel");
                    String levelB = (String) b.get("riskLevel");
                    
                    int priorityA = getPriorityForRiskLevel(levelA);
                    int priorityB = getPriorityForRiskLevel(levelB);
                    
                    return Integer.compare(priorityB, priorityA);
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error identifying at-risk students", e);
            return Collections.emptyList();
        }
    }
    
    // Get class performance analytics
    public Map<String, Object> getClassPerformanceAnalytics(String courseId) {
        try {
            // Get all students (simplified approach)
            List<Student> students = studentRepository.findAll();
            List<Map<String, Object>> classPerformance = new ArrayList<>();
            
            double totalGPA = 0.0;
            double totalAttendance = 0.0;
            int validStudents = 0;
            
            for (Student student : students) {
                Map<String, Object> performance = predictStudentPerformance(student.getId());
                
                if (!performance.containsKey("error")) {
                    classPerformance.add(performance);
                    totalGPA += (Double) performance.get("currentGPA");
                    totalAttendance += (Double) performance.get("attendanceRate");
                    validStudents++;
                }
            }
            
            if (validStudents == 0) {
                return Map.of("error", "No performance data available for this class");
            }
            
            double averageGPA = totalGPA / validStudents;
            double averageAttendance = totalAttendance / validStudents;
            
            // Calculate distribution
            Map<String, Integer> riskDistribution = calculateRiskDistribution(classPerformance);
            
            return Map.of(
                "courseId", courseId,
                "totalStudents", students.size(),
                "studentsWithPerformanceData", validStudents,
                "averageGPA", averageGPA,
                "averageAttendance", averageAttendance,
                "riskDistribution", riskDistribution,
                "topPerformers", getTopPerformers(classPerformance, 5),
                "studentsNeedingAttention", getStudentsNeedingAttention(classPerformance, 5),
                "generatedAt", LocalDateTime.now()
            );
        } catch (Exception e) {
            log.error("Error getting class performance analytics", e);
            return Map.of("error", "Analytics generation failed: " + e.getMessage());
        }
    }
    
    // Helper methods
    private double calculateGPA(List<Grade> grades) {
        if (grades.isEmpty()) return 0.0;
        
        // Weighted GPA calculation considering recent grades more heavily
        double totalWeightedPoints = 0.0;
        double totalWeight = 0.0;
        
        // Sort grades by date to give more weight to recent performance
        List<Grade> sortedGrades = grades.stream()
            .sorted(Comparator.comparing(Grade::getCreatedAt).reversed())
            .collect(Collectors.toList());
        
        for (int i = 0; i < sortedGrades.size(); i++) {
            Grade grade = sortedGrades.get(i);
            double weight = calculateGradeWeight(i, sortedGrades.size());
            double points = convertGradeToPointsFromGrade(grade);
            
            totalWeightedPoints += points * weight;
            totalWeight += weight;
        }
        
        return totalWeight > 0 ? totalWeightedPoints / totalWeight : 0.0;
    }
    
    private double calculateGradeWeight(int index, int totalGrades) {
        // Recent grades (last 30%) get more weight
        double recentThreshold = totalGrades * 0.3;
        if (index < recentThreshold) {
            return 1.5; // 50% more weight for recent grades
        }
        return 1.0; // Normal weight for older grades
    }
    
    private double convertGradeToPoints(String grade) {
        switch (grade.toUpperCase()) {
            case "A+": return 4.0;
            case "A": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "C+": return 2.7;
            case "C": return 2.3;
            case "D+": return 2.0;
            case "D": return 1.7;
            case "F": return 0.0;
            default: return 2.0; // Average for unknown grades
        }
    }
    
    private double convertGradeToPointsFromGrade(Grade grade) {
        if (grade.getGradePoints() != null) {
            return grade.getGradePoints();
        }
        return 2.0; // Default average
    }
    
    private double calculateAttendanceRate(List<Attendance> attendances) {
        if (attendances.isEmpty()) return 0.0;
        
        long presentCount = attendances.stream()
            .filter(a -> "PRESENT".equals(a.getStatus()))
            .count();
        
        return (double) presentCount / attendances.size() * 100;
    }
    
    private double calculateGradeTrend(List<Grade> grades) {
        if (grades.size() < 2) return 0.0;
        
        // Sort grades by date (assuming they have createdAt field)
        List<Grade> sortedGrades = grades.stream()
            .sorted(Comparator.comparing(Grade::getCreatedAt))
            .collect(Collectors.toList());
        
        double firstHalf = sortedGrades.subList(0, sortedGrades.size() / 2).stream()
            .mapToDouble(g -> convertGradeToPointsFromGrade(g))
            .average()
            .orElse(2.0);
        
        double secondHalf = sortedGrades.subList(sortedGrades.size() / 2, sortedGrades.size()).stream()
            .mapToDouble(g -> convertGradeToPointsFromGrade(g))
            .average()
            .orElse(2.0);
        
        return secondHalf - firstHalf;
    }
    
    private double calculateAttendanceTrend(List<Attendance> attendances) {
        if (attendances.size() < 10) return 0.0;
        
        // Sort by date
        List<Attendance> sorted = attendances.stream()
            .sorted(Comparator.comparing(Attendance::getAttendanceDate))
            .collect(Collectors.toList());
        
        int firstHalfSize = sorted.size() / 2;
        double firstHalfRate = calculateAttendanceRate(sorted.subList(0, firstHalfSize));
        double secondHalfRate = calculateAttendanceRate(sorted.subList(firstHalfSize, sorted.size()));
        
        return secondHalfRate - firstHalfRate;
    }
    
    private Map<String, Object> generatePredictions(double currentGPA, double attendanceRate, 
                                               double gradeTrend, double attendanceTrend) {
        // Advanced prediction using multiple factors
        double predictedGPA = calculateAdvancedGPAPrediction(currentGPA, gradeTrend, attendanceTrend);
        double predictedAttendance = calculateAdvancedAttendancePrediction(attendanceRate, attendanceTrend);
        
        // Calculate multiple success metrics
        double graduationProbability = calculateAdvancedGraduationProbability(predictedGPA, predictedAttendance, gradeTrend);
        double honorRollProbability = calculateAdvancedHonorRollProbability(predictedGPA, gradeTrend);
        double academicWarningProbability = calculateAdvancedWarningProbability(predictedGPA, predictedAttendance);
        
        return Map.of(
            "nextSemesterGPA", predictedGPA,
            "nextSemesterAttendance", predictedAttendance,
            "graduationProbability", graduationProbability,
            "honorRollProbability", honorRollProbability,
            "academicWarningProbability", academicWarningProbability,
            "confidence", calculatePredictionConfidence(currentGPA, attendanceRate, gradeTrend, attendanceTrend),
            "factors", Map.of(
                "gradeTrend", gradeTrend,
                "attendanceTrend", attendanceTrend,
                "consistency", calculateConsistencyScore(currentGPA, attendanceRate)
            )
        );
    }
    
    private double calculateAdvancedGPAPrediction(double currentGPA, double gradeTrend, double attendanceTrend) {
        // Base prediction on current performance
        double basePrediction = currentGPA + (gradeTrend * 0.4);
        
        // Attendance factor (good attendance improves GPA)
        double attendanceFactor = attendanceTrend > 0 ? 0.1 : (attendanceTrend < 0 ? -0.05 : 0);
        
        // Apply momentum and regression to mean
        double prediction = basePrediction + attendanceFactor;
        
        // Ensure realistic bounds
        return Math.max(0.0, Math.min(4.0, prediction));
    }
    
    private double calculateAdvancedAttendancePrediction(double currentAttendance, double attendanceTrend) {
        double basePrediction = currentAttendance + attendanceTrend;
        
        // Apply seasonal adjustment (students often improve/deteriorate during certain periods)
        double seasonalFactor = 0.95; // Slight adjustment for typical patterns
        
        return Math.max(0.0, Math.min(100.0, basePrediction * seasonalFactor));
    }
    
    private double calculateAdvancedGraduationProbability(double predictedGPA, double predictedAttendance, double gradeTrend) {
        // Multi-factor graduation probability
        double gpaFactor = Math.min(1.0, predictedGPA / 2.0); // Normalized to 2.0 requirement
        double attendanceFactor = Math.min(1.0, predictedAttendance / 75.0); // Normalized to 75% requirement
        double trendFactor = gradeTrend > -0.2 ? 1.1 : (gradeTrend < -0.3 ? 0.8 : 1.0);
        
        double baseProbability = (gpaFactor * 0.4 + attendanceFactor * 0.4 + trendFactor * 0.2);
        
        return Math.max(0.0, Math.min(1.0, baseProbability));
    }
    
    private double calculateAdvancedHonorRollProbability(double predictedGPA, double gradeTrend) {
        if (predictedGPA < 3.5) return 0.05; // Very low chance below 3.5
        
        double baseProbability = Math.max(0.0, (predictedGPA - 3.0) / 1.0);
        double trendBonus = gradeTrend > 0.1 ? 0.2 : (gradeTrend > 0 ? 0.1 : 0);
        
        return Math.min(0.95, baseProbability + trendBonus);
    }
    
    private double calculateAdvancedWarningProbability(double predictedGPA, double predictedAttendance) {
        double gpaRisk = Math.max(0.0, (2.0 - predictedGPA) / 2.0);
        double attendanceRisk = Math.max(0.0, (75.0 - predictedAttendance) / 75.0);
        
        return Math.min(1.0, (gpaRisk * 0.6 + attendanceRisk * 0.4));
    }
    
    private double calculatePredictionConfidence(double currentGPA, double attendanceRate, 
                                         double gradeTrend, double attendanceTrend) {
        // Confidence based on data quality and consistency
        double dataVolumeFactor = Math.min(1.0, (currentGPA > 0 && attendanceRate > 0) ? 0.8 : 0.5);
        double consistencyFactor = calculateConsistencyScore(currentGPA, attendanceRate);
        double trendStabilityFactor = Math.abs(gradeTrend) < 0.3 && Math.abs(attendanceTrend) < 5.0 ? 1.0 : 0.7;
        
        return Math.min(1.0, dataVolumeFactor * consistencyFactor * trendStabilityFactor);
    }
    
    private double calculateConsistencyScore(double currentGPA, double attendanceRate) {
        // High GPA with low attendance or vice versa indicates inconsistency
        double gpaScore = Math.min(1.0, currentGPA / 4.0);
        double attendanceScore = Math.min(1.0, attendanceRate / 100.0);
        
        // Consistency is higher when both are aligned
        double alignment = 1.0 - Math.abs(gpaScore - attendanceScore);
        return (gpaScore + attendanceScore) * 0.5 + alignment * 0.5;
    }
    
    private double calculateGraduationProbability(double gpa, double attendance) {
        if (gpa >= 2.0 && attendance >= 75) return 0.95;
        if (gpa >= 1.5 && attendance >= 60) return 0.75;
        if (gpa >= 1.0 && attendance >= 50) return 0.50;
        return 0.25;
    }
    
    private double calculateHonorRollProbability(double gpa) {
        if (gpa >= 3.5) return 0.85;
        if (gpa >= 3.0) return 0.60;
        if (gpa >= 2.5) return 0.30;
        return 0.10;
    }
    
    private double calculateWarningProbability(double gpa, double attendance) {
        if (gpa < 2.0 || attendance < 75) return 0.80;
        if (gpa < 2.5 || attendance < 85) return 0.50;
        return 0.20;
    }
    
    private List<String> generateRecommendations(double gpa, double attendance, double gradeTrend) {
        List<String> recommendations = new ArrayList<>();
        
        if (gpa < 2.0) {
            recommendations.add("Schedule regular tutoring sessions");
            recommendations.add("Meet with academic advisor");
            recommendations.add("Consider reducing course load");
        }
        
        if (attendance < 75) {
            recommendations.add("Improve attendance to meet requirements");
            recommendations.add("Set up automatic class reminders");
        }
        
        if (gradeTrend < -0.5) {
            recommendations.add("Review study habits and time management");
            recommendations.add("Seek help from professors early");
        }
        
        if (gpa >= 3.5 && attendance >= 95) {
            recommendations.add("Consider advanced coursework");
            recommendations.add("Explore leadership opportunities");
        }
        
        return recommendations;
    }
    
    private String calculateRiskLevel(double gpa, double attendance) {
        if (gpa >= 3.0 && attendance >= 90) return "LOW";
        if (gpa >= 2.5 && attendance >= 80) return "MEDIUM";
        if (gpa >= 2.0 && attendance >= 75) return "HIGH";
        return "CRITICAL";
    }
    
    private int getPriorityForRiskLevel(String level) {
        switch (level) {
            case "CRITICAL": return 4;
            case "HIGH": return 3;
            case "MEDIUM": return 2;
            case "LOW": return 1;
            default: return 0;
        }
    }
    
    private Map<String, Integer> calculateRiskDistribution(List<Map<String, Object>> performances) {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("LOW", 0);
        distribution.put("MEDIUM", 0);
        distribution.put("HIGH", 0);
        distribution.put("CRITICAL", 0);
        
        for (Map<String, Object> perf : performances) {
            String level = (String) perf.get("riskLevel");
            distribution.put(level, distribution.getOrDefault(level, 0) + 1);
        }
        
        return distribution;
    }
    
    private List<Map<String, Object>> getTopPerformers(List<Map<String, Object>> performances, int count) {
        return performances.stream()
            .sorted((a, b) -> Double.compare((Double) b.get("currentGPA"), (Double) a.get("currentGPA")))
            .limit(count)
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> getStudentsNeedingAttention(List<Map<String, Object>> performances, int count) {
        return performances.stream()
            .filter(p -> "HIGH".equals(p.get("riskLevel")) || "CRITICAL".equals(p.get("riskLevel")))
            .sorted((a, b) -> Double.compare((Double) a.get("currentGPA"), (Double) b.get("currentGPA")))
            .limit(count)
            .collect(Collectors.toList());
    }
}

package com.college.service;

import com.college.model.*;
import com.college.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudyScheduleOptimizationService {
    
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    // Study optimization based on student performance and patterns
    public Map<String, Object> generateOptimizedStudySchedule(Long studentId) {
        try {
            Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
            
            // Get student performance data
            List<Grade> grades = gradeRepository.findByStudent(student);
            List<Attendance> attendances = attendanceRepository.findByStudentId(studentId);
            List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
            
            if (grades.size() < 3 || attendances.size() < 5) {
                return Map.of(
                    "error", "Insufficient data for optimization",
                    "message", "Need at least 3 grades and 5 attendance records"
                );
            }
            
            // Analyze student patterns
            StudentPerformanceProfile profile = analyzeStudentPerformance(student, grades, attendances);
            
            // Generate optimized schedule
            List<StudyBlock> optimizedSchedule = generateOptimizedSchedule(profile, enrollments);
            
            // Calculate optimization metrics
            Map<String, Object> optimizationMetrics = calculateOptimizationMetrics(profile, optimizedSchedule);
            
            return Map.of(
                "studentId", studentId,
                "studentName", student.getFirstName() + " " + student.getLastName(),
                "performanceProfile", profile,
                "optimizedSchedule", optimizedSchedule,
                "optimizationMetrics", optimizationMetrics,
                "recommendations", generateStudyRecommendations(profile),
                "generatedAt", LocalDateTime.now(),
                "confidence", calculateOptimizationConfidence(profile)
            );
        } catch (Exception e) {
            log.error("Error generating optimized study schedule", e);
            return Map.of("error", "Schedule optimization failed: " + e.getMessage());
        }
    }
    
    // Analyze student performance patterns
    private StudentPerformanceProfile analyzeStudentPerformance(Student student, List<Grade> grades, List<Attendance> attendances) {
        double gpa = calculateGPA(grades);
        double attendanceRate = calculateAttendanceRate(attendances);
        
        // Identify learning patterns
        LearningPattern learningPattern = identifyLearningPattern(grades, attendances);
        StudyPreferences preferences = identifyStudyPreferences(student, grades, attendances);
        
        return StudentPerformanceProfile.builder()
                .studentId(studentId)
                .gpa(gpa)
                .attendanceRate(attendanceRate)
                .learningPattern(learningPattern)
                .preferences(preferences)
                .strengths(identifyStudentStrengths(grades))
                .weaknesses(identifyStudentWeaknesses(grades))
                .optimalStudyTime(calculateOptimalStudyTime(attendances))
                .studyEfficiency(calculateStudyEfficiency(grades, attendances))
                .build();
    }
    
    private LearningPattern identifyLearningPattern(List<Grade> grades, List<Attendance> attendances) {
        // Analyze grade distribution by subject/time
        Map<String, Double> subjectPerformance = grades.stream()
            .collect(Collectors.groupingBy(
                grade -> grade.getCourse() != null ? grade.getCourse().getName() : "General",
                Collectors.averagingDouble(Grade::getGradePoints)
            ));
        
        // Analyze attendance patterns
        Map<DayOfWeek, Double> dayOfWeekPerformance = attendances.stream()
            .filter(a -> a.getAttendanceDate() != null)
            .collect(Collectors.groupingBy(
                a -> a.getAttendanceDate().getDayOfWeek(),
                Collectors.averagingDouble(a -> "PRESENT".equals(a.getStatus()) ? 1.0 : 0.0)
            ));
        
        return LearningPattern.builder()
                .bestSubject(subjectPerformance.entrySet().stream()
                    .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("General"))
                .worstSubject(subjectPerformance.entrySet().stream()
                    .min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("General"))
                .bestDayOfWeek(dayOfWeekPerformance.entrySet().stream()
                    .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(DayOfWeek.MONDAY))
                .worstDayOfWeek(dayOfWeekPerformance.entrySet().stream()
                    .min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(DayOfWeek.FRIDAY))
                .build();
    }
    
    private StudyPreferences identifyStudyPreferences(Student student, List<Grade> grades, List<Attendance> attendances) {
        return StudyPreferences.builder()
                .preferredStudyDuration(calculateOptimalStudyDuration(attendances))
                .preferredBreakInterval(calculateOptimalBreakInterval(grades))
                .preferredStudyMethod(identifyPreferredStudyMethod(grades))
                .preferredEnvironment(calculatePreferredStudyEnvironment(attendances))
                .difficultyAdjustment(calculateDifficultyAdjustment(grades))
                .build();
    }
    
    private List<String> identifyStudentStrengths(List<Grade> grades) {
        return grades.stream()
            .filter(grade -> grade.getGradePoints() != null && grade.getGradePoints() >= 3.5)
            .map(grade -> grade.getCourse() != null ? grade.getCourse().getName() : "General Studies")
            .distinct()
            .collect(Collectors.toList());
    }
    
    private List<String> identifyStudentWeaknesses(List<Grade> grades) {
        return grades.stream()
            .filter(grade -> grade.getGradePoints() != null && grade.getGradePoints() < 2.5)
            .map(grade -> grade.getCourse() != null ? grade.getCourse().getName() : "General Studies")
            .distinct()
            .collect(Collectors.toList());
    }
    
    private LocalTime calculateOptimalStudyTime(List<Attendance> attendances) {
        // Find most productive time based on attendance patterns
        Map<Integer, Long> hourProductivity = attendances.stream()
            .filter(a -> a.getAttendanceDate() != null && "PRESENT".equals(a.getStatus()))
            .collect(Collectors.groupingBy(
                a -> a.getAttendanceDate().getHour(),
                Collectors.counting()
            ));
        
        return hourProductivity.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .map(hour -> LocalTime.of(hour, 0))
            .orElse(LocalTime.of(9, 0)); // Default 9 AM
    }
    
    private int calculateOptimalStudyDuration(List<Grade> grades) {
        double avgGradePoints = grades.stream()
            .mapToDouble(grade -> grade.getGradePoints() != null ? grade.getGradePoints() : 2.0)
            .average()
            .orElse(2.0);
        
        if (avgGradePoints >= 3.7) return 45; // High performer - shorter sessions
        if (avgGradePoints >= 3.0) return 60; // Good performer - moderate sessions
        if (avgGradePoints >= 2.3) return 75; // Average performer - longer sessions
        return 90; // Below average - longest sessions
    }
    
    private int calculateOptimalBreakInterval(List<Grade> grades) {
        double avgGradePoints = grades.stream()
            .mapToDouble(grade -> grade.getGradePoints() != null ? grade.getGradePoints() : 2.0)
            .average()
            .orElse(2.0);
        
        if (avgGradePoints >= 3.5) return 25; // High performer - shorter breaks
        if (avgGradePoints >= 2.5) return 35; // Good performer - moderate breaks
        return 50; // Average performer - longer breaks
    }
    
    private String identifyPreferredStudyMethod(List<Grade> grades) {
        Map<String, Long> methodCounts = grades.stream()
            .filter(grade -> grade.getGradeType() != null)
            .collect(Collectors.groupingBy(Grade::getGradeType, Collectors.counting()));
        
        return methodCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("MIXED"); // Default mixed approach
    }
    
    private String calculatePreferredStudyEnvironment(List<Attendance> attendances) {
        long morningSessions = attendances.stream()
            .filter(a -> a.getAttendanceDate() != null)
            .filter(a -> a.getAttendanceDate().getHour() >= 6 && a.getAttendanceDate().getHour() < 12)
            .count();
        
        long afternoonSessions = attendances.stream()
            .filter(a -> a.getAttendanceDate() != null)
            .filter(a -> a.getAttendanceDate().getHour() >= 12 && a.getAttendanceDate().getHour() < 18)
            .count();
        
        return afternoonSessions > morningSessions ? "AFTERNOON" : "MORNING";
    }
    
    private double calculateDifficultyAdjustment(List<Grade> grades) {
        return grades.stream()
            .mapToDouble(grade -> grade.getGradePoints() != null ? grade.getGradePoints() : 2.0)
            .average()
            .orElse(2.0);
    }
    
    private List<StudyBlock> generateOptimizedSchedule(StudentPerformanceProfile profile, List<Enrollment> enrollments) {
        List<StudyBlock> schedule = new ArrayList<>();
        LocalTime currentTime = profile.getOptimalStudyTime();
        DayOfWeek currentDay = LocalDateTime.now().getDayOfWeek();
        
        // Sort enrollments by difficulty based on student profile
        List<Enrollment> sortedEnrollments = enrollments.stream()
            .sorted((e1, e2) -> {
                double difficulty1 = calculateCourseDifficulty(e1.getCourse());
                double difficulty2 = calculateCourseDifficulty(e2.getCourse());
                return Double.compare(difficulty1, difficulty2);
            })
            .collect(Collectors.toList());
        
        // Generate study blocks with AI optimization
        for (Enrollment enrollment : sortedEnrollments) {
            StudyBlock block = createOptimizedStudyBlock(enrollment, profile, currentTime, currentDay);
            schedule.add(block);
        }
        
        return schedule;
    }
    
    private StudyBlock createOptimizedStudyBlock(Enrollment enrollment, StudentPerformanceProfile profile, 
                                             LocalTime currentTime, DayOfWeek currentDay) {
        double courseDifficulty = calculateCourseDifficulty(enrollment.getCourse());
        int studyDuration = profile.getPreferences().getPreferredStudyDuration();
        int breakInterval = profile.getPreferences().getPreferredBreakInterval();
        
        // AI-optimized time allocation
        double adjustedDuration = adjustDurationBasedOnPerformance(
            studyDuration, profile.getGpa(), courseDifficulty
        );
        
        return StudyBlock.builder()
                .courseId(enrollment.getCourse().getId())
                .courseName(enrollment.getCourse().getName())
                .dayOfWeek(currentDay)
                .startTime(calculateOptimalStartTime(currentTime, profile))
                .endTime(calculateOptimalStartTime(currentTime, profile).plusMinutes(adjustedDuration))
                .studyDuration(adjustedDuration)
                .breakDuration(breakInterval)
                .difficulty(courseDifficulty)
                .studyMethod(profile.getPreferences().getPreferredStudyMethod())
                .environment(profile.getPreferences().getPreferredEnvironment())
                .aiOptimization(true)
                .build();
    }
    
    private LocalTime calculateOptimalStartTime(LocalTime currentTime, StudentPerformanceProfile profile) {
        // Adjust start time based on student's optimal study time
        int optimalHour = profile.getOptimalStudyTime().getHour();
        int currentHour = currentTime.getHour();
        
        if (Math.abs(currentHour - optimalHour) <= 2) {
            return LocalTime.of(optimalHour, 0);
        } else if (currentHour < optimalHour) {
            return LocalTime.of(currentHour + 1, 0); // Start earlier
        } else {
            return LocalTime.of(currentHour, 0); // Start at current time
        }
    }
    
    private double adjustDurationBasedOnPerformance(int baseDuration, double gpa, double difficulty) {
        // High performers get more done in less time
        double performanceFactor = Math.min(1.5, gpa / 3.0);
        
        // Harder courses need more time
        double difficultyFactor = difficulty / 4.0; // Normalized to 4.0 max difficulty
        
        return baseDuration * (2.0 - performanceFactor + difficultyFactor);
    }
    
    private double calculateCourseDifficulty(Course course) {
        // Simple heuristic based on course level and type
        if (course.getName() == null) return 2.0; // Default difficulty
        
        String courseName = course.getName().toLowerCase();
        if (courseName.contains("advanced") || courseName.contains("honors")) return 4.0;
        if (courseName.contains("intro") || courseName.contains("basic")) return 1.0;
        if (courseName.contains("math") || courseName.contains("science")) return 3.5;
        if (courseName.contains("literature") || courseName.contains("history")) return 2.5;
        
        return 2.0; // Default
    }
    
    private Map<String, Object> calculateOptimizationMetrics(StudentPerformanceProfile profile, List<StudyBlock> schedule) {
        double totalStudyTime = schedule.stream()
            .mapToDouble(StudyBlock::getStudyDuration)
            .sum();
        
        double optimalStudyTime = schedule.stream()
            .mapToDouble(block -> calculateOptimalStudyTime(block))
            .average()
            .orElse(60.0);
        
        double efficiency = calculateScheduleEfficiency(schedule, profile);
        
        return Map.of(
            "totalStudyTime", totalStudyTime,
            "optimalStudyTime", optimalStudyTime,
            "scheduleEfficiency", efficiency,
            "timeUtilization", (totalStudyTime / (schedule.size() * 120.0)) * 100, // Assuming 2 hours per day
            "breakOptimization", calculateBreakOptimization(schedule),
            "difficultyDistribution", calculateDifficultyDistribution(schedule)
        );
    }
    
    private double calculateOptimalStudyTime(StudyBlock block) {
        // Score based on multiple factors
        double timeScore = 0.0;
        
        // Preferred time slot
        if (isOptimalTimeSlot(block.getStartTime())) {
            timeScore += 30;
        }
        
        // Environment match
        if (block.getEnvironment().equals("QUIET") && 
            (block.getStartTime().getHour() >= 6 && block.getStartTime().getHour() <= 9)) {
            timeScore += 20;
        }
        
        // Duration appropriateness
        double durationScore = Math.max(0, 20 - Math.abs(block.getStudyDuration() - 60));
        
        return timeScore + durationScore;
    }
    
    private boolean isOptimalTimeSlot(LocalTime time) {
        // Define optimal slots (6-10 AM, 2-6 PM)
        return (time.getHour() >= 6 && time.getHour() <= 10) || 
               (time.getHour() >= 14 && time.getHour() <= 18);
    }
    
    private double calculateScheduleEfficiency(List<StudyBlock> schedule, StudentPerformanceProfile profile) {
        // Efficiency based on how well schedule matches student's patterns
        double patternMatchScore = 0.0;
        
        for (StudyBlock block : schedule) {
            if (block.getStartTime().getHour() >= profile.getOptimalStudyTime().getHour() - 1 &&
                block.getStartTime().getHour() <= profile.getOptimalStudyTime().getHour() + 1) {
                patternMatchScore += 10;
            }
        }
        
        return Math.min(1.0, patternMatchScore / schedule.size());
    }
    
    private Map<String, Object> calculateBreakOptimization(List<StudyBlock> schedule) {
        Map<Integer, Integer> breakGaps = new HashMap<>();
        
        for (int i = 0; i < schedule.size() - 1; i++) {
            StudyBlock current = schedule.get(i);
            StudyBlock next = schedule.get(i + 1);
            
            if (next != null && current != null) {
                int gap = (int) java.time.Duration.between(
                    current.getEndTime(), next.getStartTime()
                ).toMinutes();
                
                breakGaps.put(gap, breakGaps.getOrDefault(gap, 0) + 1);
            }
        }
        
        // Calculate optimal break gap
        int optimalGap = breakGaps.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(15);
        
        return Map.of(
            "averageBreakGap", breakGaps.values().stream().mapToInt(Integer::intValue).average(),
            "optimalBreakGap", optimalGap,
            "breakEfficiency", optimalGap > 0 ? "OPTIMAL" : "SUBOPTIMAL"
        );
    }
    
    private Map<String, Object> calculateDifficultyDistribution(List<StudyBlock> schedule) {
        Map<String, Integer> difficultyCounts = schedule.stream()
            .collect(Collectors.groupingBy(StudyBlock::getDifficulty, Collectors.counting()));
        
        return Map.of(
            "easy", difficultyCounts.getOrDefault(1.0, 0),
            "medium", difficultyCounts.getOrDefault(2.0, 0),
            "hard", difficultyCounts.getOrDefault(3.0, 0),
            "advanced", difficultyCounts.getOrDefault(4.0, 0)
        );
    }
    
    private double calculateOptimizationConfidence(StudentPerformanceProfile profile) {
        // Confidence based on data quality and pattern consistency
        double dataVolumeFactor = Math.min(1.0, (profile.getGpa() > 0 && profile.getAttendanceRate() > 0) ? 0.9 : 0.6);
        double consistencyFactor = calculatePatternConsistency(profile);
        
        return Math.min(1.0, dataVolumeFactor * consistencyFactor);
    }
    
    private double calculatePatternConsistency(StudentPerformanceProfile profile) {
        // Consistency between performance metrics
        double gpaScore = Math.min(1.0, profile.getGpa() / 4.0);
        double attendanceScore = Math.min(1.0, profile.getAttendanceRate() / 100.0);
        
        return 1.0 - Math.abs(gpaScore - attendanceScore);
    }
    
    private List<String> generateStudyRecommendations(StudentPerformanceProfile profile) {
        List<String> recommendations = new ArrayList<>();
        
        // Study time recommendations
        if (profile.getOptimalStudyTime().getHour() >= 9) {
            recommendations.add("Your optimal study time is morning. Consider scheduling important classes before noon.");
        } else {
            recommendations.add("Your optimal study time is afternoon/evening. Consider scheduling demanding classes during your peak hours.");
        }
        
        // Study method recommendations
        switch (profile.getPreferences().getPreferredStudyMethod()) {
            case "VISUAL":
                recommendations.add("Use diagrams, charts, and visual aids to enhance learning.");
                break;
            case "AUDITORY":
                recommendations.add("Record lectures and review them. Consider joining study groups for discussion.");
                break;
            case "KINESTHETIC":
                recommendations.add("Hands-on practice and real-world applications enhance understanding.");
                break;
            case "READING":
                recommendations.add("Take notes and create summaries. Use active reading techniques like SQ3R.");
                break;
            default:
                recommendations.add("Mixed study approach combining multiple learning styles may be most effective.");
        }
        
        // Performance-based recommendations
        if (profile.getGpa() < 2.0) {
            recommendations.add("Focus on foundational concepts. Consider academic support services.");
            recommendations.add("Reduce course load if possible. Prioritize core subjects.");
        } else if (profile.getGpa() >= 3.5) {
            recommendations.add("Consider advanced coursework or research opportunities.");
            recommendations.add("Maintain study-life balance to avoid burnout.");
        }
        
        // Environment recommendations
        if ("QUIET".equals(profile.getPreferences().getPreferredStudyEnvironment())) {
            recommendations.add("Use noise-canceling headphones and minimize distractions.");
            recommendations.add("Study in short, focused sessions with regular breaks.");
        } else {
            recommendations.add("Consider study groups for collaborative learning.");
            recommendations.add("Utilize campus resources like libraries and study halls.");
        }
        
        return recommendations;
    }
    
    // Helper classes for data structures
    public static class StudentPerformanceProfile {
        private Long studentId;
        private double gpa;
        private double attendanceRate;
        private LearningPattern learningPattern;
        private StudyPreferences preferences;
        private List<String> strengths;
        private List<String> weaknesses;
        private LocalTime optimalStudyTime;
        private double studyEfficiency;
        
        // Constructors
        public StudentPerformanceProfile() {}
        
        public StudentPerformanceProfile(Long studentId, double gpa, double attendanceRate, LearningPattern learningPattern, StudyPreferences preferences, List<String> strengths, List<String> weaknesses, LocalTime optimalStudyTime, double studyEfficiency) {
            this.studentId = studentId;
            this.gpa = gpa;
            this.attendanceRate = attendanceRate;
            this.learningPattern = learningPattern;
            this.preferences = preferences;
            this.strengths = strengths;
            this.weaknesses = weaknesses;
            this.optimalStudyTime = optimalStudyTime;
            this.studyEfficiency = studyEfficiency;
        }
        
        // Builder pattern
        public static StudentPerformanceProfileBuilder builder() {
            return new StudentPerformanceProfileBuilder();
        }
        
        // Getters and setters
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public double getGpa() { return gpa; }
        public void setGpa(double gpa) { this.gpa = gpa; }
        
        public double getAttendanceRate() { return attendanceRate; }
        public void setAttendanceRate(double attendanceRate) { this.attendanceRate = attendanceRate; }
        
        public LearningPattern getLearningPattern() { return learningPattern; }
        public void setLearningPattern(LearningPattern learningPattern) { this.learningPattern = learningPattern; }
        
        public StudyPreferences getPreferences() { return preferences; }
        public void setPreferences(StudyPreferences preferences) { this.preferences = preferences; }
        
        public List<String> getStrengths() { return strengths; }
        public void setStrengths(List<String> strengths) { this.strengths = strengths; }
        
        public List<String> getWeaknesses() { return weaknesses; }
        public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }
        
        public LocalTime getOptimalStudyTime() { return optimalStudyTime; }
        public void setOptimalStudyTime(LocalTime optimalStudyTime) { this.optimalStudyTime = optimalStudyTime; }
        
        public double getStudyEfficiency() { return studyEfficiency; }
        public void setStudyEfficiency(double studyEfficiency) { this.studyEfficiency = studyEfficiency; }
        
        public static class StudentPerformanceProfileBuilder {
            private Long studentId;
            private double gpa;
            private double attendanceRate;
            private LearningPattern learningPattern;
            private StudyPreferences preferences;
            private List<String> strengths;
            private List<String> weaknesses;
            private LocalTime optimalStudyTime;
            private double studyEfficiency;
            
            public StudentPerformanceProfileBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
            public StudentPerformanceProfileBuilder gpa(double gpa) { this.gpa = gpa; return this; }
            public StudentPerformanceProfileBuilder attendanceRate(double attendanceRate) { this.attendanceRate = attendanceRate; return this; }
            public StudentPerformanceProfileBuilder learningPattern(LearningPattern learningPattern) { this.learningPattern = learningPattern; return this; }
            public StudentPerformanceProfileBuilder preferences(StudyPreferences preferences) { this.preferences = preferences; return this; }
            public StudentPerformanceProfileBuilder strengths(List<String> strengths) { this.strengths = strengths; return this; }
            public StudentPerformanceProfileBuilder weaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; return this; }
            public StudentPerformanceProfileBuilder optimalStudyTime(LocalTime optimalStudyTime) { this.optimalStudyTime = optimalStudyTime; return this; }
            public StudentPerformanceProfileBuilder studyEfficiency(double studyEfficiency) { this.studyEfficiency = studyEfficiency; return this; }
            
            public StudentPerformanceProfile build() {
                return new StudentPerformanceProfile(studentId, gpa, attendanceRate, learningPattern, preferences, strengths, weaknesses, optimalStudyTime, studyEfficiency);
            }
        }
    }
    
    public static class LearningPattern {
        private String bestSubject;
        private String worstSubject;
        private DayOfWeek bestDayOfWeek;
        private DayOfWeek worstDayOfWeek;
        
        // Constructors
        public LearningPattern() {}
        
        public LearningPattern(String bestSubject, String worstSubject, DayOfWeek bestDayOfWeek, DayOfWeek worstDayOfWeek) {
            this.bestSubject = bestSubject;
            this.worstSubject = worstSubject;
            this.bestDayOfWeek = bestDayOfWeek;
            this.worstDayOfWeek = worstDayOfWeek;
        }
        
        // Builder pattern
        public static LearningPatternBuilder builder() {
            return new LearningPatternBuilder();
        }
        
        // Getters and setters
        public String getBestSubject() { return bestSubject; }
        public void setBestSubject(String bestSubject) { this.bestSubject = bestSubject; }
        
        public String getWorstSubject() { return worstSubject; }
        public void setWorstSubject(String worstSubject) { this.worstSubject = worstSubject; }
        
        public DayOfWeek getBestDayOfWeek() { return bestDayOfWeek; }
        public void setBestDayOfWeek(DayOfWeek bestDayOfWeek) { this.bestDayOfWeek = bestDayOfWeek; }
        
        public DayOfWeek getWorstDayOfWeek() { return worstDayOfWeek; }
        public void setWorstDayOfWeek(DayOfWeek worstDayOfWeek) { this.worstDayOfWeek = worstDayOfWeek; }
        
        public static class LearningPatternBuilder {
            private String bestSubject;
            private String worstSubject;
            private DayOfWeek bestDayOfWeek;
            private DayOfWeek worstDayOfWeek;
            
            public LearningPatternBuilder bestSubject(String bestSubject) { this.bestSubject = bestSubject; return this; }
            public LearningPatternBuilder worstSubject(String worstSubject) { this.worstSubject = worstSubject; return this; }
            public LearningPatternBuilder bestDayOfWeek(DayOfWeek bestDayOfWeek) { this.bestDayOfWeek = bestDayOfWeek; return this; }
            public LearningPatternBuilder worstDayOfWeek(DayOfWeek worstDayOfWeek) { this.worstDayOfWeek = worstDayOfWeek; return this; }
            
            public LearningPattern build() {
                return new LearningPattern(bestSubject, worstSubject, bestDayOfWeek, worstDayOfWeek);
            }
        }
    }
    
    public static class StudyPreferences {
        private int preferredStudyDuration;
        private int preferredBreakInterval;
        private String preferredStudyMethod;
        private String preferredEnvironment;
        private double difficultyAdjustment;
        
        // Constructors
        public StudyPreferences() {}
        
        public StudyPreferences(int preferredStudyDuration, int preferredBreakInterval, String preferredStudyMethod, String preferredEnvironment, double difficultyAdjustment) {
            this.preferredStudyDuration = preferredStudyDuration;
            this.preferredBreakInterval = preferredBreakInterval;
            this.preferredStudyMethod = preferredStudyMethod;
            this.preferredEnvironment = preferredEnvironment;
            this.difficultyAdjustment = difficultyAdjustment;
        }
        
        // Builder pattern
        public static StudyPreferencesBuilder builder() {
            return new StudyPreferencesBuilder();
        }
        
        // Getters and setters
        public int getPreferredStudyDuration() { return preferredStudyDuration; }
        public void setPreferredStudyDuration(int preferredStudyDuration) { this.preferredStudyDuration = preferredStudyDuration; }
        
        public int getPreferredBreakInterval() { return preferredBreakInterval; }
        public void setPreferredBreakInterval(int preferredBreakInterval) { this.preferredBreakInterval = preferredBreakInterval; }
        
        public String getPreferredStudyMethod() { return preferredStudyMethod; }
        public void setPreferredStudyMethod(String preferredStudyMethod) { this.preferredStudyMethod = preferredStudyMethod; }
        
        public String getPreferredEnvironment() { return preferredEnvironment; }
        public void setPreferredEnvironment(String preferredEnvironment) { this.preferredEnvironment = preferredEnvironment; }
        
        public double getDifficultyAdjustment() { return difficultyAdjustment; }
        public void setDifficultyAdjustment(double difficultyAdjustment) { this.difficultyAdjustment = difficultyAdjustment; }
        
        public static class StudyPreferencesBuilder {
            private int preferredStudyDuration;
            private int preferredBreakInterval;
            private String preferredStudyMethod;
            private String preferredEnvironment;
            private double difficultyAdjustment;
            
            public StudyPreferencesBuilder preferredStudyDuration(int preferredStudyDuration) { this.preferredStudyDuration = preferredStudyDuration; return this; }
            public StudyPreferencesBuilder preferredBreakInterval(int preferredBreakInterval) { this.preferredBreakInterval = preferredBreakInterval; return this; }
            public StudyPreferencesBuilder preferredStudyMethod(String preferredStudyMethod) { this.preferredStudyMethod = preferredStudyMethod; return this; }
            public StudyPreferencesBuilder preferredEnvironment(String preferredEnvironment) { this.preferredEnvironment = preferredEnvironment; return this; }
            public StudyPreferencesBuilder difficultyAdjustment(double difficultyAdjustment) { this.difficultyAdjustment = difficultyAdjustment; return this; }
            
            public StudyPreferences build() {
                return new StudyPreferences(preferredStudyDuration, preferredBreakInterval, preferredStudyMethod, preferredEnvironment, difficultyAdjustment);
            }
        }
    }
    
    public static class StudyBlock {
        private Long courseId;
        private String courseName;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private int studyDuration;
        private int breakDuration;
        private double difficulty;
        private String studyMethod;
        private String environment;
        private boolean aiOptimization;
        
        // Constructors
        public StudyBlock() {}
        
        public StudyBlock(Long courseId, String courseName, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, int studyDuration, int breakDuration, double difficulty, String studyMethod, String environment, boolean aiOptimization) {
            this.courseId = courseId;
            this.courseName = courseName;
            this.dayOfWeek = dayOfWeek;
            this.startTime = startTime;
            this.endTime = endTime;
            this.studyDuration = studyDuration;
            this.breakDuration = breakDuration;
            this.difficulty = difficulty;
            this.studyMethod = studyMethod;
            this.environment = environment;
            this.aiOptimization = aiOptimization;
        }
        
        // Builder pattern
        public static StudyBlockBuilder builder() {
            return new StudyBlockBuilder();
        }
        
        // Getters and setters
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }
        
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        
        public DayOfWeek getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
        
        public int getStudyDuration() { return studyDuration; }
        public void setStudyDuration(int studyDuration) { this.studyDuration = studyDuration; }
        
        public int getBreakDuration() { return breakDuration; }
        public void setBreakDuration(int breakDuration) { this.breakDuration = breakDuration; }
        
        public double getDifficulty() { return difficulty; }
        public void setDifficulty(double difficulty) { this.difficulty = difficulty; }
        
        public String getStudyMethod() { return studyMethod; }
        public void setStudyMethod(String studyMethod) { this.studyMethod = studyMethod; }
        
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        
        public boolean isAiOptimization() { return aiOptimization; }
        public void setAiOptimization(boolean aiOptimization) { this.aiOptimization = aiOptimization; }
        
        public static class StudyBlockBuilder {
            private Long courseId;
            private String courseName;
            private DayOfWeek dayOfWeek;
            private LocalTime startTime;
            private LocalTime endTime;
            private int studyDuration;
            private int breakDuration;
            private double difficulty;
            private String studyMethod;
            private String environment;
            private boolean aiOptimization;
            
            public StudyBlockBuilder courseId(Long courseId) { this.courseId = courseId; return this; }
            public StudyBlockBuilder courseName(String courseName) { this.courseName = courseName; return this; }
            public StudyBlockBuilder dayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; return this; }
            public StudyBlockBuilder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
            public StudyBlockBuilder endTime(LocalTime endTime) { this.endTime = endTime; return this; }
            public StudyBlockBuilder studyDuration(int studyDuration) { this.studyDuration = studyDuration; return this; }
            public StudyBlockBuilder breakDuration(int breakDuration) { this.breakDuration = breakDuration; return this; }
            public StudyBlockBuilder difficulty(double difficulty) { this.difficulty = difficulty; return this; }
            public StudyBlockBuilder studyMethod(String studyMethod) { this.studyMethod = studyMethod; return this; }
            public StudyBlockBuilder environment(String environment) { this.environment = environment; return this; }
            public StudyBlockBuilder aiOptimization(boolean aiOptimization) { this.aiOptimization = aiOptimization; return this; }
            
            public StudyBlock build() {
                return new StudyBlock(courseId, courseName, dayOfWeek, startTime, endTime, studyDuration, breakDuration, difficulty, studyMethod, environment, aiOptimization);
            }
        }
    }
    
    // Helper methods for calculations
    private double calculateGPA(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;
        return grades.stream()
            .filter(g -> g.getGradePoints() != null)
            .mapToDouble(Grade::getGradePoints)
            .average()
            .orElse(0.0);
    }
    
    private double calculateAttendanceRate(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) return 0.0;
        long presentCount = attendances.stream()
            .filter(a -> "PRESENT".equals(a.getStatus()))
            .count();
        return (double) presentCount / attendances.size() * 100.0;
    }
    
    private double calculateStudyEfficiency(List<Grade> grades, List<Attendance> attendances) {
        double gpa = calculateGPA(grades);
        double attendanceRate = calculateAttendanceRate(attendances);
        return (gpa / 4.0) * 0.6 + (attendanceRate / 100.0) * 0.4;
    }
}

package com.college.controller;

import com.college.model.Grade;
import com.college.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/grades")
@CrossOrigin(origins = "http://localhost:3000")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    // Basic CRUD operations
    @GetMapping
    public ResponseEntity<List<Grade>> getAllGrades() {
        List<Grade> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable Long id) {
        Grade grade = gradeService.getGradeById(id);
        if (grade != null) {
            return ResponseEntity.ok(grade);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Grade> createGrade(@RequestBody Grade grade) {
        try {
            Grade createdGrade = gradeService.createGrade(grade);
            return ResponseEntity.ok(createdGrade);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grade> updateGrade(@PathVariable Long id, @RequestBody Grade grade) {
        Grade updatedGrade = gradeService.updateGrade(id, grade);
        if (updatedGrade != null) {
            return ResponseEntity.ok(updatedGrade);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        boolean deleted = gradeService.deleteGrade(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Student grade operations
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Grade>> getStudentGrades(@PathVariable Long studentId) {
        List<Grade> grades = gradeService.getStudentGrades(studentId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<Grade>> getStudentGradesByCourse(
            @PathVariable Long studentId, 
            @PathVariable Long courseId) {
        List<Grade> grades = gradeService.getStudentGradesByCourse(studentId, courseId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}/published")
    public ResponseEntity<List<Grade>> getPublishedStudentGrades(@PathVariable Long studentId) {
        List<Grade> grades = gradeService.getPublishedStudentGrades(studentId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}/gpa")
    public ResponseEntity<Double> getStudentGPA(@PathVariable Long studentId) {
        Double gpa = gradeService.calculateStudentGPA(studentId);
        return ResponseEntity.ok(gpa);
    }

    @GetMapping("/student/{studentId}/report")
    public ResponseEntity<Map<String, Object>> getStudentGradeReport(@PathVariable Long studentId) {
        Map<String, Object> report = gradeService.getStudentGradeReport(studentId);
        if (report != null) {
            return ResponseEntity.ok(report);
        }
        return ResponseEntity.notFound().build();
    }

    // Course grade operations
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Grade>> getCourseGrades(@PathVariable Long courseId) {
        List<Grade> grades = gradeService.getCourseGrades(courseId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/course/{courseId}/statistics")
    public ResponseEntity<Map<String, Object>> getCourseGradeStatistics(@PathVariable Long courseId) {
        Map<String, Object> stats = gradeService.getCourseGradeStatistics(courseId);
        if (stats != null) {
            return ResponseEntity.ok(stats);
        }
        return ResponseEntity.notFound().build();
    }

    // Faculty grade operations
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<Grade>> getFacultyGrades(@PathVariable Long facultyId) {
        List<Grade> grades = gradeService.getFacultyGrades(facultyId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/faculty/{facultyId}/pending")
    public ResponseEntity<List<Grade>> getFacultyPendingGrades(@PathVariable Long facultyId) {
        List<Grade> grades = gradeService.getFacultyPendingGrades(facultyId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/faculty/{facultyId}/statistics")
    public ResponseEntity<Map<String, Object>> getFacultyGradeStats(@PathVariable Long facultyId) {
        Map<String, Object> stats = gradeService.getFacultyGradeStats(facultyId);
        if (stats != null) {
            return ResponseEntity.ok(stats);
        }
        return ResponseEntity.notFound().build();
    }

    // Batch operations
    @PostMapping("/batch")
    public ResponseEntity<List<Grade>> createBatchGrades(@RequestBody List<Grade> grades) {
        try {
            List<Grade> createdGrades = gradeService.createBatchGrades(grades);
            return ResponseEntity.ok(createdGrades);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/publish")
    public ResponseEntity<List<Grade>> publishGrades(@RequestBody List<Long> gradeIds) {
        try {
            List<Grade> publishedGrades = gradeService.publishGrades(gradeIds);
            return ResponseEntity.ok(publishedGrades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Grade type operations
    @GetMapping("/type/{gradeType}")
    public ResponseEntity<List<Grade>> getGradesByType(@PathVariable String gradeType) {
        List<Grade> grades = gradeService.getGradesByType(gradeType);
        return ResponseEntity.ok(grades);
    }

    // GPA calculations
    @GetMapping("/student/{studentId}/course/{courseId}/gpa")
    public ResponseEntity<Double> getCourseGPA(@PathVariable Long studentId, @PathVariable Long courseId) {
        Double gpa = gradeService.calculateCourseGPA(studentId, courseId);
        return ResponseEntity.ok(gpa);
    }

    @GetMapping("/student/{studentId}/recent")
    public ResponseEntity<List<Grade>> getRecentGrades(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Grade> grades = gradeService.getRecentGrades(studentId, limit);
        return ResponseEntity.ok(grades);
    }

    // Grade statistics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getGradeStatistics() {
        List<Grade> allGrades = gradeService.getAllGrades();
        
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalGrades", allGrades.size());
        
        // Count by status
        long draftCount = allGrades.stream().filter(g -> "DRAFT".equals(g.getStatus())).count();
        long publishedCount = allGrades.stream().filter(g -> "PUBLISHED".equals(g.getStatus())).count();
        long pendingCount = allGrades.stream().filter(g -> "PENDING".equals(g.getStatus())).count();
        
        stats.put("draftGrades", draftCount);
        stats.put("publishedGrades", publishedCount);
        stats.put("pendingGrades", pendingCount);
        
        // Count by grade type
        Map<String, Long> gradeTypeCount = allGrades.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Grade::getGradeType, 
                java.util.stream.Collectors.counting()
            ));
        stats.put("gradesByType", gradeTypeCount);
        
        // Grade distribution
        Map<String, Long> gradeDistribution = allGrades.stream()
            .filter(g -> g.getGradeLetter() != null)
            .collect(java.util.stream.Collectors.groupingBy(
                Grade::getGradeLetter, 
                java.util.stream.Collectors.counting()
            ));
        stats.put("gradeDistribution", gradeDistribution);
        
        // Average statistics
        double avgPercentage = allGrades.stream()
            .filter(g -> g.getPercentage() != null)
            .mapToDouble(Grade::getPercentage)
            .average()
            .orElse(0.0);
        stats.put("averagePercentage", avgPercentage);
        
        return ResponseEntity.ok(stats);
    }
}

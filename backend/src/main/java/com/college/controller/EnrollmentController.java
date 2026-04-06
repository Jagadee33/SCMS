package com.college.controller;

import com.college.model.Enrollment;
import com.college.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/enrollments")
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // Get all enrollments
    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    // Get enrollment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Long id) {
        Enrollment enrollment = enrollmentService.getEnrollmentById(id);
        if (enrollment != null) {
            return ResponseEntity.ok(enrollment);
        }
        return ResponseEntity.notFound().build();
    }

    // Enroll student in course
    @PostMapping("/enroll")
    public ResponseEntity<?> enrollStudent(@RequestBody Map<String, Object> enrollmentData) {
        try {
            Long studentId = ((Number) enrollmentData.get("studentId")).longValue();
            Long courseId = ((Number) enrollmentData.get("courseId")).longValue();
            String semester = (String) enrollmentData.get("semester");
            String academicYear = (String) enrollmentData.get("academicYear");

            Enrollment enrollment = enrollmentService.enrollStudent(studentId, courseId, semester, academicYear);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Student enrolled successfully");
            response.put("enrollment", enrollment);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Update enrollment
    @PutMapping("/{id}")
    public ResponseEntity<Enrollment> updateEnrollment(
            @PathVariable Long id, 
            @RequestBody Enrollment enrollment) {
        Enrollment updatedEnrollment = enrollmentService.updateEnrollment(id, enrollment);
        if (updatedEnrollment != null) {
            return ResponseEntity.ok(updatedEnrollment);
        }
        return ResponseEntity.notFound().build();
    }

    // Drop student from course
    @PutMapping("/{id}/drop")
    public ResponseEntity<?> dropStudent(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String remarks = request.get("remarks");
            Enrollment enrollment = enrollmentService.dropStudent(id, remarks);
            
            if (enrollment != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Student dropped from course successfully");
                response.put("enrollment", enrollment);
                
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Complete course
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeCourse(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Integer grade = (Integer) request.get("grade");
            String remarks = (String) request.get("remarks");
            
            Enrollment enrollment = enrollmentService.completeCourse(id, grade, remarks);
            
            if (enrollment != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Course completed successfully");
                response.put("enrollment", enrollment);
                
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get student's enrollments
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> getStudentEnrollments(@PathVariable Long studentId) {
        List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(enrollments);
    }

    // Get student's active enrollments
    @GetMapping("/student/{studentId}/active")
    public ResponseEntity<List<Enrollment>> getStudentActiveEnrollments(@PathVariable Long studentId) {
        List<Enrollment> enrollments = enrollmentService.getStudentActiveEnrollments(studentId);
        return ResponseEntity.ok(enrollments);
    }

    // Get student's completed courses
    @GetMapping("/student/{studentId}/completed")
    public ResponseEntity<List<Enrollment>> getStudentCompletedCourses(@PathVariable Long studentId) {
        List<Enrollment> enrollments = enrollmentService.getStudentCompletedCourses(studentId);
        return ResponseEntity.ok(enrollments);
    }

    // Get course enrollments
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> getCourseEnrollments(@PathVariable Long courseId) {
        List<Enrollment> enrollments = enrollmentService.getCourseEnrollments(courseId);
        return ResponseEntity.ok(enrollments);
    }

    // Get course active enrollments
    @GetMapping("/course/{courseId}/active")
    public ResponseEntity<List<Enrollment>> getCourseActiveEnrollments(@PathVariable Long courseId) {
        List<Enrollment> enrollments = enrollmentService.getCourseActiveEnrollments(courseId);
        return ResponseEntity.ok(enrollments);
    }

    // Get student GPA
    @GetMapping("/student/{studentId}/gpa")
    public ResponseEntity<Map<String, Object>> getStudentGPA(@PathVariable Long studentId) {
        Double gpa = enrollmentService.calculateStudentGPA(studentId);
        Double credits = enrollmentService.calculateCreditsEarned(studentId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("gpa", gpa);
        response.put("creditsEarned", credits);
        
        return ResponseEntity.ok(response);
    }

    // Get enrollment statistics for course
    @GetMapping("/course/{courseId}/stats")
    public ResponseEntity<List<Object[]>> getEnrollmentStats(@PathVariable Long courseId) {
        List<Object[]> stats = enrollmentService.getEnrollmentStats(courseId);
        return ResponseEntity.ok(stats);
    }

    // Delete enrollment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        boolean deleted = enrollmentService.deleteEnrollment(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

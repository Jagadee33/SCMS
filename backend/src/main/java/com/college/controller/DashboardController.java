package com.college.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.college.repository.StudentRepository;
import com.college.repository.FacultyRepository;
import com.college.repository.CourseRepository;
import com.college.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get real counts from database
            long totalStudents = studentRepository.count();
            long totalFaculty = facultyRepository.count();
            long totalCourses = courseRepository.count();
            long totalUsers = userRepository.count();
            
            // Calculate additional stats
            long activeStudents = studentRepository.countByStatus("Active");
            long recentEnrollments = studentRepository.countRecentEnrollments(java.time.LocalDate.now().minusDays(30)); // Last 30 days
            
            stats.put("totalStudents", totalStudents);
            stats.put("totalFaculty", totalFaculty);
            stats.put("totalCourses", totalCourses);
            stats.put("totalDepartments", 8L); // This could be from departments table
            stats.put("activeStudents", activeStudents);
            stats.put("recentEnrollments", recentEnrollments);
            stats.put("totalUsers", totalUsers);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching dashboard stats: " + e.getMessage());
        }
    }

    @GetMapping("/recent-activities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRecentActivities() {
        try {
            // In a real implementation, this would fetch from activity logs
            // For now, return some sample recent activities
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("activities", new java.util.ArrayList<>() {{
                    add(new HashMap<String, Object>() {{
                        put("type", "STUDENT_ENROLLMENT");
                        put("description", "New student enrolled");
                        put("details", "Rahul Kumar - B.Tech Computer Science");
                        put("timestamp", System.currentTimeMillis() - (2 * 60 * 1000));
                    }});
                    add(new HashMap<String, Object>() {{
                        put("type", "FEE_PAYMENT");
                        put("description", "Fee payment received");
                        put("details", "Priya Sharma - ₹45,000");
                        put("timestamp", System.currentTimeMillis() - (15 * 60 * 1000));
                    }});
                    add(new HashMap<String, Object>() {{
                        put("type", "COURSE_CREATION");
                        put("description", "New course added");
                        put("details", "M.Sc Data Science - Semester 1");
                        put("timestamp", System.currentTimeMillis() - (60 * 60 * 1000));
                    }});
                    add(new HashMap<String, Object>() {{
                        put("type", "FACULTY_ONBOARDING");
                        put("description", "Faculty onboarding completed");
                        put("details", "Dr. Rajesh Kumar - Computer Science");
                        put("timestamp", System.currentTimeMillis() - (3 * 60 * 60 * 1000));
                    }});
                }});
            }});
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching recent activities: " + e.getMessage());
        }
    }
}

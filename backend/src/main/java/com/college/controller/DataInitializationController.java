package com.college.controller;

import com.college.model.Course;
import com.college.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/init")
public class DataInitializationController {

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping("/courses")
    public ResponseEntity<String> initializeComputerScienceCourses() {
        try {
            // Check if courses already exist
            if (courseRepository.count() > 0) {
                return ResponseEntity.ok("Courses already exist in the database.");
            }

            List<Course> courses = Arrays.asList(
                createCourse(
                    "Data Structures and Algorithms",
                    "CS201",
                    "Study of fundamental data structures including arrays, linked lists, stacks, queues, trees, and graphs. Algorithm analysis and design techniques including sorting, searching, and dynamic programming.",
                    "Computer Science",
                    "Dr. Sarah Johnson",
                    4,
                    "16 weeks"
                ),
                createCourse(
                    "Database Management Systems",
                    "CS301",
                    "Comprehensive study of database design, implementation, and management. Topics include relational database theory, SQL, normalization, transaction management, and NoSQL databases.",
                    "Computer Science",
                    "Prof. Michael Chen",
                    4,
                    "16 weeks"
                ),
                createCourse(
                    "Web Development and Design",
                    "CS202",
                    "Modern web development covering HTML5, CSS3, JavaScript, React, and responsive design. Frontend and backend development principles including REST APIs and database integration.",
                    "Computer Science",
                    "Dr. Emily Rodriguez",
                    3,
                    "16 weeks"
                ),
                createCourse(
                    "Machine Learning Fundamentals",
                    "CS401",
                    "Introduction to machine learning concepts including supervised and unsupervised learning, neural networks, deep learning, and practical applications using Python and TensorFlow.",
                    "Computer Science",
                    "Dr. Raj Patel",
                    4,
                    "16 weeks"
                ),
                createCourse(
                    "Operating Systems",
                    "CS302",
                    "Study of operating system principles including process management, memory management, file systems, and security. Hands-on experience with Linux/Unix systems and kernel programming.",
                    "Computer Science",
                    "Prof. David Kim",
                    4,
                    "16 weeks"
                ),
                createCourse(
                    "Software Engineering Principles",
                    "CS351",
                    "Comprehensive software engineering methodology covering requirements analysis, system design, testing, maintenance, and project management. Agile methodologies and version control with Git.",
                    "Computer Science",
                    "Dr. Lisa Anderson",
                    3,
                    "16 weeks"
                )
            );

            courseRepository.saveAll(courses);
            return ResponseEntity.ok("✅ Successfully added 6 Computer Science courses to the database!");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding courses: " + e.getMessage());
        }
    }

    private Course createCourse(String name, String code, String description, String department, 
                               String instructor, Integer credits, String duration) {
        Course course = new Course();
        course.setName(name);
        course.setCode(code);
        course.setDescription(description);
        course.setDepartment(department);
        course.setInstructor(instructor);
        course.setCredits(credits);
        course.setDuration(duration);
        course.setLevel("Undergraduate"); // Add default level
        course.setStartDate(LocalDate.of(2024, 8, 1));
        course.setEndDate(LocalDate.of(2024, 12, 15));
        course.setStatus("Active");
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return course;
    }
}

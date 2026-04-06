package com.college.util;

import com.college.model.Course;
import com.college.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class CourseDataInitializer implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no courses exist
        if (courseRepository.count() == 0) {
            initializeComputerScienceCourses();
        }
    }

    private void initializeComputerScienceCourses() {
        Course course1 = new Course(
                "Data Structures and Algorithms",
                "CS201",
                "Study of fundamental data structures including arrays, linked lists, stacks, queues, trees, and graphs. Algorithm analysis and design techniques including sorting, searching, and dynamic programming.",
                "Computer Science",
                "Dr. Sarah Johnson",
                4,
                "16 weeks",
                "Active",
                "Undergraduate"
        );
        course1.setStartDate(LocalDate.of(2024, 8, 1));
        course1.setEndDate(LocalDate.of(2024, 12, 15));

        Course course2 = new Course(
                "Database Management Systems",
                "CS301",
                "Comprehensive study of database design, implementation, and management. Topics include relational database theory, SQL, normalization, transaction management, and NoSQL databases.",
                "Computer Science",
                "Prof. Michael Chen",
                4,
                "16 weeks",
                "Active",
                "Undergraduate"
        );
        course2.setStartDate(LocalDate.of(2024, 8, 1));
        course2.setEndDate(LocalDate.of(2024, 12, 15));

        Course course3 = new Course(
                "Web Development and Design",
                "CS202",
                "Modern web development covering HTML5, CSS3, JavaScript, React, and responsive design. Frontend and backend development principles including REST APIs and database integration.",
                "Computer Science",
                "Dr. Emily Rodriguez",
                3,
                "16 weeks",
                "Active",
                "Undergraduate"
        );
        course3.setStartDate(LocalDate.of(2024, 8, 1));
        course3.setEndDate(LocalDate.of(2024, 12, 15));

        Course course4 = new Course(
                "Machine Learning Fundamentals",
                "CS401",
                "Introduction to machine learning concepts including supervised and unsupervised learning, neural networks, deep learning, and practical applications using Python and TensorFlow.",
                "Computer Science",
                "Dr. Raj Patel",
                4,
                "16 weeks",
                "Active",
                "Undergraduate"
        );
        course4.setStartDate(LocalDate.of(2024, 8, 1));
        course4.setEndDate(LocalDate.of(2024, 12, 15));

        Course course5 = new Course(
                "Operating Systems",
                "CS302",
                "Study of operating system principles including process management, memory management, file systems, and security. Hands-on experience with Linux/Unix systems and kernel programming.",
                "Computer Science",
                "Prof. David Kim",
                4,
                "16 weeks",
                "Active",
                "Undergraduate"
        );
        course5.setStartDate(LocalDate.of(2024, 8, 1));
        course5.setEndDate(LocalDate.of(2024, 12, 15));

        Course course6 = new Course(
                "Software Engineering Principles",
                "CS351",
                "Comprehensive software engineering methodology covering requirements analysis, system design, testing, maintenance, and project management. Agile methodologies and version control with Git.",
                "Computer Science",
                "Dr. Lisa Anderson",
                3,
                "16 weeks",
                "Active",
                "Undergraduate"
        );
        course6.setStartDate(LocalDate.of(2024, 8, 1));
        course6.setEndDate(LocalDate.of(2024, 12, 15));

        List<Course> courses = Arrays.asList(course1, course2, course3, course4, course5, course6);

        courseRepository.saveAll(courses);
        System.out.println("✅ Successfully added 6 Computer Science courses to the database!");
    }
}

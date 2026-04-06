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
        List<Course> courses = Arrays.asList(
            Course.builder()
                .name("Data Structures and Algorithms")
                .code("CS201")
                .description("Study of fundamental data structures including arrays, linked lists, stacks, queues, trees, and graphs. Algorithm analysis and design techniques including sorting, searching, and dynamic programming.")
                .department("Computer Science")
                .instructor("Dr. Sarah Johnson")
                .credits(4)
                .duration("16 weeks")
                .level("Undergraduate")
                .startDate(LocalDate.of(2024, 8, 1))
                .endDate(LocalDate.of(2024, 12, 15))
                .status("Active")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),

            Course.builder()
                .name("Database Management Systems")
                .code("CS301")
                .description("Comprehensive study of database design, implementation, and management. Topics include relational database theory, SQL, normalization, transaction management, and NoSQL databases.")
                .department("Computer Science")
                .instructor("Prof. Michael Chen")
                .credits(4)
                .duration("16 weeks")
                .level("Undergraduate")
                .startDate(LocalDate.of(2024, 8, 1))
                .endDate(LocalDate.of(2024, 12, 15))
                .status("Active")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),

            Course.builder()
                .name("Web Development and Design")
                .code("CS202")
                .description("Modern web development covering HTML5, CSS3, JavaScript, React, and responsive design. Frontend and backend development principles including REST APIs and database integration.")
                .department("Computer Science")
                .instructor("Dr. Emily Rodriguez")
                .credits(3)
                .duration("16 weeks")
                .level("Undergraduate")
                .startDate(LocalDate.of(2024, 8, 1))
                .endDate(LocalDate.of(2024, 12, 15))
                .status("Active")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),

            Course.builder()
                .name("Machine Learning Fundamentals")
                .code("CS401")
                .description("Introduction to machine learning concepts including supervised and unsupervised learning, neural networks, deep learning, and practical applications using Python and TensorFlow.")
                .department("Computer Science")
                .instructor("Dr. Raj Patel")
                .credits(4)
                .duration("16 weeks")
                .level("Undergraduate")
                .startDate(LocalDate.of(2024, 8, 1))
                .endDate(LocalDate.of(2024, 12, 15))
                .status("Active")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),

            Course.builder()
                .name("Operating Systems")
                .code("CS302")
                .description("Study of operating system principles including process management, memory management, file systems, and security. Hands-on experience with Linux/Unix systems and kernel programming.")
                .department("Computer Science")
                .instructor("Prof. David Kim")
                .credits(4)
                .duration("16 weeks")
                .level("Undergraduate")
                .startDate(LocalDate.of(2024, 8, 1))
                .endDate(LocalDate.of(2024, 12, 15))
                .status("Active")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),

            Course.builder()
                .name("Software Engineering Principles")
                .code("CS351")
                .description("Comprehensive software engineering methodology covering requirements analysis, system design, testing, maintenance, and project management. Agile methodologies and version control with Git.")
                .department("Computer Science")
                .instructor("Dr. Lisa Anderson")
                .credits(3)
                .duration("16 weeks")
                .level("Undergraduate")
                .startDate(LocalDate.of(2024, 8, 1))
                .endDate(LocalDate.of(2024, 12, 15))
                .status("Active")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        );

        courseRepository.saveAll(courses);
        System.out.println("✅ Successfully added 6 Computer Science courses to the database!");
    }
}

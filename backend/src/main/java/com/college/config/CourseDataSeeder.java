package com.college.config;

import com.college.model.Course;
import com.college.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CourseDataSeeder implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if courses already exist
        if (courseRepository.count() == 0) {
            // Create sample courses
            createSampleCourses();
            System.out.println("Sample courses created successfully!");
        }
    }

    private void createSampleCourses() {
        // Computer Science Courses
        Course cs101 = Course.builder()
                .name("Introduction to Computer Science")
                .code("CS101")
                .description("Fundamental concepts of computer science and programming")
                .department("Computer Science")
                .instructor("Dr. Rajesh Kumar")
                .credits(3)
                .duration("16 weeks")
                .startDate(java.time.LocalDate.of(2024, 8, 1))
                .endDate(java.time.LocalDate.of(2024, 12, 15))
                .status("Active")
                .level("Beginner")
                .build();

        Course cs102 = Course.builder()
                .name("Data Structures and Algorithms")
                .code("CS102")
                .description("Advanced data structures and algorithm analysis")
                .department("Computer Science")
                .instructor("Dr. Priya Sharma")
                .credits(4)
                .duration("16 weeks")
                .startDate(java.time.LocalDate.of(2024, 8, 1))
                .endDate(java.time.LocalDate.of(2024, 12, 15))
                .status("Active")
                .level("Intermediate")
                .build();

        Course cs201 = Course.builder()
                .name("Database Management Systems")
                .code("CS201")
                .description("Database design, SQL, and database administration")
                .department("Computer Science")
                .instructor("Dr. Amit Patel")
                .credits(3)
                .duration("16 weeks")
                .startDate(java.time.LocalDate.of(2024, 8, 1))
                .endDate(java.time.LocalDate.of(2024, 12, 15))
                .status("Active")
                .level("Intermediate")
                .build();

        // Mathematics Courses
        Course math101 = Course.builder()
                .name("Calculus I")
                .code("MATH101")
                .description("Differential and integral calculus")
                .department("Mathematics")
                .instructor("Prof. Sunita Rao")
                .credits(4)
                .duration("16 weeks")
                .startDate(java.time.LocalDate.of(2024, 8, 1))
                .endDate(java.time.LocalDate.of(2024, 12, 15))
                .status("Active")
                .level("Beginner")
                .build();

        Course math102 = Course.builder()
                .name("Linear Algebra")
                .code("MATH102")
                .description("Vector spaces, matrices, and linear transformations")
                .department("Mathematics")
                .instructor("Prof. Vijay Kumar")
                .credits(3)
                .duration("16 weeks")
                .startDate(java.time.LocalDate.of(2024, 8, 1))
                .endDate(java.time.LocalDate.of(2024, 12, 15))
                .status("Active")
                .level("Intermediate")
                .build();

        // Physics Courses
        Course phys101 = Course.builder()
                .name("Physics I")
                .code("PHYS101")
                .description("Mechanics, thermodynamics, and waves")
                .department("Physics")
                .instructor("Dr. Anjali Gupta")
                .credits(4)
                .duration("16 weeks")
                .startDate(java.time.LocalDate.of(2024, 8, 1))
                .endDate(java.time.LocalDate.of(2024, 12, 15))
                .status("Active")
                .level("Beginner")
                .build();

        // Chemistry Courses
        Course chem101 = Course.builder()
                .name("General Chemistry")
                .code("CHEM101")
                .description("Fundamental principles of chemistry")
                .department("Chemistry")
                .instructor("Dr. Ramesh Singh")
                .credits(3)
                .duration("16 weeks")
                .startDate(java.time.LocalDate.of(2024, 8, 1))
                .endDate(java.time.LocalDate.of(2024, 12, 15))
                .status("Active")
                .level("Beginner")
                .build();

        // Business Courses
        Course bus101 = Course.builder()
                .name("Introduction to Business")
                .code("BUS101")
                .description("Fundamentals of business administration")
                .department("Business")
                .instructor("Prof. Meera Reddy")
                .credits(3)
                .duration("16 weeks")
                .startDate(java.time.LocalDate.of(2024, 8, 1))
                .endDate(java.time.LocalDate.of(2024, 12, 15))
                .status("Active")
                .level("Beginner")
                .build();

        Course bus201 = Course.builder()
                .name("Financial Accounting")
                .code("BUS201")
                .description("Principles of financial accounting")
                .department("Business")
                .instructor("Dr. Sanjay Kumar")
                .credits(3)
                .duration("16 weeks")
                .startDate(java.time.LocalDate.of(2024, 8, 1))
                .endDate(java.time.LocalDate.of(2024, 12, 15))
                .status("Active")
                .level("Intermediate")
                .build();

        // English Courses
        Course eng101 = Course.builder()
                .name("English Composition")
                .code("ENG101")
                .description("Academic writing and communication skills")
                .department("English")
                .instructor("Prof. Sarah Johnson")
                .credits(3)
                .duration("16 weeks")
                .startDate(java.time.LocalDate.of(2024, 8, 1))
                .endDate(java.time.LocalDate.of(2024, 12, 15))
                .status("Active")
                .level("Beginner")
                .build();

        // Save all courses
        courseRepository.save(cs101);
        courseRepository.save(cs102);
        courseRepository.save(cs201);
        courseRepository.save(math101);
        courseRepository.save(math102);
        courseRepository.save(phys101);
        courseRepository.save(chem101);
        courseRepository.save(bus101);
        courseRepository.save(bus201);
        courseRepository.save(eng101);
    }
}

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
        Course cs101 = new Course(
                "Introduction to Computer Science",
                "CS101",
                "Fundamental concepts of computer science and programming",
                "Computer Science",
                "Dr. Rajesh Kumar",
                3,
                "16 weeks",
                "Active",
                "Beginner"
        );
        cs101.setStartDate(java.time.LocalDate.of(2024, 8, 1));
        cs101.setEndDate(java.time.LocalDate.of(2024, 12, 15));

        Course cs102 = new Course(
                "Data Structures and Algorithms",
                "CS102",
                "Advanced data structures and algorithm analysis",
                "Computer Science",
                "Dr. Priya Sharma",
                4,
                "16 weeks",
                "Active",
                "Intermediate"
        );
        cs102.setStartDate(java.time.LocalDate.of(2024, 8, 1));
        cs102.setEndDate(java.time.LocalDate.of(2024, 12, 15));

        Course cs201 = new Course(
                "Database Management Systems",
                "CS201",
                "Database design, SQL, and database administration",
                "Computer Science",
                "Dr. Amit Patel",
                3,
                "16 weeks",
                "Active",
                "Intermediate"
        );
        cs201.setStartDate(java.time.LocalDate.of(2024, 8, 1));
        cs201.setEndDate(java.time.LocalDate.of(2024, 12, 15));

        // Mathematics Courses
        Course math101 = new Course(
                "Calculus I",
                "MATH101",
                "Differential and integral calculus",
                "Mathematics",
                "Prof. Sunita Rao",
                4,
                "16 weeks",
                "Active",
                "Beginner"
        );
        math101.setStartDate(java.time.LocalDate.of(2024, 8, 1));
        math101.setEndDate(java.time.LocalDate.of(2024, 12, 15));

        Course math102 = new Course(
                "Linear Algebra",
                "MATH102",
                "Vector spaces, matrices, and linear transformations",
                "Mathematics",
                "Prof. Vijay Kumar",
                3,
                "16 weeks",
                "Active",
                "Intermediate"
        );
        math102.setStartDate(java.time.LocalDate.of(2024, 8, 1));
        math102.setEndDate(java.time.LocalDate.of(2024, 12, 15));

        // Physics Courses
        Course phys101 = new Course(
                "Physics I",
                "PHYS101",
                "Mechanics, thermodynamics, and waves",
                "Physics",
                "Dr. Anjali Gupta",
                4,
                "16 weeks",
                "Active",
                "Beginner"
        );
        phys101.setStartDate(java.time.LocalDate.of(2024, 8, 1));
        phys101.setEndDate(java.time.LocalDate.of(2024, 12, 15));

        // Chemistry Courses
        Course chem101 = new Course(
                "General Chemistry",
                "CHEM101",
                "Fundamental principles of chemistry",
                "Chemistry",
                "Dr. Ramesh Singh",
                3,
                "16 weeks",
                "Active",
                "Beginner"
        );
        chem101.setStartDate(java.time.LocalDate.of(2024, 8, 1));
        chem101.setEndDate(java.time.LocalDate.of(2024, 12, 15));

        // Business Courses
        Course bus101 = new Course(
                "Introduction to Business",
                "BUS101",
                "Fundamentals of business administration",
                "Business",
                "Prof. Meera Reddy",
                3,
                "16 weeks",
                "Active",
                "Beginner"
        );
        bus101.setStartDate(java.time.LocalDate.of(2024, 8, 1));
        bus101.setEndDate(java.time.LocalDate.of(2024, 12, 15));

        Course bus201 = new Course(
                "Financial Accounting",
                "BUS201",
                "Principles of financial accounting",
                "Business",
                "Dr. Sanjay Kumar",
                3,
                "16 weeks",
                "Active",
                "Intermediate"
        );
        bus201.setStartDate(java.time.LocalDate.of(2024, 8, 1));
        bus201.setEndDate(java.time.LocalDate.of(2024, 12, 15));

        // English Courses
        Course eng101 = new Course(
                "English Composition",
                "ENG101",
                "Academic writing and communication skills",
                "English",
                "Prof. Sarah Johnson",
                3,
                "16 weeks",
                "Active",
                "Beginner"
        );
        eng101.setStartDate(java.time.LocalDate.of(2024, 8, 1));
        eng101.setEndDate(java.time.LocalDate.of(2024, 12, 15));

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

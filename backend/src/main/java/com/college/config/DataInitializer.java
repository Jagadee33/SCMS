package com.college.config;

import com.college.model.User;
import com.college.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                // Create admin user
                User admin = new User();
                admin.setEmail("admin@college.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(User.Role.ADMIN);
                admin.setEnabled(true);
                userRepository.save(admin);

                // Create sample faculty user
                User faculty = new User();
                faculty.setEmail("faculty@college.com");
                faculty.setPassword(passwordEncoder.encode("faculty123"));
                faculty.setRole(User.Role.FACULTY);
                faculty.setEnabled(true);
                userRepository.save(faculty);

                // Create sample student user
                User student = new User();
                student.setEmail("student@college.com");
                student.setPassword(passwordEncoder.encode("student123"));
                student.setRole(User.Role.STUDENT);
                student.setEnabled(true);
                userRepository.save(student);

                System.out.println("Sample users created:");
                System.out.println("Admin: admin@college.com / admin123");
                System.out.println("Faculty: faculty@college.com / faculty123");
                System.out.println("Student: student@college.com / student123");
            }
        };
    }
}

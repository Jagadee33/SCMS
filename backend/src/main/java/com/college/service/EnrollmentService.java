package com.college.service;

import com.college.model.Enrollment;
import com.college.model.Student;
import com.college.model.Course;
import com.college.repository.EnrollmentRepository;
import com.college.repository.StudentRepository;
import com.college.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Get all enrollments
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    // Get enrollment by ID
    public Enrollment getEnrollmentById(Long id) {
        Optional<Enrollment> enrollment = enrollmentRepository.findById(id);
        return enrollment.orElse(null);
    }

    // Enroll student in course
    public Enrollment enrollStudent(Long studentId, Long courseId, String semester, String academicYear) {
        // Validate student and course exist
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        Optional<Course> courseOpt = courseRepository.findById(courseId);

        if (!studentOpt.isPresent() || !courseOpt.isPresent()) {
            throw new IllegalArgumentException("Student or Course not found");
        }

        Student student = studentOpt.get();
        Course course = courseOpt.get();

        // Check if already enrolled
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByStudentAndCourse(student, course);
        if (existingEnrollment.isPresent() && existingEnrollment.get().isActive()) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }

        // Check course capacity (if needed)
        Long currentEnrollments = enrollmentRepository.countActiveEnrollmentsByCourse(course);
        // You can add capacity logic here if needed

        // Create enrollment
        Enrollment enrollment = new Enrollment(student, course, "ENROLLED", null, null);
        enrollment.setEnrollmentDate(LocalDate.now());
        if (semester != null) {
            enrollment.setSemester(semester);
        }
        if (academicYear != null) {
            enrollment.setAcademicYear(academicYear);
        }
        enrollment.setCreditsEarned(0.0);

        return enrollmentRepository.save(enrollment);
    }

    // Update enrollment
    public Enrollment updateEnrollment(Long id, Enrollment enrollmentDetails) {
        Optional<Enrollment> optionalEnrollment = enrollmentRepository.findById(id);
        if (optionalEnrollment.isPresent()) {
            Enrollment enrollment = optionalEnrollment.get();
            enrollment.setStatus(enrollmentDetails.getStatus());
            enrollment.setGrade(enrollmentDetails.getGrade());
            enrollment.setGradeLetter(enrollmentDetails.getGradeLetter());
            enrollment.setCreditsEarned(enrollmentDetails.getCreditsEarned());
            enrollment.setCompletionDate(enrollmentDetails.getCompletionDate());
            enrollment.setRemarks(enrollmentDetails.getRemarks());
            enrollment.setSemester(enrollmentDetails.getSemester());
            enrollment.setAcademicYear(enrollmentDetails.getAcademicYear());

            // Auto-calculate grade letter if grade is provided
            if (enrollment.getGrade() != null && enrollment.getGradeLetter() == null) {
                enrollment.setGradeLetter(calculateGradeLetter(enrollment.getGrade()));
            }

            // Auto-calculate credits earned if completed
            if ("COMPLETED".equals(enrollment.getStatus()) && enrollment.getCreditsEarned() == null) {
                enrollment.setCreditsEarned(enrollment.getCourse().getCredits().doubleValue());
            }

            return enrollmentRepository.save(enrollment);
        }
        return null;
    }

    // Drop student from course
    public Enrollment dropStudent(Long enrollmentId, String remarks) {
        Optional<Enrollment> optionalEnrollment = enrollmentRepository.findById(enrollmentId);
        if (optionalEnrollment.isPresent()) {
            Enrollment enrollment = optionalEnrollment.get();
            enrollment.setStatus("DROPPED");
            enrollment.setRemarks(remarks);
            enrollment.setCreditsEarned(0.0);
            return enrollmentRepository.save(enrollment);
        }
        return null;
    }

    // Complete course
    public Enrollment completeCourse(Long enrollmentId, Integer grade, String remarks) {
        Optional<Enrollment> optionalEnrollment = enrollmentRepository.findById(enrollmentId);
        if (optionalEnrollment.isPresent()) {
            Enrollment enrollment = optionalEnrollment.get();
            enrollment.setStatus("COMPLETED");
            enrollment.setGrade(grade);
            enrollment.setGradeLetter(calculateGradeLetter(grade));
            enrollment.setCompletionDate(LocalDate.now());
            enrollment.setCreditsEarned(enrollment.getCourse().getCredits().doubleValue());
            enrollment.setRemarks(remarks);
            return enrollmentRepository.save(enrollment);
        }
        return null;
    }

    // Get student's enrollments
    public List<Enrollment> getStudentEnrollments(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(enrollmentRepository::findByStudent).orElse(null);
    }

    // Get student's active enrollments
    public List<Enrollment> getStudentActiveEnrollments(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(enrollmentRepository::findActiveEnrollmentsByStudent).orElse(null);
    }

    // Get student's completed courses
    public List<Enrollment> getStudentCompletedCourses(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(enrollmentRepository::findCompletedEnrollmentsByStudent).orElse(null);
    }

    // Get course enrollments
    public List<Enrollment> getCourseEnrollments(Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        return course.map(enrollmentRepository::findByCourse).orElse(null);
    }

    // Get course active enrollments
    public List<Enrollment> getCourseActiveEnrollments(Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        return course.map(c -> enrollmentRepository.findByCourseAndStatus(c, "ENROLLED")).orElse(null);
    }

    // Delete enrollment
    public boolean deleteEnrollment(Long id) {
        if (enrollmentRepository.existsById(id)) {
            enrollmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Calculate GPA for student
    public Double calculateStudentGPA(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(enrollmentRepository::calculateStudentGPA).orElse(0.0);
    }

    // Calculate credits earned for student
    public Double calculateCreditsEarned(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(enrollmentRepository::calculateCreditsEarned).orElse(0.0);
    }

    // Helper method to calculate grade letter
    private String calculateGradeLetter(Integer grade) {
        if (grade == null) return null;
        if (grade >= 90) return "A";
        if (grade >= 80) return "B";
        if (grade >= 70) return "C";
        if (grade >= 60) return "D";
        return "F";
    }

    // Get enrollment statistics
    public List<Object[]> getEnrollmentStats(Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        return course.map(enrollmentRepository::getEnrollmentStatsByCourse).orElse(null);
    }
}

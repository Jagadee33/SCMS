package com.college.service;

import com.college.model.Grade;
import com.college.model.Student;
import com.college.model.Course;
import com.college.model.Faculty;
import com.college.model.Examination;
import com.college.repository.GradeRepository;
import com.college.repository.StudentRepository;
import com.college.repository.CourseRepository;
import com.college.repository.FacultyRepository;
import com.college.repository.ExaminationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    // Grade CRUD operations
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id).orElse(null);
    }

    public Grade createGrade(Grade grade) {
        // Validate entities exist
        validateGradeEntities(grade);
        
        // Set default values
        if (grade.getStatus() == null) {
            grade.setStatus("DRAFT");
        }
        if (grade.getGradedDate() == null) {
            grade.setGradedDate(LocalDateTime.now());
        }

        return gradeRepository.save(grade);
    }

    public Grade updateGrade(Long id, Grade gradeDetails) {
        Grade grade = gradeRepository.findById(id).orElse(null);
        if (grade != null) {
            // Update fields
            grade.setGradeType(gradeDetails.getGradeType());
            grade.setGradeTitle(gradeDetails.getGradeTitle());
            grade.setMaxMarks(gradeDetails.getMaxMarks());
            grade.setObtainedMarks(gradeDetails.getObtainedMarks());
            grade.setWeightage(gradeDetails.getWeightage());
            grade.setStatus(gradeDetails.getStatus());
            grade.setFeedback(gradeDetails.getFeedback());
            grade.setRemarks(gradeDetails.getRemarks());
            grade.setSubmissionDate(gradeDetails.getSubmissionDate());
            
            if ("PUBLISHED".equals(gradeDetails.getStatus()) && !"PUBLISHED".equals(grade.getStatus())) {
                grade.setGradedDate(LocalDateTime.now());
            }

            return gradeRepository.save(grade);
        }
        return null;
    }

    public boolean deleteGrade(Long id) {
        if (gradeRepository.existsById(id)) {
            gradeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Student grade operations
    public List<Grade> getStudentGrades(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        return student != null ? gradeRepository.findByStudent(student) : null;
    }

    public List<Grade> getStudentGradesByCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (student != null && course != null) {
            return gradeRepository.findByStudentAndCourse(student, course);
        }
        return null;
    }

    public List<Grade> getPublishedStudentGrades(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        return student != null ? gradeRepository.findByStudentAndStatus(student, "PUBLISHED") : null;
    }

    public Double calculateStudentGPA(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        return student != null ? gradeRepository.calculateStudentGPA(student) : 0.0;
    }

    public Map<String, Object> getStudentGradeReport(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return null;

        List<Grade> grades = gradeRepository.findByStudentAndStatus(student, "PUBLISHED");
        
        Map<String, Object> report = new HashMap<>();
        report.put("student", student);
        report.put("totalGrades", grades.size());
        report.put("gpa", gradeRepository.calculateStudentGPA(student));
        
        // Group grades by course
        Map<String, List<Grade>> gradesByCourse = grades.stream()
            .collect(Collectors.groupingBy(g -> g.getCourse().getName()));
        report.put("gradesByCourse", gradesByCourse);
        
        // Calculate average percentage
        double avgPercentage = grades.stream()
            .mapToDouble(g -> g.getPercentage() != null ? g.getPercentage() : 0.0)
            .average()
            .orElse(0.0);
        report.put("averagePercentage", avgPercentage);
        
        // Grade distribution
        Map<String, Long> gradeDistribution = grades.stream()
            .collect(Collectors.groupingBy(Grade::getGradeLetter, Collectors.counting()));
        report.put("gradeDistribution", gradeDistribution);
        
        return report;
    }

    // Course grade operations
    public List<Grade> getCourseGrades(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        return course != null ? gradeRepository.findByCourse(course) : null;
    }

    public Map<String, Object> getCourseGradeStatistics(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return null;

        List<Grade> grades = gradeRepository.findByCourseAndStatus(course, "PUBLISHED");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("course", course);
        stats.put("totalGrades", grades.size());
        
        if (!grades.isEmpty()) {
            double avgPercentage = grades.stream()
                .mapToDouble(g -> g.getPercentage() != null ? g.getPercentage() : 0.0)
                .average()
                .orElse(0.0);
            stats.put("averagePercentage", avgPercentage);
            
            double highestGrade = grades.stream()
                .mapToDouble(g -> g.getPercentage() != null ? g.getPercentage() : 0.0)
                .max()
                .orElse(0.0);
            stats.put("highestGrade", highestGrade);
            
            double lowestGrade = grades.stream()
                .mapToDouble(g -> g.getPercentage() != null ? g.getPercentage() : 0.0)
                .min()
                .orElse(0.0);
            stats.put("lowestGrade", lowestGrade);
        }
        
        // Grade distribution
        List<Object[]> distribution = gradeRepository.getCourseGradeDistribution(course);
        Map<String, Long> gradeDistribution = new HashMap<>();
        for (Object[] row : distribution) {
            gradeDistribution.put((String) row[0], (Long) row[1]);
        }
        stats.put("gradeDistribution", gradeDistribution);
        
        return stats;
    }

    // Faculty grade operations
    public List<Grade> getFacultyGrades(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);
        return faculty != null ? gradeRepository.findByFaculty(faculty) : null;
    }

    public List<Grade> getFacultyPendingGrades(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);
        return faculty != null ? gradeRepository.findByFacultyAndStatus(faculty, "DRAFT") : null;
    }

    public Map<String, Object> getFacultyGradeStats(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);
        if (faculty == null) return null;

        List<Object[]> stats = gradeRepository.getFacultyGradeStats(faculty);
        
        Map<String, Object> result = new HashMap<>();
        if (!stats.isEmpty()) {
            Object[] row = stats.get(0);
            result.put("totalGrades", (Long) row[0]);
            result.put("averageObtainedMarks", (Double) row[1]);
            result.put("averagePercentage", (Double) row[2]);
        }
        
        return result;
    }

    // Batch grade operations
    public List<Grade> publishGrades(List<Long> gradeIds) {
        List<Grade> grades = gradeRepository.findAllById(gradeIds);
        grades.forEach(grade -> {
            grade.setStatus("PUBLISHED");
            grade.setGradedDate(LocalDateTime.now());
        });
        return gradeRepository.saveAll(grades);
    }

    public List<Grade> createBatchGrades(List<Grade> grades) {
        grades.forEach(this::validateGradeEntities);
        grades.forEach(grade -> {
            if (grade.getStatus() == null) {
                grade.setStatus("DRAFT");
            }
        });
        return gradeRepository.saveAll(grades);
    }

    // Grade type operations
    public List<Grade> getGradesByType(String gradeType) {
        return gradeRepository.findAll().stream()
            .filter(g -> gradeType.equals(g.getGradeType()))
            .collect(Collectors.toList());
    }

    // Helper methods
    private void validateGradeEntities(Grade grade) {
        if (grade.getStudent() == null || grade.getStudent().getId() == null) {
            throw new IllegalArgumentException("Student is required");
        }
        if (grade.getCourse() == null || grade.getCourse().getId() == null) {
            throw new IllegalArgumentException("Course is required");
        }
        if (grade.getFaculty() == null || grade.getFaculty().getId() == null) {
            throw new IllegalArgumentException("Faculty is required");
        }
        
        // Verify entities exist
        if (!studentRepository.existsById(grade.getStudent().getId())) {
            throw new IllegalArgumentException("Student not found");
        }
        if (!courseRepository.existsById(grade.getCourse().getId())) {
            throw new IllegalArgumentException("Course not found");
        }
        if (!facultyRepository.existsById(grade.getFaculty().getId())) {
            throw new IllegalArgumentException("Faculty not found");
        }
        
        // Validate examination if provided
        if (grade.getExamination() != null && grade.getExamination().getId() != null) {
            if (!examinationRepository.existsById(grade.getExamination().getId())) {
                throw new IllegalArgumentException("Examination not found");
            }
        }
    }

    // Grade calculation utilities
    public Double calculateCourseGPA(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (student != null && course != null) {
            return gradeRepository.calculateCourseGPA(student, course);
        }
        return 0.0;
    }

    public List<Grade> getRecentGrades(Long studentId, int limit) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            List<Grade> grades = gradeRepository.findRecentGradesByStudent(student);
            return grades.stream().limit(limit).collect(Collectors.toList());
        }
        return null;
    }
}

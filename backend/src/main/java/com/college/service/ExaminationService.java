package com.college.service;

import com.college.model.Examination;
import com.college.model.Course;
import com.college.model.Faculty;
import com.college.model.ExamRegistration;
import com.college.repository.ExaminationRepository;
import com.college.repository.CourseRepository;
import com.college.repository.FacultyRepository;
import com.college.repository.ExamRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class ExaminationService {

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private ExamRegistrationRepository examRegistrationRepository;

    public List<Examination> getAllExaminations() {
        return examinationRepository.findAll();
    }

    public Examination getExaminationById(Long id) {
        Optional<Examination> examination = examinationRepository.findById(id);
        return examination.orElse(null);
    }

    public Examination createExamination(Examination examination) {
        // Validate that course and faculty exist
        if (examination.getCourse() == null || examination.getCourse().getId() == null) {
            throw new RuntimeException("Course is required");
        }
        if (examination.getFaculty() == null || examination.getFaculty().getId() == null) {
            throw new RuntimeException("Faculty is required");
        }

        // Verify entities exist
        if (!courseRepository.existsById(examination.getCourse().getId())) {
            throw new RuntimeException("Course not found");
        }
        if (!facultyRepository.existsById(examination.getFaculty().getId())) {
            throw new RuntimeException("Faculty not found");
        }

        // Set default values
        if (examination.getStatus() == null) {
            examination.setStatus("Upcoming");
        }
        if (examination.getExamDate() == null) {
            examination.setExamDate(LocalDateTime.now());
        }
        if (examination.getCurrentParticipants() == null) {
            examination.setCurrentParticipants(0);
        }
        if (examination.getIsActive() == null) {
            examination.setIsActive(true);
        }
        if (examination.getRequiresApproval() == null) {
            examination.setRequiresApproval(false);
        }

        return examinationRepository.save(examination);
    }

    public Examination updateExamination(Long id, Examination examinationDetails) {
        Optional<Examination> optionalExamination = examinationRepository.findById(id);
        if (optionalExamination.isPresent()) {
            Examination examination = optionalExamination.get();
            
            // Update fields
            examination.setTitle(examinationDetails.getTitle());
            examination.setDescription(examinationDetails.getDescription());
            examination.setCourse(examinationDetails.getCourse());
            examination.setFaculty(examinationDetails.getFaculty());
            examination.setExamDate(examinationDetails.getExamDate());
            examination.setStartTime(examinationDetails.getStartTime());
            examination.setEndTime(examinationDetails.getEndTime());
            examination.setDuration(examinationDetails.getDuration());
            examination.setTotalMarks(examinationDetails.getTotalMarks());
            examination.setPassingMarks(examinationDetails.getPassingMarks());
            examination.setStatus(examinationDetails.getStatus());
            examination.setExamType(examinationDetails.getExamType());
            examination.setExamMode(examinationDetails.getExamMode());
            examination.setVenue(examinationDetails.getVenue());
            examination.setInstructions(examinationDetails.getInstructions());
            examination.setRegistrationDeadline(examinationDetails.getRegistrationDeadline());
            examination.setResultDate(examinationDetails.getResultDate());
            examination.setMaxParticipants(examinationDetails.getMaxParticipants());
            examination.setIsActive(examinationDetails.getIsActive());
            examination.setRequiresApproval(examinationDetails.getRequiresApproval());
            
            return examinationRepository.save(examination);
        }
        return null;
    }

    public boolean deleteExamination(Long id) {
        if (examinationRepository.existsById(id)) {
            // Check if there are any registrations for this examination
            List<ExamRegistration> registrations = examRegistrationRepository.findByExaminationId(id);
            if (!registrations.isEmpty()) {
                throw new RuntimeException("Cannot delete examination with existing registrations");
            }
            
            examinationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Examination> getExaminationsByCourse(Long courseId) {
        return examinationRepository.findByCourseId(courseId);
    }

    public List<Examination> getExaminationsByFaculty(Long facultyId) {
        return examinationRepository.findByFacultyId(facultyId);
    }

    public List<Examination> getExaminationsByStatus(String status) {
        return examinationRepository.findByStatus(status);
    }

    public List<Examination> getExaminationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return examinationRepository.findByExamDateBetween(startDate, endDate);
    }

    public List<Examination> getCourseExaminationsByDateRange(Long courseId, LocalDateTime startDate, LocalDateTime endDate) {
        return examinationRepository.findByCourseIdAndExamDateBetween(courseId, startDate, endDate);
    }

    // Enhanced methods for exam management
    public List<Examination> getAvailableExaminationsForStudent(Long studentId) {
        List<Examination> allExaminations = examinationRepository.findAll();
        return allExaminations.stream()
            .filter(exam -> exam.canRegister() && 
                    !examRegistrationRepository.existsByStudentIdAndExaminationId(studentId, exam.getId()))
            .collect(Collectors.toList());
    }

    public List<Examination> getUpcomingExaminations() {
        LocalDateTime now = LocalDateTime.now();
        return examinationRepository.findAll().stream()
            .filter(exam -> "Upcoming".equals(exam.getStatus()) && exam.getExamDate().isAfter(now))
            .collect(Collectors.toList());
    }

    public List<Examination> getOngoingExaminations() {
        LocalDateTime now = LocalDateTime.now();
        return examinationRepository.findAll().stream()
            .filter(exam -> "Ongoing".equals(exam.getStatus()) && 
                    exam.getStartTime().isBefore(now) && exam.getEndTime().isAfter(now))
            .collect(Collectors.toList());
    }

    public Map<String, Object> getExaminationStatistics(Long examinationId) {
        Examination examination = examinationRepository.findById(examinationId).orElse(null);
        if (examination == null) return null;

        List<ExamRegistration> registrations = examRegistrationRepository.findByExaminationId(examinationId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("examination", examination);
        stats.put("totalRegistrations", registrations.size());
        stats.put("currentParticipants", examination.getCurrentParticipants());
        stats.put("maxParticipants", examination.getMaxParticipants());
        stats.put("isFull", examination.isFull());
        stats.put("isRegistrationOpen", examination.isRegistrationOpen());
        stats.put("canRegister", examination.canRegister());
        
        // Registration status distribution
        Map<String, Long> statusDistribution = registrations.stream()
            .collect(Collectors.groupingBy(ExamRegistration::getStatus, Collectors.counting()));
        stats.put("registrationStatusDistribution", statusDistribution);
        
        // Attendance statistics
        long attendedCount = registrations.stream()
            .filter(ExamRegistration::hasAttended)
            .count();
        long absentCount = registrations.stream()
            .filter(ExamRegistration::wasAbsent)
            .count();
        long completedCount = registrations.stream()
            .filter(ExamRegistration::isCompleted)
            .count();
        
        stats.put("attendedCount", attendedCount);
        stats.put("absentCount", absentCount);
        stats.put("completedCount", completedCount);
        
        double attendanceRate = completedCount > 0 ? (double) attendedCount / completedCount * 100 : 0;
        stats.put("attendanceRate", attendanceRate);
        
        return stats;
    }

    public Map<String, Object> getFacultyExaminationStatistics(Long facultyId) {
        List<Examination> facultyExaminations = examinationRepository.findByFacultyId(facultyId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalExaminations", facultyExaminations.size());
        
        // Status distribution
        Map<String, Long> statusDistribution = facultyExaminations.stream()
            .collect(Collectors.groupingBy(Examination::getStatus, Collectors.counting()));
        stats.put("statusDistribution", statusDistribution);
        
        // Exam type distribution
        Map<String, Long> typeDistribution = facultyExaminations.stream()
            .collect(Collectors.groupingBy(Examination::getExamType, Collectors.counting()));
        stats.put("typeDistribution", typeDistribution);
        
        // Total registrations across all exams
        long totalRegistrations = facultyExaminations.stream()
            .mapToLong(exam -> examRegistrationRepository.countByExamination(exam))
            .sum();
        stats.put("totalRegistrations", totalRegistrations);
        
        // Upcoming exams
        long upcomingCount = facultyExaminations.stream()
            .filter(exam -> "Upcoming".equals(exam.getStatus()))
            .count();
        stats.put("upcomingExaminations", upcomingCount);
        
        return stats;
    }

    // Auto-update examination status based on dates
    public void updateExaminationStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Examination> examinations = examinationRepository.findAll();
        
        examinations.forEach(exam -> {
            if (exam.getStartTime() != null && exam.getEndTime() != null) {
                if (now.isBefore(exam.getStartTime()) && !"Upcoming".equals(exam.getStatus())) {
                    exam.setStatus("Upcoming");
                    examinationRepository.save(exam);
                } else if (now.isAfter(exam.getStartTime()) && now.isBefore(exam.getEndTime()) && !"Ongoing".equals(exam.getStatus())) {
                    exam.setStatus("Ongoing");
                    examinationRepository.save(exam);
                } else if (now.isAfter(exam.getEndTime()) && !"Completed".equals(exam.getStatus())) {
                    exam.setStatus("Completed");
                    examinationRepository.save(exam);
                }
            }
        });
    }

    // Get examination calendar for a specific period
    public List<Map<String, Object>> getExaminationCalendar(LocalDateTime startDate, LocalDateTime endDate) {
        List<Examination> examinations = examinationRepository.findByExamDateBetween(startDate, endDate);
        
        return examinations.stream()
            .map(exam -> {
                Map<String, Object> event = new HashMap<>();
                event.put("id", exam.getId());
                event.put("title", exam.getTitle());
                event.put("start", exam.getStartTime());
                event.put("end", exam.getEndTime());
                event.put("course", exam.getCourse().getName());
                event.put("faculty", exam.getFaculty().getFirstName() + " " + exam.getFaculty().getLastName());
                event.put("status", exam.getStatus());
                event.put("type", exam.getExamType());
                event.put("venue", exam.getVenue());
                return event;
            })
            .collect(Collectors.toList());
    }
}

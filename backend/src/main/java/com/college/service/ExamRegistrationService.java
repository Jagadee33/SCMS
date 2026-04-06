package com.college.service;

import com.college.model.ExamRegistration;
import com.college.model.Student;
import com.college.model.Examination;
import com.college.model.Grade;
import com.college.repository.ExamRegistrationRepository;
import com.college.repository.StudentRepository;
import com.college.repository.ExaminationRepository;
import com.college.repository.GradeRepository;
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
public class ExamRegistrationService {

    @Autowired
    private ExamRegistrationRepository examRegistrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private GradeRepository gradeRepository;

    // Exam Registration CRUD operations
    public List<ExamRegistration> getAllExamRegistrations() {
        return examRegistrationRepository.findAll();
    }

    public ExamRegistration getExamRegistrationById(Long id) {
        return examRegistrationRepository.findById(id).orElse(null);
    }

    public ExamRegistration registerStudentForExam(Long studentId, Long examinationId, String specialRequirements) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        Examination examination = examinationRepository.findById(examinationId)
            .orElseThrow(() -> new RuntimeException("Examination not found"));

        // Check if student is already registered
        if (examRegistrationRepository.existsByStudentAndExamination(student, examination)) {
            throw new RuntimeException("Student is already registered for this examination");
        }

        // Check if registration is open
        if (!examination.canRegister()) {
            throw new RuntimeException("Registration is not open for this examination");
        }

        // Check if exam is full
        if (examination.isFull()) {
            throw new RuntimeException("Examination is full");
        }

        ExamRegistration registration = new ExamRegistration(
                student, 
                examination, 
                LocalDateTime.now(),
                false, 
                specialRequirements,
                examination.getRequiresApproval() ? "REGISTERED" : "APPROVED"
        );

        // Update current participants count
        examination.setCurrentParticipants(examination.getCurrentParticipants() + 1);
        examinationRepository.save(examination);

        return examRegistrationRepository.save(registration);
    }

    public ExamRegistration approveRegistration(Long registrationId, String approvalNotes) {
        ExamRegistration registration = examRegistrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!"REGISTERED".equals(registration.getStatus())) {
            throw new RuntimeException("Registration is not in pending status");
        }

        registration.setStatus("APPROVED");
        registration.setApprovalDate(LocalDateTime.now());
        registration.setApprovalNotes(approvalNotes);

        return examRegistrationRepository.save(registration);
    }

    public ExamRegistration rejectRegistration(Long registrationId, String approvalNotes) {
        ExamRegistration registration = examRegistrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!"REGISTERED".equals(registration.getStatus())) {
            throw new RuntimeException("Registration is not in pending status");
        }

        registration.setStatus("REJECTED");
        registration.setApprovalDate(LocalDateTime.now());
        registration.setApprovalNotes(approvalNotes);

        // Update current participants count
        Examination examination = registration.getExamination();
        examination.setCurrentParticipants(Math.max(0, examination.getCurrentParticipants() - 1));
        examinationRepository.save(examination);

        return examRegistrationRepository.save(registration);
    }

    public ExamRegistration markAttendance(Long registrationId, Boolean attended) {
        ExamRegistration registration = examRegistrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!"APPROVED".equals(registration.getStatus())) {
            throw new RuntimeException("Cannot mark attendance for unapproved registration");
        }

        registration.setExamAttendance(attended);
        registration.setStatus(attended ? "COMPLETED" : "ABSENT");
        registration.setCompletionDate(LocalDateTime.now());

        return examRegistrationRepository.save(registration);
    }

    public boolean deleteRegistration(Long id) {
        if (examRegistrationRepository.existsById(id)) {
            ExamRegistration registration = examRegistrationRepository.findById(id).get();
            
            // Update current participants count
            Examination examination = registration.getExamination();
            examination.setCurrentParticipants(Math.max(0, examination.getCurrentParticipants() - 1));
            examinationRepository.save(examination);

            examRegistrationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Student exam operations
    public List<ExamRegistration> getStudentExamRegistrations(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        return student != null ? examRegistrationRepository.findByStudent(student) : null;
    }

    public List<ExamRegistration> getUpcomingExamsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            return examRegistrationRepository.findUpcomingExamsForStudent(student, LocalDateTime.now());
        }
        return null;
    }

    public List<ExamRegistration> getCompletedExamsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            return examRegistrationRepository.findCompletedExamsForStudent(student, LocalDateTime.now());
        }
        return null;
    }

    public Map<String, Object> getStudentExamReport(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return null;

        List<ExamRegistration> allRegistrations = examRegistrationRepository.findByStudent(student);
        List<ExamRegistration> completedRegistrations = examRegistrationRepository.findCompletedExamsForStudent(student, LocalDateTime.now());
        List<ExamRegistration> upcomingRegistrations = examRegistrationRepository.findUpcomingExamsForStudent(student, LocalDateTime.now());

        Map<String, Object> report = new HashMap<>();
        report.put("student", student);
        report.put("totalRegistrations", allRegistrations.size());
        report.put("completedExams", completedRegistrations.size());
        report.put("upcomingExams", upcomingRegistrations.size());
        
        // Calculate attendance rate
        long attendedCount = completedRegistrations.stream()
            .mapToLong(reg -> reg.hasAttended() ? 1 : 0)
            .sum();
        double attendanceRate = completedRegistrations.size() > 0 ? 
            (double) attendedCount / completedRegistrations.size() * 100 : 0;
        report.put("attendanceRate", attendanceRate);

        // Get grades for completed exams
        List<Grade> grades = gradeRepository.findByStudent(student);
        Map<String, Object> gradeStats = new HashMap<>();
        gradeStats.put("totalGrades", grades.size());
        
        if (!grades.isEmpty()) {
            double avgPercentage = grades.stream()
                .filter(g -> g.getPercentage() != null)
                .mapToDouble(Grade::getPercentage)
                .average()
                .orElse(0.0);
            gradeStats.put("averagePercentage", avgPercentage);
        }
        
        report.put("gradeStats", gradeStats);

        return report;
    }

    // Examination operations
    public List<ExamRegistration> getExaminationRegistrations(Long examinationId) {
        Examination examination = examinationRepository.findById(examinationId).orElse(null);
        return examination != null ? examRegistrationRepository.findByExamination(examination) : null;
    }

    public Map<String, Object> getExaminationStatistics(Long examinationId) {
        Examination examination = examinationRepository.findById(examinationId).orElse(null);
        if (examination == null) return null;

        List<Object[]> statusStats = examRegistrationRepository.getExamStatisticsByExamination(examination);
        Map<String, Long> statusDistribution = new HashMap<>();
        for (Object[] row : statusStats) {
            statusDistribution.put((String) row[0], (Long) row[1]);
        }

        List<Object[]> attendanceStats = examRegistrationRepository.getAttendanceStatisticsByExamination(examination);
        Map<String, Object> attendanceData = new HashMap<>();
        if (!attendanceStats.isEmpty()) {
            Object[] row = attendanceStats.get(0);
            attendanceData.put("totalCompleted", (Long) row[0]);
            attendanceData.put("attended", (Long) row[1]);
            attendanceData.put("absent", (Long) row[2]);
        }

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("examination", examination);
        statistics.put("statusDistribution", statusDistribution);
        statistics.put("attendanceData", attendanceData);
        statistics.put("totalRegistrations", examRegistrationRepository.countByExamination(examination));

        return statistics;
    }

    // Faculty operations
    public List<ExamRegistration> getPendingApprovalsForFaculty(Long facultyId) {
        List<Examination> facultyExaminations = examinationRepository.findByFacultyId(facultyId);
        return facultyExaminations.stream()
            .flatMap(exam -> examRegistrationRepository.findByExaminationAndStatus(exam, "REGISTERED").stream())
            .collect(Collectors.toList());
    }

    // Batch operations
    public List<ExamRegistration> approveMultipleRegistrations(List<Long> registrationIds, String approvalNotes) {
        List<ExamRegistration> registrations = examRegistrationRepository.findAllById(registrationIds);
        registrations.forEach(reg -> {
            if ("REGISTERED".equals(reg.getStatus())) {
                reg.setStatus("APPROVED");
                reg.setApprovalDate(LocalDateTime.now());
                reg.setApprovalNotes(approvalNotes);
            }
        });
        return examRegistrationRepository.saveAll(registrations);
    }

    public List<ExamRegistration> markMultipleAttendances(Map<Long, Boolean> attendanceData) {
        List<ExamRegistration> registrations = examRegistrationRepository.findAllById(attendanceData.keySet());
        registrations.forEach(reg -> {
            Boolean attended = attendanceData.get(reg.getId());
            if (attended != null && "APPROVED".equals(reg.getStatus())) {
                reg.setExamAttendance(attended);
                reg.setStatus(attended ? "COMPLETED" : "ABSENT");
                reg.setCompletionDate(LocalDateTime.now());
            }
        });
        return examRegistrationRepository.saveAll(registrations);
    }

    // Utility methods
    public boolean isStudentRegistered(Long studentId, Long examinationId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Examination examination = examinationRepository.findById(examinationId).orElse(null);
        if (student != null && examination != null) {
            return examRegistrationRepository.existsByStudentAndExamination(student, examination);
        }
        return false;
    }

    public List<Student> getStudentsWhoAttended(Long examinationId) {
        Examination examination = examinationRepository.findById(examinationId).orElse(null);
        return examination != null ? examRegistrationRepository.findStudentsWhoAttended(examination) : null;
    }

    public List<Student> getStudentsWhoWereAbsent(Long examinationId) {
        Examination examination = examinationRepository.findById(examinationId).orElse(null);
        return examination != null ? examRegistrationRepository.findStudentsWhoWereAbsent(examination) : null;
    }
}

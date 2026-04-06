package com.college.service;

import com.college.model.Fee;
import com.college.model.Student;
import com.college.repository.FeeRepository;
import com.college.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeeService {

    @Autowired
    private FeeRepository feeRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public Fee getFeeById(Long id) {
        Optional<Fee> fee = feeRepository.findById(id);
        return fee.orElse(null);
    }

    public Fee createFee(Fee fee) {
        // Validate that student exists
        if (fee.getStudent() == null || fee.getStudent().getId() == null) {
            throw new RuntimeException("Student is required");
        }

        // Set default values
        if (fee.getStatus() == null) {
            fee.setStatus("Pending");
        }
        if (fee.getPaidAmount() == null) {
            fee.setPaidAmount(0.0);
        }
        if (fee.getDueDate() == null) {
            fee.setDueDate(LocalDateTime.now().plusMonths(1));
        }

        return feeRepository.save(fee);
    }

    public Fee updateFee(Long id, Fee feeDetails) {
        Optional<Fee> optionalFee = feeRepository.findById(id);
        if (optionalFee.isPresent()) {
            Fee fee = optionalFee.get();
            fee.setStudent(feeDetails.getStudent());
            fee.setFeeType(feeDetails.getFeeType());
            fee.setDescription(feeDetails.getDescription());
            fee.setAmount(feeDetails.getAmount());
            fee.setPaidAmount(feeDetails.getPaidAmount());
            fee.setDueDate(feeDetails.getDueDate());
            fee.setPaymentDate(feeDetails.getPaymentDate());
            fee.setPaymentMethod(feeDetails.getPaymentMethod());
            fee.setTransactionId(feeDetails.getTransactionId());
            fee.setReceiptNumber(feeDetails.getReceiptNumber());
            fee.setStatus(feeDetails.getStatus());
            fee.setAcademicYear(feeDetails.getAcademicYear());
            fee.setSemester(feeDetails.getSemester());
            fee.setLateFee(feeDetails.getLateFee());
            fee.setDiscountAmount(feeDetails.getDiscountAmount());
            fee.setDiscountReason(feeDetails.getDiscountReason());
            return feeRepository.save(fee);
        }
        return null;
    }

    public boolean deleteFee(Long id) {
        if (feeRepository.existsById(id)) {
            feeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Fee> getFeesByStudent(Long studentId) {
        return feeRepository.findByStudentId(studentId);
    }

    public List<Fee> getFeesByStudentAndStatus(Long studentId, String status) {
        return feeRepository.findByStudentIdAndStatus(studentId, status);
    }

    public List<Fee> getFeesByStatus(String status) {
        return feeRepository.findByStatus(status);
    }

    public List<Fee> getFeesByFeeType(String feeType) {
        return feeRepository.findByFeeType(feeType);
    }

    public List<Fee> getOverdueFees() {
        return feeRepository.findByDueDateBefore(LocalDateTime.now());
    }

    public List<Fee> getFeesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return feeRepository.findByDueDateBetween(startDate, endDate);
    }

    public List<Fee> getFeesByAcademicYear(String academicYear) {
        return feeRepository.findByAcademicYear(academicYear);
    }

    public List<Fee> getFeesByAcademicYearAndSemester(String academicYear, String semester) {
        return feeRepository.findByAcademicYearAndSemester(academicYear, semester);
    }

    public List<Fee> getStudentFeesByAcademicYear(Long studentId, String academicYear) {
        return feeRepository.findByStudentIdAndAcademicYear(studentId, academicYear);
    }

    public Fee processPayment(Long feeId, Double paymentAmount, String paymentMethod, String transactionId) {
        Optional<Fee> optionalFee = feeRepository.findById(feeId);
        if (optionalFee.isPresent()) {
            Fee fee = optionalFee.get();
            
            // Update payment details
            fee.setPaidAmount((fee.getPaidAmount() != null ? fee.getPaidAmount() : 0.0) + paymentAmount);
            fee.setPaymentDate(LocalDateTime.now());
            fee.setPaymentMethod(paymentMethod);
            fee.setTransactionId(transactionId);
            
            // Generate receipt number
            if (fee.getReceiptNumber() == null) {
                fee.setReceiptNumber("RCP" + System.currentTimeMillis());
            }
            
            // Update status
            if (fee.getRemainingAmount() <= 0) {
                fee.setStatus("Paid");
            } else {
                fee.setStatus("Partial");
            }
            
            return feeRepository.save(fee);
        }
        return null;
    }

    public Fee applyDiscount(Long feeId, Double discountAmount, String discountReason) {
        Optional<Fee> optionalFee = feeRepository.findById(feeId);
        if (optionalFee.isPresent()) {
            Fee fee = optionalFee.get();
            fee.setDiscountAmount(discountAmount);
            fee.setDiscountReason(discountReason);
            return feeRepository.save(fee);
        }
        return null;
    }

    public Double getTotalFeesByStudent(Long studentId) {
        List<Fee> fees = feeRepository.findByStudentId(studentId);
        return fees.stream().mapToDouble(Fee::getAmount).sum();
    }

    public Double getTotalPaidByStudent(Long studentId) {
        List<Fee> fees = feeRepository.findByStudentId(studentId);
        return fees.stream().mapToDouble(fee -> fee.getPaidAmount() != null ? fee.getPaidAmount() : 0.0).sum();
    }

    public Double getTotalRemainingByStudent(Long studentId) {
        List<Fee> fees = feeRepository.findByStudentId(studentId);
        return fees.stream().mapToDouble(Fee::getRemainingAmount).sum();
    }

    public List<Fee> getOverdueFeesByStudent(Long studentId) {
        List<Fee> studentFees = feeRepository.findByStudentId(studentId);
        return studentFees.stream().filter(Fee::isOverdue).collect(Collectors.toList());
    }
}

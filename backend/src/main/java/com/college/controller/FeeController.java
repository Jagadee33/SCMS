package com.college.controller;

import com.college.model.Fee;
import com.college.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/fees")
@CrossOrigin(origins = "http://localhost:3000")
public class FeeController {

    @Autowired
    private FeeService feeService;

    @GetMapping
    public ResponseEntity<List<Fee>> getAllFees() {
        List<Fee> fees = feeService.getAllFees();
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fee> getFeeById(@PathVariable Long id) {
        Fee fee = feeService.getFeeById(id);
        if (fee != null) {
            return ResponseEntity.ok(fee);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Fee> createFee(@RequestBody Fee fee) {
        try {
            Fee createdFee = feeService.createFee(fee);
            return ResponseEntity.ok(createdFee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fee> updateFee(
            @PathVariable Long id, 
            @RequestBody Fee fee) {
        Fee updatedFee = feeService.updateFee(id, fee);
        if (updatedFee != null) {
            return ResponseEntity.ok(updatedFee);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFee(@PathVariable Long id) {
        boolean deleted = feeService.deleteFee(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Fee>> getFeesByStudent(@PathVariable Long studentId) {
        List<Fee> fees = feeService.getFeesByStudent(studentId);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/student/{studentId}/status/{status}")
    public ResponseEntity<List<Fee>> getFeesByStudentAndStatus(
            @PathVariable Long studentId, 
            @PathVariable String status) {
        List<Fee> fees = feeService.getFeesByStudentAndStatus(studentId, status);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Fee>> getFeesByStatus(@PathVariable String status) {
        List<Fee> fees = feeService.getFeesByStatus(status);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/type/{feeType}")
    public ResponseEntity<List<Fee>> getFeesByFeeType(@PathVariable String feeType) {
        List<Fee> fees = feeService.getFeesByFeeType(feeType);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Fee>> getOverdueFees() {
        List<Fee> fees = feeService.getOverdueFees();
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/student/{studentId}/overdue")
    public ResponseEntity<List<Fee>> getOverdueFeesByStudent(@PathVariable Long studentId) {
        List<Fee> fees = feeService.getOverdueFeesByStudent(studentId);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Fee>> getFeesByDateRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();
        
        List<Fee> fees = feeService.getFeesByDateRange(start, end);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/academic-year/{academicYear}")
    public ResponseEntity<List<Fee>> getFeesByAcademicYear(@PathVariable String academicYear) {
        List<Fee> fees = feeService.getFeesByAcademicYear(academicYear);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/academic-year/{academicYear}/semester/{semester}")
    public ResponseEntity<List<Fee>> getFeesByAcademicYearAndSemester(
            @PathVariable String academicYear,
            @PathVariable String semester) {
        List<Fee> fees = feeService.getFeesByAcademicYearAndSemester(academicYear, semester);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/student/{studentId}/academic-year/{academicYear}")
    public ResponseEntity<List<Fee>> getStudentFeesByAcademicYear(
            @PathVariable Long studentId,
            @PathVariable String academicYear) {
        List<Fee> fees = feeService.getStudentFeesByAcademicYear(studentId, academicYear);
        return ResponseEntity.ok(fees);
    }

    @PostMapping("/{feeId}/payment")
    public ResponseEntity<Fee> processPayment(
            @PathVariable Long feeId,
            @RequestBody Map<String, Object> paymentData) {
        
        try {
            Double paymentAmount = ((Number) paymentData.get("paymentAmount")).doubleValue();
            String paymentMethod = (String) paymentData.get("paymentMethod");
            String transactionId = (String) paymentData.get("transactionId");
            
            Fee updatedFee = feeService.processPayment(feeId, paymentAmount, paymentMethod, transactionId);
            if (updatedFee != null) {
                return ResponseEntity.ok(updatedFee);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{feeId}/discount")
    public ResponseEntity<Fee> applyDiscount(
            @PathVariable Long feeId,
            @RequestBody Map<String, Object> discountData) {
        
        try {
            Double discountAmount = ((Number) discountData.get("discountAmount")).doubleValue();
            String discountReason = (String) discountData.get("discountReason");
            
            Fee updatedFee = feeService.applyDiscount(feeId, discountAmount, discountReason);
            if (updatedFee != null) {
                return ResponseEntity.ok(updatedFee);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<Map<String, Object>> getStudentFeeSummary(@PathVariable Long studentId) {
        Double totalFees = feeService.getTotalFeesByStudent(studentId);
        Double totalPaid = feeService.getTotalPaidByStudent(studentId);
        Double totalRemaining = feeService.getTotalRemainingByStudent(studentId);
        List<Fee> overdueFees = feeService.getOverdueFeesByStudent(studentId);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalFees", totalFees);
        summary.put("totalPaid", totalPaid);
        summary.put("totalRemaining", totalRemaining);
        summary.put("overdueCount", overdueFees.size());
        summary.put("overdueAmount", overdueFees.stream().mapToDouble(Fee::getRemainingAmount).sum());
        
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/{feeId}/gateway-payment")
    public ResponseEntity<?> processGatewayPayment(
            @PathVariable Long feeId,
            @RequestBody Map<String, Object> paymentData) {
        
        try {
            Double amount = ((Number) paymentData.get("amount")).doubleValue();
            String paymentMethod = (String) paymentData.get("paymentMethod");
            String gatewayTransactionId = (String) paymentData.get("gatewayTransactionId");

            // Process payment using existing fee service
            Fee updatedFee = feeService.processPayment(feeId, amount, paymentMethod, gatewayTransactionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fee", updatedFee);
            response.put("message", "Payment processed successfully");
            response.put("receiptNumber", updatedFee.getReceiptNumber());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getFeeStats() {
        List<Fee> allFees = feeService.getAllFees();
        
        long total = allFees.size();
        long paid = allFees.stream().filter(Fee::isPaid).count();
        long pending = allFees.stream().filter(fee -> "Pending".equals(fee.getStatus())).count();
        long partial = allFees.stream().filter(fee -> "Partial".equals(fee.getStatus())).count();
        long overdue = allFees.stream().filter(Fee::isOverdue).count();
        
        Double totalAmount = allFees.stream().mapToDouble(Fee::getAmount).sum();
        Double totalPaidAmount = allFees.stream().mapToDouble(fee -> fee.getPaidAmount() != null ? fee.getPaidAmount() : 0.0).sum();
        Double totalRemainingAmount = allFees.stream().mapToDouble(Fee::getRemainingAmount).sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("paid", paid);
        stats.put("pending", pending);
        stats.put("partial", partial);
        stats.put("overdue", overdue);
        stats.put("totalAmount", totalAmount);
        stats.put("totalPaidAmount", totalPaidAmount);
        stats.put("totalRemainingAmount", totalRemainingAmount);
        stats.put("collectionRate", totalAmount > 0 ? (totalPaidAmount / totalAmount) * 100 : 0);
        
        return ResponseEntity.ok(stats);
    }
}

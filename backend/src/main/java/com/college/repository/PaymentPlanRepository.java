package com.college.repository;

import com.college.model.PaymentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, Long> {
    
    // Find active payment plans
    List<PaymentPlan> findByIsActive(Boolean isActive);
    
    // Find plan by name
    PaymentPlan findByPlanName(String planName);
    
    // Find plans by type
    List<PaymentPlan> findByPlanType(String planType);
    
    // Check if plan exists
    boolean existsByPlanName(String planName);
}

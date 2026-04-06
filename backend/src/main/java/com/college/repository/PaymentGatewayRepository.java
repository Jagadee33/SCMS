package com.college.repository;

import com.college.model.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, Long> {
    
    // Find active payment gateways
    List<PaymentGateway> findByIsActive(Boolean isActive);
    
    // Find gateway by name
    PaymentGateway findByGatewayName(String gatewayName);
    
    // Find gateway by provider
    List<PaymentGateway> findByProviderName(String providerName);
    
    // Check if gateway exists
    boolean existsByGatewayName(String gatewayName);
}

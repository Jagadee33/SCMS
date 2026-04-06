package com.college.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_gateways")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PaymentGateway {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "gateway_name", nullable = false, unique = true)
    private String gatewayName;
    
    @Column(name = "provider_name", nullable = false)
    private String providerName;
    
    @Column(name = "api_key", nullable = false)
    private String apiKey;
    
    @Column(name = "api_secret", nullable = false)
    private String apiSecret;
    
    @Column(name = "merchant_id", nullable = true)
    private String merchantId;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "supports_refund", nullable = false)
    private Boolean supportsRefund;
    
    @Column(name = "supported_methods", columnDefinition = "JSON")
    private String supportedMethods;
    
    @Column(name = "webhook_url", nullable = true)
    private String webhookUrl;
    
    @Column(name = "success_url", nullable = true)
    private String successUrl;
    
    @Column(name = "failure_url", nullable = true)
    private String failureUrl;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

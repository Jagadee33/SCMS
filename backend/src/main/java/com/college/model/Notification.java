package com.college.model;

import java.time.LocalDateTime;
import java.util.Map;

public class Notification {
    private Long id;
    private String type; // student, faculty, admin, parent, system
    private String recipientType; // STUDENT, FACULTY, ADMIN, PARENT
    private Long recipientId;
    private String message;
    private String priority; // high, medium, low
    private String status; // PENDING, SENT, DELIVERED, READ, EXPIRED
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime expiresAt;
    private String deliveryChannel; // email, sms, push_notification, dashboard, banner
    private Boolean read;
    private Map<String, Object> metadata; // Additional data specific to notification type
    
    // Constructors
    public Notification() {}
    
    public Notification(Long id, String type, String recipientType, Long recipientId, String message, 
                     String priority, String status, LocalDateTime createdAt, LocalDateTime deliveredAt, 
                     LocalDateTime expiresAt, String deliveryChannel, Boolean read, Map<String, Object> metadata) {
        this.id = id;
        this.type = type;
        this.recipientType = recipientType;
        this.recipientId = recipientId;
        this.message = message;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.deliveredAt = deliveredAt;
        this.expiresAt = expiresAt;
        this.deliveryChannel = deliveryChannel;
        this.read = read;
        this.metadata = metadata;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getRecipientType() { return recipientType; }
    public void setRecipientType(String recipientType) { this.recipientType = recipientType; }
    
    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getDeliveryChannel() { return deliveryChannel; }
    public void setDeliveryChannel(String deliveryChannel) { this.deliveryChannel = deliveryChannel; }
    
    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}

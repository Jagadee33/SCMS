package com.college.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}

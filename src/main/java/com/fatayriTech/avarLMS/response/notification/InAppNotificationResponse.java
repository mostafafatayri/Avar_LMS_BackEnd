package com.fatayriTech.avarLMS.response.notification;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InAppNotificationResponse {
    private Long id;
    private Long organizationId;
    private Long userId;
    private Long employeeId;

    private String title;
    private String message;
    private String type;
    private Boolean read;
    private LocalDateTime readAt;
    private String actionUrl;
    private LocalDateTime creationDate;
}
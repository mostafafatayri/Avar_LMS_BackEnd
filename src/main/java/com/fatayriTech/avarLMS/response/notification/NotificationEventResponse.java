package com.fatayriTech.avarLMS.response.notification;

import com.fatayriTech.avarLMS.enums.NotificationChannel;
import com.fatayriTech.avarLMS.enums.NotificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationEventResponse {

    private Long id;
    private Long organizationId;

    private Long ruleId;
    private String ruleName;
    private String ruleCode;

    private String targetType;
    private Long targetId;

    private Long recipientUserId;
    private Long recipientEmployeeId;
    private String recipientEmail;

    private String eventKey;

    private NotificationChannel channel;
    private NotificationStatus status;

    private LocalDateTime sentAt;
    private String errorMessage;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
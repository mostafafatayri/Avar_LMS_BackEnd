package com.fatayriTech.avarLMS.response.notification;

import com.fatayriTech.avarLMS.enums.NotificationEventType;
import com.fatayriTech.avarLMS.enums.NotificationModule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationRuleResponse {

    private Long id;
    private Long organizationId;

    private String code;
    private String name;

    private NotificationModule module;
    private NotificationEventType eventType;

    private Boolean channelEmail;
    private Boolean channelInApp;

    private Integer daysBefore;
    private Integer daysAfter;

    private String cronExpression;

    private Boolean active;

    private String subjectTemplate;
    private String bodyTemplate;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
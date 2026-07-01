package com.fatayriTech.avarLMS.request.notification;

import com.fatayriTech.avarLMS.enums.NotificationEventType;
import com.fatayriTech.avarLMS.enums.NotificationModule;
import com.fatayriTech.avarLMS.enums.NotificationRecipientType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRuleRequest {

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
    private NotificationRecipientType recipientType;
    private Long recipientTargetId;
    private String recipientTargetCode;
}
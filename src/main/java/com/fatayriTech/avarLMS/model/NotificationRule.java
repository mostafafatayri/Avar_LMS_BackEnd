package com.fatayriTech.avarLMS.model;
import com.fatayriTech.avarLMS.enums.NotificationRecipientType;
import com.fatayriTech.avarLMS.enums.NotificationEventType;
import com.fatayriTech.avarLMS.enums.NotificationModule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long organizationId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationModule module;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationEventType eventType;

    private Boolean channelEmail;
    private Boolean channelInApp;

    private Integer daysBefore;
    private Integer daysAfter;

    private String cronExpression;

    private Boolean active;

    @Column(columnDefinition = "TEXT")
    private String subjectTemplate;

    @Column(columnDefinition = "TEXT")
    private String bodyTemplate;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    @Enumerated(EnumType.STRING)
    private NotificationRecipientType recipientType;

    private Long recipientTargetId;

    private String recipientTargetCode;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (active == null) active = true;
        if (channelEmail == null) channelEmail = true;
        if (channelInApp == null) channelInApp = true;
        if (recipientType == null) recipientType = NotificationRecipientType.EMPLOYEE;
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
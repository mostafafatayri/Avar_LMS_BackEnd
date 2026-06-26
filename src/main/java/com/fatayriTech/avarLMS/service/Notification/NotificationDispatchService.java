package com.fatayriTech.avarLMS.service.Notification;

import com.fatayriTech.avarLMS.enums.NotificationChannel;
import com.fatayriTech.avarLMS.enums.NotificationStatus;
import com.fatayriTech.avarLMS.model.InAppNotification;
import com.fatayriTech.avarLMS.model.NotificationEvent;
import com.fatayriTech.avarLMS.model.NotificationRule;
import com.fatayriTech.avarLMS.repository.NotificationRepos.InAppNotificationRepo;
import com.fatayriTech.avarLMS.repository.NotificationRepos.NotificationEventRepo;
import com.fatayriTech.avarLMS.service.EmailService.EmailQueueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationDispatchService {

    private final NotificationEventRepo notificationEventRepo;
    private final InAppNotificationRepo inAppNotificationRepo;
    private final EmailQueueService emailQueueService;

    @Transactional
    public void createEmailEventIfNotExists(
            NotificationRule rule,
            String eventKey,
            String targetType,
            Long targetId,
            Long recipientUserId,
            Long recipientEmployeeId,
            String recipientEmail,
            String subject,
            String body
    ) {
        if (recipientEmail == null || recipientEmail.isBlank()) return;

        if (notificationEventRepo.existsByEventKeyAndChannel(eventKey, NotificationChannel.EMAIL)) {
            return;
        }

        NotificationEvent event = NotificationEvent.builder()
                .organizationId(rule.getOrganizationId())
                .rule(rule)
                .targetType(targetType)
                .targetId(targetId)
                .recipientUserId(recipientUserId)
                .recipientEmployeeId(recipientEmployeeId)
                .recipientEmail(recipientEmail)
                .eventKey(eventKey)
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .build();

        try {
            notificationEventRepo.save(event);

            emailQueueService.queueEmail(
                    recipientEmail,
                    safeSubject(subject),
                    safeBody(body)
            );

            event.setStatus(NotificationStatus.SENT);
            event.setSentAt(LocalDateTime.now());
            event.setErrorMessage(null);
        } catch (Exception e) {
            event.setStatus(NotificationStatus.FAILED);
            event.setErrorMessage(e.getMessage());
        }

        notificationEventRepo.save(event);
    }

    @Transactional
    public void createInAppEventIfNotExists(
            NotificationRule rule,
            String eventKey,
            String targetType,
            Long targetId,
            Long recipientUserId,
            Long recipientEmployeeId,
            String title,
            String message,
            String actionUrl
    ) {
        if (recipientUserId == null && recipientEmployeeId == null) return;

        if (notificationEventRepo.existsByEventKeyAndChannel(eventKey, NotificationChannel.IN_APP)) {
            return;
        }

        NotificationEvent event = NotificationEvent.builder()
                .organizationId(rule.getOrganizationId())
                .rule(rule)
                .targetType(targetType)
                .targetId(targetId)
                .recipientUserId(recipientUserId)
                .recipientEmployeeId(recipientEmployeeId)
                .eventKey(eventKey)
                .channel(NotificationChannel.IN_APP)
                .status(NotificationStatus.PENDING)
                .build();

        try {
            notificationEventRepo.save(event);

            inAppNotificationRepo.save(
                    InAppNotification.builder()
                            .organizationId(rule.getOrganizationId())
                            .userId(recipientUserId)
                            .employeeId(recipientEmployeeId)
                            .title(safeSubject(title))
                            .message(safeBody(message))
                            .type(rule.getEventType().name())
                            .actionUrl(actionUrl)
                            .read(false)
                            .build()
            );

            event.setStatus(NotificationStatus.SENT);
            event.setSentAt(LocalDateTime.now());
            event.setErrorMessage(null);
        } catch (Exception e) {
            event.setStatus(NotificationStatus.FAILED);
            event.setErrorMessage(e.getMessage());
        }

        notificationEventRepo.save(event);
    }

    private String safeSubject(String value) {
        return value == null || value.isBlank()
                ? "AVAR LMS Notification"
                : value;
    }

    private String safeBody(String value) {
        return value == null || value.isBlank()
                ? "You have a new notification in AVAR LMS."
                : value;
    }
}
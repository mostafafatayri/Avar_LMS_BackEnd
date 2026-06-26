package com.fatayriTech.avarLMS.service.Notification;
//
import com.fatayriTech.avarLMS.enums.NotificationChannel;
import com.fatayriTech.avarLMS.enums.NotificationStatus;
import com.fatayriTech.avarLMS.model.NotificationEvent;
import com.fatayriTech.avarLMS.model.NotificationRule;
import com.fatayriTech.avarLMS.repository.NotificationRepos.NotificationEventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationEventFactoryService {

    private final NotificationEventRepo notificationEventRepo;

    public void createEventIfNotExists(
            NotificationRule rule,
            String eventKey,
            NotificationChannel channel,
            String targetType,
            Long targetId,
            Long recipientUserId,
            Long recipientEmployeeId,
            String recipientEmail
    ) {
        if (notificationEventRepo.existsByEventKeyAndChannel(eventKey, channel)) {
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
                .channel(channel)
                .status(NotificationStatus.PENDING)
                .build();

        notificationEventRepo.save(event);
    }
}
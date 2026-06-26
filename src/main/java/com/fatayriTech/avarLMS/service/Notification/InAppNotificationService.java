package com.fatayriTech.avarLMS.service.Notification;

import com.fatayriTech.avarLMS.model.InAppNotification;
import com.fatayriTech.avarLMS.repository.NotificationRepos.InAppNotificationRepo;
import com.fatayriTech.avarLMS.response.notification.InAppNotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InAppNotificationService {

    private final InAppNotificationRepo repo;

    public List<InAppNotificationResponse> getMyNotifications(
            Long organizationId,
            Long userId
    ) {
        return repo.findByOrganizationIdAndUserIdOrderByCreationDateDesc(
                        organizationId,
                        userId
                )
                .stream()
                .limit(20)
                .map(this::map)
                .toList();
    }

    public Long getUnreadCount(Long organizationId, Long userId) {
        return repo.countByOrganizationIdAndUserIdAndReadFalse(
                organizationId,
                userId
        );
    }

    @Transactional
    public InAppNotificationResponse markAsRead(
            Long organizationId,
            Long notificationId
    ) {
        InAppNotification notification = repo.findByIdAndOrganizationId(
                        notificationId,
                        organizationId
                )
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());

        return map(repo.save(notification));
    }

    @Transactional
    public void markAllAsRead(Long organizationId, Long userId) {
        List<InAppNotification> notifications =
                repo.findByOrganizationIdAndUserIdOrderByCreationDateDesc(
                        organizationId,
                        userId
                );

        for (InAppNotification notification : notifications) {
            if (!Boolean.TRUE.equals(notification.getRead())) {
                notification.setRead(true);
                notification.setReadAt(LocalDateTime.now());
            }
        }

        repo.saveAll(notifications);
    }

    private InAppNotificationResponse map(InAppNotification notification) {
        return InAppNotificationResponse.builder()
                .id(notification.getId())
                .organizationId(notification.getOrganizationId())
                .userId(notification.getUserId())
                .employeeId(notification.getEmployeeId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.getRead())
                .readAt(notification.getReadAt())
                .actionUrl(notification.getActionUrl())
                .creationDate(notification.getCreationDate())
                .build();
    }
}
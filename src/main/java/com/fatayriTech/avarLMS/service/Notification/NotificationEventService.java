package com.fatayriTech.avarLMS.service.Notification;
//
import com.fatayriTech.avarLMS.enums.NotificationChannel;
import com.fatayriTech.avarLMS.enums.NotificationStatus;
import com.fatayriTech.avarLMS.model.NotificationEvent;
import com.fatayriTech.avarLMS.model.NotificationRule;
import com.fatayriTech.avarLMS.repository.NotificationRepos.NotificationEventRepo;
import com.fatayriTech.avarLMS.repository.NotificationRepos.NotificationRuleRepo;
import com.fatayriTech.avarLMS.response.notification.NotificationEngineStatsResponse;
import com.fatayriTech.avarLMS.response.notification.NotificationEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationEventService {

    private final NotificationEventRepo notificationEventRepo;
    private final NotificationRuleRepo notificationRuleRepo;

    public List<NotificationEventResponse> getEvents(Long organizationId) {
        return notificationEventRepo
                .findAll()
                .stream()
                .filter(event -> organizationId.equals(event.getOrganizationId()))
                .sorted((a, b) -> b.getCreationDate().compareTo(a.getCreationDate()))
                .map(this::mapToResponse)
                .toList();
    }

    public List<NotificationEventResponse> getEventsByStatus(
            Long organizationId,
            NotificationStatus status
    ) {
        return notificationEventRepo
                .findByOrganizationIdAndStatusOrderByCreationDateDesc(
                        organizationId,
                        status
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public NotificationEngineStatsResponse getStats(Long organizationId) {
        List<NotificationRule> rules = notificationRuleRepo
                .findAll()
                .stream()
                .filter(rule -> organizationId.equals(rule.getOrganizationId()))
                .toList();

        List<NotificationEvent> events = notificationEventRepo
                .findAll()
                .stream()
                .filter(event -> organizationId.equals(event.getOrganizationId()))
                .toList();

        long totalRules = rules.size();
        long activeRules = rules.stream()
                .filter(rule -> Boolean.TRUE.equals(rule.getActive()))
                .count();

        long inactiveRules = totalRules - activeRules;

        long pendingEvents = events.stream()
                .filter(event -> event.getStatus() == NotificationStatus.PENDING)
                .count();

        long sentEvents = events.stream()
                .filter(event -> event.getStatus() == NotificationStatus.SENT)
                .count();

        long failedEvents = events.stream()
                .filter(event -> event.getStatus() == NotificationStatus.FAILED)
                .count();

        long emailEvents = events.stream()
                .filter(event -> event.getChannel() == NotificationChannel.EMAIL)
                .count();

        long inAppEvents = events.stream()
                .filter(event -> event.getChannel() == NotificationChannel.IN_APP)
                .count();

        return NotificationEngineStatsResponse.builder()
                .totalRules(totalRules)
                .activeRules(activeRules)
                .inactiveRules(inactiveRules)
                .pendingEvents(pendingEvents)
                .sentEvents(sentEvents)
                .failedEvents(failedEvents)
                .emailEvents(emailEvents)
                .inAppEvents(inAppEvents)
                .build();
    }

    private NotificationEventResponse mapToResponse(NotificationEvent event) {
        return NotificationEventResponse.builder()
                .id(event.getId())
                .organizationId(event.getOrganizationId())
                .ruleId(event.getRule().getId())
                .ruleName(event.getRule().getName())
                .ruleCode(event.getRule().getCode())
                .targetType(event.getTargetType())
                .targetId(event.getTargetId())
                .recipientUserId(event.getRecipientUserId())
                .recipientEmployeeId(event.getRecipientEmployeeId())
                .recipientEmail(event.getRecipientEmail())
                .eventKey(event.getEventKey())
                .channel(event.getChannel())
                .status(event.getStatus())
                .sentAt(event.getSentAt())
                .errorMessage(event.getErrorMessage())
                .creationDate(event.getCreationDate())
                .modificationDate(event.getModificationDate())
                .build();
    }
}
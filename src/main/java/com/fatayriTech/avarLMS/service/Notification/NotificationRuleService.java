package com.fatayriTech.avarLMS.service.Notification;

import com.fatayriTech.avarLMS.enums.NotificationRecipientType;
import com.fatayriTech.avarLMS.model.NotificationRule;
import com.fatayriTech.avarLMS.repository.NotificationRepos.NotificationRuleRepo;
import com.fatayriTech.avarLMS.request.notification.NotificationRuleRequest;
import com.fatayriTech.avarLMS.response.notification.NotificationRuleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationRuleService {

    private final NotificationRuleRepo notificationRuleRepo;

    public NotificationRuleResponse create(Long organizationId, NotificationRuleRequest request) {
        validateRequest(request);

        if (notificationRuleRepo.existsByOrganizationIdAndCodeIgnoreCase(
                organizationId,
                request.getCode()
        )) {
            throw new RuntimeException("Notification rule code already exists");
        }

        NotificationRule rule = NotificationRule.builder()
                .organizationId(organizationId)
                .code(request.getCode())
                .name(request.getName())
                .module(request.getModule())
                .eventType(request.getEventType())
                .recipientType(
                        request.getRecipientType() == null
                                ? NotificationRecipientType.EMPLOYEE
                                : request.getRecipientType()
                )
                .recipientTargetId(request.getRecipientTargetId())
                .recipientTargetCode(request.getRecipientTargetCode())
                .channelEmail(Boolean.TRUE.equals(request.getChannelEmail()))
                .channelInApp(Boolean.TRUE.equals(request.getChannelInApp()))
                .daysBefore(request.getDaysBefore())
                .daysAfter(request.getDaysAfter())
                .cronExpression(request.getCronExpression())
                .active(request.getActive() != null ? request.getActive() : true)
                .subjectTemplate(request.getSubjectTemplate())
                .bodyTemplate(request.getBodyTemplate())
                .build();

        return mapToResponse(notificationRuleRepo.save(rule));
    }

    public List<NotificationRuleResponse> getAll(Long organizationId) {
        return notificationRuleRepo
                .findAll()
                .stream()
                .filter(rule -> organizationId.equals(rule.getOrganizationId()))
                .sorted((a, b) -> b.getCreationDate().compareTo(a.getCreationDate()))
                .map(this::mapToResponse)
                .toList();
    }

    public List<NotificationRuleResponse> getActive(Long organizationId) {
        return notificationRuleRepo
                .findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public NotificationRuleResponse getById(Long organizationId, Long id) {
        NotificationRule rule = findRule(organizationId, id);
        return mapToResponse(rule);
    }

    public NotificationRuleResponse update(
            Long organizationId,
            Long id,
            NotificationRuleRequest request
    ) {
        validateRequest(request);

        NotificationRule rule = findRule(organizationId, id);

        if (!rule.getCode().equalsIgnoreCase(request.getCode()) &&
                notificationRuleRepo.existsByOrganizationIdAndCodeIgnoreCase(
                        organizationId,
                        request.getCode()
                )) {
            throw new RuntimeException("Notification rule code already exists");
        }

        rule.setCode(request.getCode());
        rule.setName(request.getName());
        rule.setModule(request.getModule());
        rule.setEventType(request.getEventType());
        rule.setRecipientType(
                request.getRecipientType() == null
                        ? NotificationRecipientType.EMPLOYEE
                        : request.getRecipientType()
        );
        rule.setRecipientTargetId(request.getRecipientTargetId());
        rule.setRecipientTargetCode(request.getRecipientTargetCode());
        rule.setChannelEmail(Boolean.TRUE.equals(request.getChannelEmail()));
        rule.setChannelInApp(Boolean.TRUE.equals(request.getChannelInApp()));
        rule.setDaysBefore(request.getDaysBefore());
        rule.setDaysAfter(request.getDaysAfter());
        rule.setCronExpression(request.getCronExpression());
        rule.setActive(request.getActive() != null ? request.getActive() : rule.getActive());
        rule.setSubjectTemplate(request.getSubjectTemplate());
        rule.setBodyTemplate(request.getBodyTemplate());

        return mapToResponse(notificationRuleRepo.save(rule));
    }

    public NotificationRuleResponse toggleStatus(Long organizationId, Long id) {
        NotificationRule rule = findRule(organizationId, id);
        rule.setActive(!Boolean.TRUE.equals(rule.getActive()));
        return mapToResponse(notificationRuleRepo.save(rule));
    }

    public void delete(Long organizationId, Long id) {
        NotificationRule rule = findRule(organizationId, id);
        rule.setActive(false);
        notificationRuleRepo.save(rule);
    }

    private NotificationRule findRule(Long organizationId, Long id) {
        return notificationRuleRepo
                .findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Notification rule not found"));
    }

    private void validateRequest(NotificationRuleRequest request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new RuntimeException("Rule code is required");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Rule name is required");
        }

        if (request.getModule() == null) {
            throw new RuntimeException("Notification module is required");
        }

        if (request.getEventType() == null) {
            throw new RuntimeException("Notification event type is required");
        }

        NotificationRecipientType recipientType =
                request.getRecipientType() == null
                        ? NotificationRecipientType.EMPLOYEE
                        : request.getRecipientType();

        if (recipientType == NotificationRecipientType.DEPARTMENT
                && request.getRecipientTargetId() == null) {
            throw new RuntimeException("Department is required for department recipient rules");
        }

        if ((recipientType == NotificationRecipientType.EMPLOYEE_TYPE
                || recipientType == NotificationRecipientType.ACADEMY)
                && (request.getRecipientTargetCode() == null
                || request.getRecipientTargetCode().isBlank())) {
            throw new RuntimeException("Recipient target code is required for this recipient type");
        }
    }

    private NotificationRuleResponse mapToResponse(NotificationRule rule) {
        return NotificationRuleResponse.builder()
                .id(rule.getId())
                .organizationId(rule.getOrganizationId())
                .code(rule.getCode())
                .name(rule.getName())
                .module(rule.getModule())
                .eventType(rule.getEventType())
                .recipientType(rule.getRecipientType())
                .recipientTargetId(rule.getRecipientTargetId())
                .recipientTargetCode(rule.getRecipientTargetCode())
                .channelEmail(rule.getChannelEmail())
                .channelInApp(rule.getChannelInApp())
                .daysBefore(rule.getDaysBefore())
                .daysAfter(rule.getDaysAfter())
                .cronExpression(rule.getCronExpression())
                .active(rule.getActive())
                .subjectTemplate(rule.getSubjectTemplate())
                .bodyTemplate(rule.getBodyTemplate())
                .creationDate(rule.getCreationDate())
                .modificationDate(rule.getModificationDate())
                .build();
    }
}
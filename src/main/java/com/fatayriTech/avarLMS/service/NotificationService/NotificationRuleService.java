package com.fatayriTech.avarLMS.service.NotificationService;

import com.fatayriTech.avarLMS.enums.NotificationEventType;
import com.fatayriTech.avarLMS.enums.NotificationModule;
import com.fatayriTech.avarLMS.model.NotificationRule;
import com.fatayriTech.avarLMS.repository.NotificationRepos.NotificationRuleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationRuleService {

    private final NotificationRuleRepo notificationRuleRepo;

    public List<NotificationRule> getRules(Long organizationId) {
        return notificationRuleRepo
                .findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(
                        organizationId
                );
    }

    public List<NotificationRule> getActiveRulesByModuleAndEvent(
            Long organizationId,
            NotificationModule module,
            NotificationEventType eventType
    ) {
        return notificationRuleRepo
                .findByOrganizationIdAndModuleAndEventTypeAndActiveTrue(
                        organizationId,
                        module,
                        eventType
                );
    }

    public NotificationRule createRule(
            Long organizationId,
            NotificationRule rule
    ) {
        if (rule.getCode() == null || rule.getCode().isBlank()) {
            throw new RuntimeException("Notification rule code is required");
        }

        if (notificationRuleRepo.existsByOrganizationIdAndCodeIgnoreCase(
                organizationId,
                rule.getCode()
        )) {
            throw new RuntimeException("Notification rule code already exists");
        }

        rule.setOrganizationId(organizationId);
        return notificationRuleRepo.save(rule);
    }

    public NotificationRule updateRule(
            Long organizationId,
            Long ruleId,
            NotificationRule request
    ) {
        NotificationRule rule = notificationRuleRepo
                .findByIdAndOrganizationId(ruleId, organizationId)
                .orElseThrow(() -> new RuntimeException("Notification rule not found"));

        rule.setName(request.getName());
        rule.setModule(request.getModule());
        rule.setEventType(request.getEventType());
        rule.setChannelEmail(request.getChannelEmail());
        rule.setChannelInApp(request.getChannelInApp());
        rule.setDaysBefore(request.getDaysBefore());
        rule.setDaysAfter(request.getDaysAfter());
        rule.setCronExpression(request.getCronExpression());
        rule.setSubjectTemplate(request.getSubjectTemplate());
        rule.setBodyTemplate(request.getBodyTemplate());
        rule.setActive(request.getActive());

        return notificationRuleRepo.save(rule);
    }

    public void deactivateRule(Long organizationId, Long ruleId) {
        NotificationRule rule = notificationRuleRepo
                .findByIdAndOrganizationId(ruleId, organizationId)
                .orElseThrow(() -> new RuntimeException("Notification rule not found"));

        rule.setActive(false);
        notificationRuleRepo.save(rule);
    }
}
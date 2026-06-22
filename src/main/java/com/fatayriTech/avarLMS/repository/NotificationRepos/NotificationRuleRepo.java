package com.fatayriTech.avarLMS.repository.NotificationRepos;

import com.fatayriTech.avarLMS.enums.NotificationEventType;
import com.fatayriTech.avarLMS.enums.NotificationModule;
import com.fatayriTech.avarLMS.model.NotificationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRuleRepo extends JpaRepository<NotificationRule, Long> {

    List<NotificationRule> findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(
            Long organizationId
    );

    List<NotificationRule> findByModuleAndEventTypeAndActiveTrue(
            NotificationModule module,
            NotificationEventType eventType
    );

    List<NotificationRule> findByOrganizationIdAndModuleAndEventTypeAndActiveTrue(
            Long organizationId,
            NotificationModule module,
            NotificationEventType eventType
    );

    Optional<NotificationRule> findByIdAndOrganizationId(
            Long id,
            Long organizationId
    );

    boolean existsByOrganizationIdAndCodeIgnoreCase(
            Long organizationId,
            String code
    );
}
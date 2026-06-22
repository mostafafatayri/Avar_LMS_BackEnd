package com.fatayriTech.avarLMS.repository.NotificationRepos;



import com.fatayriTech.avarLMS.model.InAppNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InAppNotificationRepo extends JpaRepository<InAppNotification, Long> {

    List<InAppNotification> findByOrganizationIdAndUserIdOrderByCreationDateDesc(
            Long organizationId,
            Long userId
    );

    List<InAppNotification> findByOrganizationIdAndEmployeeIdOrderByCreationDateDesc(
            Long organizationId,
            Long employeeId
    );

    Long countByOrganizationIdAndUserIdAndReadFalse(
            Long organizationId,
            Long userId
    );

    Optional<InAppNotification> findByIdAndOrganizationId(
            Long id,
            Long organizationId
    );
}
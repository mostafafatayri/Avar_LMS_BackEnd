package com.fatayriTech.avarLMS.repository.NotificationRepos;

import com.fatayriTech.avarLMS.enums.NotificationChannel;
import com.fatayriTech.avarLMS.enums.NotificationStatus;
import com.fatayriTech.avarLMS.model.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationEventRepo extends JpaRepository<NotificationEvent, Long> {

    boolean existsByEventKeyAndChannel(
            String eventKey,
            NotificationChannel channel
    );

    Optional<NotificationEvent> findByEventKeyAndChannel(
            String eventKey,
            NotificationChannel channel
    );

    List<NotificationEvent> findTop20ByStatusOrderByCreationDateAsc(
            NotificationStatus status
    );

    List<NotificationEvent> findByOrganizationIdAndStatusOrderByCreationDateDesc(
            Long organizationId,
            NotificationStatus status
    );
}
package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.OrganizationAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationAccessRequestRepo
        extends JpaRepository<OrganizationAccessRequest, Long> {

    Optional<OrganizationAccessRequest>
    findByRequesterUserIdAndOrganizationIdAndStatus(
            Long requesterUserId,
            Long organizationId,
            String status
    );

    List<OrganizationAccessRequest>
    findByOrganizationIdOrderByCreationDateDesc(Long organizationId);
}
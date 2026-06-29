package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepo extends JpaRepository<Location, Long> {

    List<Location> findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(
            Long organizationId
    );

    List<Location> findByOrganizationIdOrderByCreationDateDesc(
            Long organizationId
    );

    Optional<Location> findByIdAndOrganizationId(
            Long id,
            Long organizationId
    );

    boolean existsByOrganizationIdAndNameIgnoreCase(
            Long organizationId,
            String name
    );

    boolean existsByOrganizationIdAndCodeIgnoreCase(
            Long organizationId,
            String code
    );

    long countByOrganizationIdAndActiveTrue(Long organizationId);

    long countByOrganizationIdAndActiveFalse(Long organizationId);

    Optional<Location> findByOrganizationIdAndCodeIgnoreCase(
            Long organizationId,
            String code
    );
}
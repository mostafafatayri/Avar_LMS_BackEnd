package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.SeniorityLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeniorityLevelRepo extends JpaRepository<SeniorityLevel, Long> {

    List<SeniorityLevel> findByOrganizationIdOrderByDisplayOrderAsc(
            Long organizationId
    );

    List<SeniorityLevel> findByOrganizationIdAndActiveTrueOrderByDisplayOrderAsc(
            Long organizationId
    );

    Optional<SeniorityLevel> findByIdAndOrganizationId(
            Long id,
            Long organizationId
    );

    boolean existsByNameIgnoreCaseAndOrganizationId(
            String name,
            Long organizationId
    );

    boolean existsByDisplayOrderAndOrganizationId(
            Integer displayOrder,
            Long organizationId
    );

    Optional<SeniorityLevel> findByNameIgnoreCaseAndOrganizationId(
            String name,
            Long organizationId
    );
}
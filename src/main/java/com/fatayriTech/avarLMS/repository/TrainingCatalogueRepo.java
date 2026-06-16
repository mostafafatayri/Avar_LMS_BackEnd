package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingCatalogueRepo extends JpaRepository<TrainingCatalogue, Long> {

    List<TrainingCatalogue> findByOrganizationIdAndActiveTrue(Long organizationId);

    Optional<TrainingCatalogue> findByIdAndOrganizationId(Long id, Long organizationId);

    Optional<TrainingCatalogue> findByIdAndOrganizationIdAndActiveTrue(
            Long id,
            Long organizationId
    );
}
package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingDisplayModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingDisplayModuleRepo extends JpaRepository<TrainingDisplayModule, Long> {

    List<TrainingDisplayModule> findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
            Long organizationId,
            Long trainingCatalogueId
    );

    Optional<TrainingDisplayModule> findByIdAndOrganizationIdAndTrainingCatalogueIdAndActiveTrue(
            Long id,
            Long organizationId,
            Long trainingCatalogueId
    );
}
package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingRequirementRepo extends JpaRepository<TrainingRequirement, Long> {

    List<TrainingRequirement> findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
            Long organizationId,
            Long trainingCatalogueId
    );

    Optional<TrainingRequirement> findByIdAndOrganizationIdAndTrainingCatalogueId(
            Long id,
            Long organizationId,
            Long trainingCatalogueId
    );
}
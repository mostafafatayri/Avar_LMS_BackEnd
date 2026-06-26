package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.enums.TrainingProgressStatus;
import com.fatayriTech.avarLMS.model.TrainingProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingProgressRepo extends JpaRepository<TrainingProgress, Long> {

    Optional<TrainingProgress> findByOrganizationIdAndUserIdAndModuleItemId(
            Long organizationId,
            Long userId,
            Long moduleItemId
    );

    List<TrainingProgress> findByOrganizationIdAndUserIdAndTrainingCatalogueId(
            Long organizationId,
            Long userId,
            Long trainingCatalogueId
    );

    long countByOrganizationIdAndUserIdAndTrainingCatalogueIdAndStatus(
            Long organizationId,
            Long userId,
            Long trainingCatalogueId,
            TrainingProgressStatus status
    );
}
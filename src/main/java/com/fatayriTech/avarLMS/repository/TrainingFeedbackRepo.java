package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingFeedbackRepo extends JpaRepository<TrainingFeedback, Long> {

    List<TrainingFeedback> findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByCreationDateDesc(
            Long organizationId,
            Long trainingCatalogueId
    );

    Optional<TrainingFeedback> findByOrganizationIdAndTrainingCatalogueIdAndUserIdAndActiveTrue(
            Long organizationId,
            Long trainingCatalogueId,
            Long userId
    );

    Optional<TrainingFeedback> findByIdAndOrganizationIdAndTrainingCatalogueIdAndActiveTrue(
            Long id,
            Long organizationId,
            Long trainingCatalogueId
    );

    boolean existsByOrganizationIdAndTrainingCatalogueIdAndUserIdAndActiveTrue(
            Long organizationId,
            Long trainingCatalogueId,
            Long userId
    );
}
package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingLearningObjective;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingLearningObjectiveRepo extends JpaRepository<TrainingLearningObjective, Long> {

    List<TrainingLearningObjective> findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
            Long organizationId,
            Long trainingCatalogueId
    );

    Optional<TrainingLearningObjective> findByIdAndOrganizationIdAndTrainingCatalogueId(
            Long id,
            Long organizationId,
            Long trainingCatalogueId
    );
}
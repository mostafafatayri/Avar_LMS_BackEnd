package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingQuizRepo extends JpaRepository<TrainingQuiz, Long> {

    List<TrainingQuiz> findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByIdDesc(
            Long organizationId,
            Long trainingCatalogueId
    );

    Optional<TrainingQuiz> findByIdAndOrganizationIdAndTrainingCatalogueId(
            Long id,
            Long organizationId,
            Long trainingCatalogueId
    );
}
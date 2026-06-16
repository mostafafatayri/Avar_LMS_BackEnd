package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingVideoRepo extends JpaRepository<TrainingVideo, Long> {

    List<TrainingVideo> findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
            Long organizationId,
            Long trainingCatalogueId
    );

    Optional<TrainingVideo> findByIdAndOrganizationIdAndTrainingCatalogueId(
            Long id,
            Long organizationId,
            Long trainingCatalogueId
    );
}
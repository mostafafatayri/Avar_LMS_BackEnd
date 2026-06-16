package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingLecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingLectureRepo extends JpaRepository<TrainingLecture, Long> {

    List<TrainingLecture> findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
            Long organizationId,
            Long trainingCatalogueId
    );

    Optional<TrainingLecture> findByIdAndOrganizationIdAndTrainingCatalogueId(
            Long id,
            Long organizationId,
            Long trainingCatalogueId
    );
}
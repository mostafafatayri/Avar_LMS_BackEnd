package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingEnrollmentRepo extends JpaRepository<TrainingEnrollment, Long> {

    List<TrainingEnrollment> findByOrganizationIdAndTrainingCatalogueIdAndActiveTrue(
            Long organizationId,
            Long trainingCatalogueId
    );

    Optional<TrainingEnrollment> findByOrganizationIdAndTrainingCatalogueIdAndUserIdAndActiveTrue(
            Long organizationId,
            Long trainingCatalogueId,
            Long userId
    );

    boolean existsByOrganizationIdAndTrainingCatalogueIdAndUserIdAndActiveTrue(
            Long organizationId,
            Long trainingCatalogueId,
            Long userId
    );

    boolean existsByOrganizationIdAndTrainingCatalogueIdAndUserIdAndStatusInAndActiveTrue(
            Long organizationId,
            Long trainingCatalogueId,
            Long userId,
            List<String> statuses
    );
}
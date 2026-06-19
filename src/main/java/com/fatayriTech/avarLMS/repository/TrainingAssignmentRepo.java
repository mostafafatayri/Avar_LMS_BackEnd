package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.enums.TrainingAssignmentStatus;
import com.fatayriTech.avarLMS.model.TrainingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingAssignmentRepo extends JpaRepository<TrainingAssignment, Long> {

    List<TrainingAssignment> findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(
            Long organizationId
    );

    Optional<TrainingAssignment> findByIdAndOrganizationIdAndActiveTrue(
            Long id,
            Long organizationId
    );

    boolean existsByOrganizationIdAndEmployeeIdAndTrainingCatalogueIdAndActiveTrue(
            Long organizationId,
            Long employeeId,
            Long trainingCatalogueId
    );

    List<TrainingAssignment> findByOrganizationIdAndEmployeeIdAndActiveTrueOrderByCreationDateDesc(
            Long organizationId,
            Long employeeId
    );

    List<TrainingAssignment> findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByCreationDateDesc(
            Long organizationId,
            Long trainingCatalogueId
    );

    long countByOrganizationIdAndStatusAndActiveTrue(
            Long organizationId,
            TrainingAssignmentStatus status
    );

    List<TrainingAssignment> findByOrganizationIdAndDueDateBeforeAndStatusInAndActiveTrue(
            Long organizationId,
            LocalDate dueDate,
            List<TrainingAssignmentStatus> statuses
    );
}
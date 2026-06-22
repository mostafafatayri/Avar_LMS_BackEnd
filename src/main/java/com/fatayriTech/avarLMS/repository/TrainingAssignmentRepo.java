package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.enums.LearningPathAssignmentStatus;
import com.fatayriTech.avarLMS.enums.TrainingAssignmentStatus;
import com.fatayriTech.avarLMS.model.LearningPathAssignment;
import com.fatayriTech.avarLMS.model.TrainingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    List<TrainingAssignment> findByOrganizationIdAndExpiryDateBeforeAndStatusInAndActiveTrue(
            Long organizationId,
            LocalDate expiryDate,
            List<TrainingAssignmentStatus> statuses
    );

    List<TrainingAssignment> findByOrganizationIdAndExpiryDateAndStatusInAndActiveTrue(
            Long organizationId,
            LocalDate expiryDate,
            List<TrainingAssignmentStatus> statuses
    );

    @Query("""
    select ta from TrainingAssignment ta
    join fetch ta.employee e
    join fetch ta.trainingCatalogue tc
    where ta.organizationId = :organizationId
      and ta.expiryDate = :expiryDate
      and ta.status in :statuses
      and ta.active = true
""")
    List<TrainingAssignment> findExpiryReminderAssignments(
            Long organizationId,
            LocalDate expiryDate,
            List<TrainingAssignmentStatus> statuses
    );



}
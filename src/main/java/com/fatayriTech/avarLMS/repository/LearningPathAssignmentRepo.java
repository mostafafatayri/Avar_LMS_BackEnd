package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.enums.LearningPathAssignmentStatus;
import com.fatayriTech.avarLMS.enums.LearningPathAssignmentTargetType;
import com.fatayriTech.avarLMS.model.LearningPathAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LearningPathAssignmentRepo extends JpaRepository<LearningPathAssignment, Long> {

    List<LearningPathAssignment> findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(
            Long organizationId
    );

    List<LearningPathAssignment> findByOrganizationIdAndLearningPathIdAndActiveTrueOrderByCreationDateDesc(
            Long organizationId,
            Long learningPathId
    );

    Optional<LearningPathAssignment> findByIdAndOrganizationIdAndActiveTrue(
            Long id,
            Long organizationId
    );

    Optional<LearningPathAssignment> findByIdAndOrganizationIdAndLearningPathIdAndActiveTrue(
            Long id,
            Long organizationId,
            Long learningPathId
    );

    boolean existsByOrganizationIdAndLearningPathIdAndTargetTypeAndTargetIdAndActiveTrue(
            Long organizationId,
            Long learningPathId,
            LearningPathAssignmentTargetType targetType,
            Long targetId
    );

    long countByOrganizationIdAndLearningPathIdAndActiveTrue(
            Long organizationId,
            Long learningPathId
    );

    List<LearningPathAssignment> findByOrganizationIdAndDueDateBeforeAndStatusInAndActiveTrue(
            Long organizationId,
            LocalDate dueDate,
            List<LearningPathAssignmentStatus> statuses
    );
}
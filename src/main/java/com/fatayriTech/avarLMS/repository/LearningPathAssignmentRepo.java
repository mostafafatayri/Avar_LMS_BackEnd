package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.LearningPathAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningPathAssignmentRepo extends JpaRepository<LearningPathAssignment, Long> {

    List<LearningPathAssignment> findByOrganizationIdAndLearningPathIdAndActiveTrueOrderByAssignedDateDesc(
            Long organizationId,
            Long learningPathId
    );

    Optional<LearningPathAssignment> findByIdAndOrganizationIdAndLearningPathIdAndActiveTrue(
            Long id,
            Long organizationId,
            Long learningPathId
    );

    boolean existsByOrganizationIdAndLearningPathIdAndAssignmentTypeAndTargetIdAndActiveTrue(
            Long organizationId,
            Long learningPathId,
            String assignmentType,
            Long targetId
    );

    long countByOrganizationIdAndLearningPathIdAndActiveTrue(
            Long organizationId,
            Long learningPathId
    );
}
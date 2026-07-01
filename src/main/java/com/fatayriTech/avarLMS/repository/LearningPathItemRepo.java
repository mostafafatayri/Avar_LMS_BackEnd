package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.LearningPathItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LearningPathItemRepo extends JpaRepository<LearningPathItem, Long> {

    List<LearningPathItem> findByOrganizationIdAndLearningPathIdAndActiveTrueOrderByDisplayOrderAsc(
            Long organizationId,
            Long learningPathId
    );

    Optional<LearningPathItem> findByIdAndOrganizationIdAndLearningPathIdAndActiveTrue(
            Long id,
            Long organizationId,
            Long learningPathId
    );

    boolean existsByOrganizationIdAndLearningPathIdAndTrainingCatalogueIdAndActiveTrue(
            Long organizationId,
            Long learningPathId,
            Long trainingCatalogueId
    );

    long countByOrganizationIdAndLearningPathIdAndActiveTrue(
            Long organizationId,
            Long learningPathId
    );

    @Query("""
    select item from LearningPathItem item
    join fetch item.trainingCatalogue tc
    where item.organizationId = :organizationId
      and item.learningPath.id = :learningPathId
      and item.active = true
    order by item.displayOrder asc
     """)
    List<LearningPathItem> findPathItemsWithTraining(
            Long organizationId,
            Long learningPathId
    );
}
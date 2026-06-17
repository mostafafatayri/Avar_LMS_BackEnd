package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningPathRepo extends JpaRepository<LearningPath, Long> {

    List<LearningPath> findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(
            Long organizationId
    );

    Optional<LearningPath> findByIdAndOrganizationIdAndActiveTrue(
            Long id,
            Long organizationId
    );

    boolean existsByOrganizationIdAndNameIgnoreCaseAndActiveTrue(
            Long organizationId,
            String name
    );
}
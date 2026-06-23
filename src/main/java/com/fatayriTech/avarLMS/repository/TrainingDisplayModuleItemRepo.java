package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingDisplayModuleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingDisplayModuleItemRepo extends JpaRepository<TrainingDisplayModuleItem, Long> {

    List<TrainingDisplayModuleItem> findByOrganizationIdAndModuleIdAndActiveTrueOrderByDisplayOrderAsc(
            Long organizationId,
            Long moduleId
    );

    Optional<TrainingDisplayModuleItem> findByIdAndOrganizationIdAndModuleIdAndActiveTrue(
            Long id,
            Long organizationId,
            Long moduleId
    );
}
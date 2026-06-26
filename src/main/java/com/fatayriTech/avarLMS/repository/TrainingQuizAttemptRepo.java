package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingQuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingQuizAttemptRepo extends JpaRepository<TrainingQuizAttempt, Long> {

    long countByOrganizationIdAndUserIdAndModuleItemId(
            Long organizationId,
            Long userId,
            Long moduleItemId
    );

    List<TrainingQuizAttempt> findByOrganizationIdAndUserIdAndModuleItemIdOrderByAttemptNumberDesc(
            Long organizationId,
            Long userId,
            Long moduleItemId
    );
}
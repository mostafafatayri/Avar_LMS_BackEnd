package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingQuizOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingQuizOptionRepo extends JpaRepository<TrainingQuizOption, Long> {

    List<TrainingQuizOption> findByOrganizationIdAndQuestionIdAndActiveTrueOrderByDisplayOrderAsc(
            Long organizationId,
            Long questionId
    );

    Optional<TrainingQuizOption> findByIdAndOrganizationIdAndQuestionId(
            Long id,
            Long organizationId,
            Long questionId
    );
}
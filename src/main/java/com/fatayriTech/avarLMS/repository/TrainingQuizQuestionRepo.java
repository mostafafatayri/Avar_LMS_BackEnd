package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingQuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingQuizQuestionRepo extends JpaRepository<TrainingQuizQuestion, Long> {

    List<TrainingQuizQuestion> findByOrganizationIdAndQuizIdAndActiveTrueOrderByDisplayOrderAsc(
            Long organizationId,
            Long quizId
    );

    Optional<TrainingQuizQuestion> findByIdAndOrganizationIdAndQuizId(
            Long id,
            Long organizationId,
            Long quizId
    );
}
package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.model.*;
import com.fatayriTech.avarLMS.repository.*;
import com.fatayriTech.avarLMS.request.training.*;
import com.fatayriTech.avarLMS.response.training.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingQuizService {

    private final TrainingCatalogueRepo trainingCatalogueRepo;
    private final TrainingQuizRepo quizRepo;
    private final TrainingQuizQuestionRepo questionRepo;
    private final TrainingQuizOptionRepo optionRepo;

    public List<TrainingQuizResponse> getQuizzes(Long organizationId, Long trainingId) {
        return quizRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByIdDesc(
                        organizationId,
                        trainingId
                )
                .stream()
                .map(this::mapQuiz)
                .toList();
    }

    public TrainingQuizResponse createQuiz(
            Long organizationId,
            Long trainingId,
            TrainingQuizRequest request
    ) {
        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationId(trainingId, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        TrainingQuiz quiz = TrainingQuiz.builder()
                .organizationId(organizationId)
                .trainingCatalogue(training)
                .title(request.getTitle())
                .description(request.getDescription())
                .passingScore(request.getPassingScore() != null ? request.getPassingScore() : 70)
                .timeLimitMinutes(request.getTimeLimitMinutes() != null ? request.getTimeLimitMinutes() : 0)
                .maxAttempts(request.getMaxAttempts() != null ? request.getMaxAttempts() : 1)
                .shuffleQuestions(Boolean.TRUE.equals(request.getShuffleQuestions()))
                .active(true)
                .build();

        return mapQuiz(quizRepo.save(quiz));
    }

    public TrainingQuizResponse updateQuiz(
            Long organizationId,
            Long trainingId,
            Long quizId,
            TrainingQuizRequest request
    ) {
        TrainingQuiz quiz = findQuiz(organizationId, trainingId, quizId);

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setPassingScore(request.getPassingScore() != null ? request.getPassingScore() : 70);
        quiz.setTimeLimitMinutes(request.getTimeLimitMinutes() != null ? request.getTimeLimitMinutes() : 0);
        quiz.setMaxAttempts(request.getMaxAttempts() != null ? request.getMaxAttempts() : 1);
        quiz.setShuffleQuestions(Boolean.TRUE.equals(request.getShuffleQuestions()));

        return mapQuiz(quizRepo.save(quiz));
    }

    public void deleteQuiz(Long organizationId, Long trainingId, Long quizId) {
        TrainingQuiz quiz = findQuiz(organizationId, trainingId, quizId);
        quiz.setActive(false);
        quizRepo.save(quiz);
    }

    public TrainingQuizQuestionResponse createQuestion(
            Long organizationId,
            Long trainingId,
            Long quizId,
            TrainingQuizQuestionRequest request
    ) {
        TrainingQuiz quiz = findQuiz(organizationId, trainingId, quizId);

        TrainingQuizQuestion question = TrainingQuizQuestion.builder()
                .organizationId(organizationId)
                .quiz(quiz)
                .questionText(request.getQuestionText())
                .questionType(request.getQuestionType() != null ? request.getQuestionType() : "SINGLE_CHOICE")
                .points(request.getPoints() != null ? request.getPoints() : 1)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .active(true)
                .build();

        return mapQuestion(questionRepo.save(question));
    }

    public TrainingQuizQuestionResponse updateQuestion(
            Long organizationId,
            Long trainingId,
            Long quizId,
            Long questionId,
            TrainingQuizQuestionRequest request
    ) {
        findQuiz(organizationId, trainingId, quizId);

        TrainingQuizQuestion question = questionRepo
                .findByIdAndOrganizationIdAndQuizId(questionId, organizationId, quizId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType() != null ? request.getQuestionType() : "SINGLE_CHOICE");
        question.setPoints(request.getPoints() != null ? request.getPoints() : 1);
        question.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        return mapQuestion(questionRepo.save(question));
    }

    public void deleteQuestion(
            Long organizationId,
            Long trainingId,
            Long quizId,
            Long questionId
    ) {
        findQuiz(organizationId, trainingId, quizId);

        TrainingQuizQuestion question = questionRepo
                .findByIdAndOrganizationIdAndQuizId(questionId, organizationId, quizId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setActive(false);
        questionRepo.save(question);
    }

    public TrainingQuizOptionResponse createOption(
            Long organizationId,
            Long trainingId,
            Long quizId,
            Long questionId,
            TrainingQuizOptionRequest request
    ) {
        findQuiz(organizationId, trainingId, quizId);

        TrainingQuizQuestion question = questionRepo
                .findByIdAndOrganizationIdAndQuizId(questionId, organizationId, quizId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        TrainingQuizOption option = TrainingQuizOption.builder()
                .organizationId(organizationId)
                .question(question)
                .optionText(request.getOptionText())
                .correct(Boolean.TRUE.equals(request.getCorrect()))
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .active(true)
                .build();

        return mapOption(optionRepo.save(option));
    }

    public TrainingQuizOptionResponse updateOption(
            Long organizationId,
            Long trainingId,
            Long quizId,
            Long questionId,
            Long optionId,
            TrainingQuizOptionRequest request
    ) {
        findQuiz(organizationId, trainingId, quizId);

        TrainingQuizOption option = optionRepo
                .findByIdAndOrganizationIdAndQuestionId(optionId, organizationId, questionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        option.setOptionText(request.getOptionText());
        option.setCorrect(Boolean.TRUE.equals(request.getCorrect()));
        option.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        return mapOption(optionRepo.save(option));
    }

    public void deleteOption(
            Long organizationId,
            Long trainingId,
            Long quizId,
            Long questionId,
            Long optionId
    ) {
        findQuiz(organizationId, trainingId, quizId);

        TrainingQuizOption option = optionRepo
                .findByIdAndOrganizationIdAndQuestionId(optionId, organizationId, questionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        option.setActive(false);
        optionRepo.save(option);
    }

    private TrainingQuiz findQuiz(Long organizationId, Long trainingId, Long quizId) {
        return quizRepo
                .findByIdAndOrganizationIdAndTrainingCatalogueId(
                        quizId,
                        organizationId,
                        trainingId
                )
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    private TrainingQuizResponse mapQuiz(TrainingQuiz quiz) {
        List<TrainingQuizQuestionResponse> questions = questionRepo
                .findByOrganizationIdAndQuizIdAndActiveTrueOrderByDisplayOrderAsc(
                        quiz.getOrganizationId(),
                        quiz.getId()
                )
                .stream()
                .map(this::mapQuestion)
                .toList();

        return TrainingQuizResponse.builder()
                .id(quiz.getId())
                .organizationId(quiz.getOrganizationId())
                .trainingCatalogueId(quiz.getTrainingCatalogue().getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .passingScore(quiz.getPassingScore())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .maxAttempts(quiz.getMaxAttempts())
                .shuffleQuestions(quiz.getShuffleQuestions())
                .active(quiz.getActive())
                .creationDate(quiz.getCreationDate())
                .modificationDate(quiz.getModificationDate())
                .questions(questions)
                .build();
    }

    private TrainingQuizQuestionResponse mapQuestion(TrainingQuizQuestion question) {
        List<TrainingQuizOptionResponse> options = optionRepo
                .findByOrganizationIdAndQuestionIdAndActiveTrueOrderByDisplayOrderAsc(
                        question.getOrganizationId(),
                        question.getId()
                )
                .stream()
                .map(this::mapOption)
                .toList();

        return TrainingQuizQuestionResponse.builder()
                .id(question.getId())
                .organizationId(question.getOrganizationId())
                .quizId(question.getQuiz().getId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .points(question.getPoints())
                .displayOrder(question.getDisplayOrder())
                .active(question.getActive())
                .creationDate(question.getCreationDate())
                .modificationDate(question.getModificationDate())
                .options(options)
                .build();
    }

    private TrainingQuizOptionResponse mapOption(TrainingQuizOption option) {
        return TrainingQuizOptionResponse.builder()
                .id(option.getId())
                .organizationId(option.getOrganizationId())
                .questionId(option.getQuestion().getId())
                .optionText(option.getOptionText())
                .correct(option.getCorrect())
                .displayOrder(option.getDisplayOrder())
                .active(option.getActive())
                .creationDate(option.getCreationDate())
                .modificationDate(option.getModificationDate())
                .build();
    }
}
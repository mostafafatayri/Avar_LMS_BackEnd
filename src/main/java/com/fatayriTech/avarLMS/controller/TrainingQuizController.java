package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.training.*;
import com.fatayriTech.avarLMS.response.training.*;
import com.fatayriTech.avarLMS.service.Training.TrainingQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/training-catalogue/{trainingId}/quizzes")
@RequiredArgsConstructor
public class TrainingQuizController {

    private final TrainingQuizService quizService;

    @GetMapping
    public List<TrainingQuizResponse> getQuizzes(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId
    ) {
        return quizService.getQuizzes(organizationId, trainingId);
    }

    @PostMapping
    public TrainingQuizResponse createQuiz(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @RequestBody TrainingQuizRequest request
    ) {
        return quizService.createQuiz(organizationId, trainingId, request);
    }

    @PutMapping("/{quizId}")
    public TrainingQuizResponse updateQuiz(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long quizId,
            @RequestBody TrainingQuizRequest request
    ) {
        return quizService.updateQuiz(organizationId, trainingId, quizId, request);
    }

    @DeleteMapping("/{quizId}")
    public void deleteQuiz(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long quizId
    ) {
        quizService.deleteQuiz(organizationId, trainingId, quizId);
    }

    @PostMapping("/{quizId}/questions")
    public TrainingQuizQuestionResponse createQuestion(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long quizId,
            @RequestBody TrainingQuizQuestionRequest request
    ) {
        return quizService.createQuestion(organizationId, trainingId, quizId, request);
    }

    @PutMapping("/{quizId}/questions/{questionId}")
    public TrainingQuizQuestionResponse updateQuestion(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @RequestBody TrainingQuizQuestionRequest request
    ) {
        return quizService.updateQuestion(
                organizationId,
                trainingId,
                quizId,
                questionId,
                request
        );
    }

    @DeleteMapping("/{quizId}/questions/{questionId}")
    public void deleteQuestion(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long quizId,
            @PathVariable Long questionId
    ) {
        quizService.deleteQuestion(organizationId, trainingId, quizId, questionId);
    }

    @PostMapping("/{quizId}/questions/{questionId}/options")
    public TrainingQuizOptionResponse createOption(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @RequestBody TrainingQuizOptionRequest request
    ) {
        return quizService.createOption(
                organizationId,
                trainingId,
                quizId,
                questionId,
                request
        );
    }

    @PutMapping("/{quizId}/questions/{questionId}/options/{optionId}")
    public TrainingQuizOptionResponse updateOption(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @PathVariable Long optionId,
            @RequestBody TrainingQuizOptionRequest request
    ) {
        return quizService.updateOption(
                organizationId,
                trainingId,
                quizId,
                questionId,
                optionId,
                request
        );
    }

    @DeleteMapping("/{quizId}/questions/{questionId}/options/{optionId}")
    public void deleteOption(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @PathVariable Long optionId
    ) {
        quizService.deleteOption(
                organizationId,
                trainingId,
                quizId,
                questionId,
                optionId
        );
    }
}
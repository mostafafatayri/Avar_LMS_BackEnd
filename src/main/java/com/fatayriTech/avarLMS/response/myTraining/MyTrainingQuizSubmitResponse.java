package com.fatayriTech.avarLMS.response.myTraining;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyTrainingQuizSubmitResponse {

    private Integer score;
    private Boolean passed;
    private Integer passingScore;

    private Integer correctAnswers;
    private Integer totalQuestions;

    private Integer attemptNumber;
    private Integer attemptsUsed;
    private Integer attemptsRemaining;
    private Integer maxAttempts;
    private Boolean canRetry;

    private Boolean timeExpired;

    private String status;
    private String message;
}
package com.fatayriTech.avarLMS.response.myTraining;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyTrainingQuizResponse {
    private Long moduleItemId;
    private Long quizId;
    private String title;
    private String description;
    private Integer passingScore;
    private Integer timeLimitMinutes;
    private Integer maxAttempts;
    private List<MyTrainingQuizQuestionResponse> questions;
    ///
    private Integer attemptsUsed;
    private Integer attemptsRemaining;
    private Boolean canAttempt;
    private Boolean alreadyPassed;
    private Boolean blocked;
    private Integer lastScore;
    private Boolean lastPassed;
    private String attemptMessage;
}
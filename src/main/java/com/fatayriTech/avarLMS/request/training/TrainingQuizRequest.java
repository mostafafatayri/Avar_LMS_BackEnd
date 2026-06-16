package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingQuizRequest {
    private String title;
    private String description;
    private Integer passingScore;
    private Integer timeLimitMinutes;
    private Integer maxAttempts;
    private Boolean shuffleQuestions;
}
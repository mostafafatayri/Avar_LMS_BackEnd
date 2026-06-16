package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TrainingQuizResponse {
    private Long id;
    private Long organizationId;
    private Long trainingCatalogueId;
    private String title;
    private String description;
    private Integer passingScore;
    private Integer timeLimitMinutes;
    private Integer maxAttempts;
    private Boolean shuffleQuestions;
    private Boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private List<TrainingQuizQuestionResponse> questions;
}
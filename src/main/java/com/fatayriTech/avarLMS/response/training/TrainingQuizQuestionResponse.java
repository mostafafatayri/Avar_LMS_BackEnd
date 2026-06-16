package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TrainingQuizQuestionResponse {
    private Long id;
    private Long organizationId;
    private Long quizId;
    private String questionText;
    private String questionType;
    private Integer points;
    private Integer displayOrder;
    private Boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private List<TrainingQuizOptionResponse> options;
}
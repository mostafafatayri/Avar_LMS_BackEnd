package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingFeedbackResponse {

    private Long id;
    private Long organizationId;
    private Long trainingCatalogueId;
    private Long userId;
    private Long employeeId;
    private Integer rating;
    private String comment;
    private Boolean anonymous;
    private Boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
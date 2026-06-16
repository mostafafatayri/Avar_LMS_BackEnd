package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingLearningObjectiveResponse {
    private Long id;
    private Long organizationId;
    private Long trainingCatalogueId;
    private String objectiveText;
    private Integer displayOrder;
    private Boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
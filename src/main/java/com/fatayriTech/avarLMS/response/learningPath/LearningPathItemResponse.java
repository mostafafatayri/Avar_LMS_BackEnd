package com.fatayriTech.avarLMS.response.learningPath;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LearningPathItemResponse {

    private Long id;

    private Long organizationId;

    private Long learningPathId;

    private Long trainingCatalogueId;

    private String trainingTitle;

    private String trainingCode;

    private String trainingType;

    private Integer displayOrder;

    private Boolean mandatory;

    private Boolean lockUntilPreviousCompleted;

    private Boolean active;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;
}
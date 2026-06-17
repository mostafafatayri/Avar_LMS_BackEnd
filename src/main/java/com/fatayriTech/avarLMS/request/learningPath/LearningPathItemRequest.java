package com.fatayriTech.avarLMS.request.learningPath;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningPathItemRequest {

    private Long trainingCatalogueId;

    private Integer displayOrder;

    private Boolean mandatory;

    private Boolean lockUntilPreviousCompleted;
}
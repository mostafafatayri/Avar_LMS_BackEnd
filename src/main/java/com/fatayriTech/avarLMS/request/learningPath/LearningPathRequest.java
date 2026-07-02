package com.fatayriTech.avarLMS.request.learningPath;

import com.fatayriTech.avarLMS.enums.LearningPathCompletionType;
import com.fatayriTech.avarLMS.enums.LearningPathModule;
import com.fatayriTech.avarLMS.enums.LearningPathStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningPathRequest {

    private LearningPathModule module;

    private String name;

    private String description;

    private Integer durationDays;

    private LearningPathCompletionType completionType;

    private Integer completionPercentage;

    private Integer completionCount;

    private Boolean lockingEnabled;

    private LearningPathStatus status;

    private Boolean approvalRequired;

    private Long parentLearningPathId;


    private Integer displayOrder;
}
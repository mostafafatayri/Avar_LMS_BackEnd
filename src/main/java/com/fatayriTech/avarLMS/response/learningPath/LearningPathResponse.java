package com.fatayriTech.avarLMS.response.learningPath;

import com.fatayriTech.avarLMS.enums.LearningPathCompletionType;
import com.fatayriTech.avarLMS.enums.LearningPathModule;
import com.fatayriTech.avarLMS.enums.LearningPathStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LearningPathResponse {

    private Long id;

    private Long organizationId;

    private String name;

    private String description;

    private Integer durationDays;

    private String completionRequirement;



    private Boolean approvalRequired;

    private Boolean active;

    private Long trainingCount;

    private Long assignmentCount;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    private List<LearningPathItemResponse> items;

    private List<LearningPathAssignmentResponse> assignments;

    private Long parentLearningPathId;
    private String parentLearningPathName;
    private Long subPathCount;
    private LearningPathModule module;
    private LearningPathCompletionType completionType;
    private Integer completionPercentage;
    private Integer completionCount;
    private Boolean lockingEnabled;
    private LearningPathStatus status;


    private Integer displayOrder;
}
package com.fatayriTech.avarLMS.response.learningPath;

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

    private String status;

    private Boolean approvalRequired;

    private Boolean active;

    private Long trainingCount;

    private Long assignmentCount;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    private List<LearningPathItemResponse> items;

    private List<LearningPathAssignmentResponse> assignments;
}
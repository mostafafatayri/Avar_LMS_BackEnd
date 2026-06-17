package com.fatayriTech.avarLMS.request.learningPath;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningPathRequest {

    private String name;

    private String description;

    private Integer durationDays;

    private String completionRequirement;

    private String status;

    private Boolean approvalRequired;
}
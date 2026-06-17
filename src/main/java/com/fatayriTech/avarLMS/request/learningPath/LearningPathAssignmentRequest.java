package com.fatayriTech.avarLMS.request.learningPath;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningPathAssignmentRequest {

    private String assignmentType;

    private Long targetId;
}
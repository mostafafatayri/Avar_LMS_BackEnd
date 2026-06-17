package com.fatayriTech.avarLMS.response.learningPath;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LearningPathAssignmentResponse {

    private Long id;

    private Long organizationId;

    private Long learningPathId;

    private String assignmentType;

    private Long targetId;

    private String status;

    private Boolean active;

    private LocalDateTime assignedDate;

    private LocalDateTime completedDate;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;
}
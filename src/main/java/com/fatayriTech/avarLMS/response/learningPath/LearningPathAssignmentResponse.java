package com.fatayriTech.avarLMS.response.learningPath;

import com.fatayriTech.avarLMS.enums.LearningPathAssignmentStatus;
import com.fatayriTech.avarLMS.enums.LearningPathAssignmentTargetType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LearningPathAssignmentResponse {

    private Long id;

    private Long organizationId;

    private Long learningPathId;
    private String learningPathName;

    private LearningPathAssignmentTargetType targetType;
    private Long targetId;
    private String targetName;

    private Long assignedBy;

    private Integer validityDays;
    private LocalDate expiryDate;

    private LearningPathAssignmentStatus status;

    private Integer progressPercentage;

    private LocalDateTime assignedDate;

    private LocalDateTime completionDate;

    private Boolean active;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;
}
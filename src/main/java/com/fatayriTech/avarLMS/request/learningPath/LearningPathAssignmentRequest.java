package com.fatayriTech.avarLMS.request.learningPath;

import com.fatayriTech.avarLMS.enums.LearningPathAssignmentTargetType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LearningPathAssignmentRequest {

    private LearningPathAssignmentTargetType targetType;

    private Long targetId;

    private LocalDate dueDate;
}
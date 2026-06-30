package com.fatayriTech.avarLMS.request.learningPath;

import com.fatayriTech.avarLMS.enums.AcademyStatus;
import com.fatayriTech.avarLMS.enums.AssignmentBatchTargetType;
import com.fatayriTech.avarLMS.enums.EmployeeType;
import com.fatayriTech.avarLMS.enums.LearningPathAssignmentTargetType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LearningPathAssignmentRequest {

    private LearningPathAssignmentTargetType targetType;

    private Long targetId;

    private List<Long> employeeIds;

    private AssignmentBatchTargetType batchTargetType;

    private EmployeeType employeeType;

    private AcademyStatus academyStatus;

    private Integer validityDays;
}
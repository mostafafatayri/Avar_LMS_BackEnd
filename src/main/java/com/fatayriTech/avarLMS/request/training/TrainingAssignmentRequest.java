package com.fatayriTech.avarLMS.request.training;

import com.fatayriTech.avarLMS.enums.AcademyStatus;
import com.fatayriTech.avarLMS.enums.AssignmentBatchTargetType;
import com.fatayriTech.avarLMS.enums.EmployeeType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrainingAssignmentRequest {

    private Long employeeId;

    private List<Long> employeeIds;

    private AssignmentBatchTargetType batchTargetType;

    private EmployeeType employeeType;

    private AcademyStatus academyStatus;

    private Long trainingCatalogueId;

    private Integer validityDays;
    private Boolean assignmentRequired;
}
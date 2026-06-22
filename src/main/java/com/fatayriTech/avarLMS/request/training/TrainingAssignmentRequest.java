package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingAssignmentRequest {

    private Long employeeId;

    private Long trainingCatalogueId;

    private Integer validityDays;
}
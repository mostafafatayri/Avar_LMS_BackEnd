package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainingAssignmentRequest {

    private Long employeeId;

    private Long trainingCatalogueId;

    private LocalDate dueDate;
}
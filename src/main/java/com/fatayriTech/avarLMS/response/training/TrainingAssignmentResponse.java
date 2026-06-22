package com.fatayriTech.avarLMS.response.training;

import com.fatayriTech.avarLMS.enums.TrainingAssignmentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TrainingAssignmentResponse {

    private Long id;
    private Long organizationId;

    private Long employeeId;
    private String employeeName;
    private String employeeEmail;

    private Long trainingCatalogueId;
    private String trainingTitle;
    private String trainingCode;
    private String trainingType;

    private Long assignedBy;

    private Integer validityDays;
    private LocalDate expiryDate;

    private TrainingAssignmentStatus status;
    private Integer progressPercentage;

    private LocalDateTime assignedDate;
    private LocalDateTime completionDate;

    private Boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingEnrollmentResponse {

    private Long id;
    private Long organizationId;
    private Long trainingCatalogueId;
    private Long userId;
    private Long employeeId;
    private String status;
    private Boolean active;
    private LocalDateTime enrolledDate;
    private LocalDateTime completedDate;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
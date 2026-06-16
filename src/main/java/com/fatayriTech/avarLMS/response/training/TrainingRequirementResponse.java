package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingRequirementResponse {

    private Long id;

    private Long organizationId;

    private Long trainingCatalogueId;

    private String title;

    private String description;

    private String requirementType;

    private String requirementValue;

    private Boolean mandatory;

    private Integer displayOrder;

    private Boolean active;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;
}
package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingRequirementRequest {

    private String title;

    private String description;

    private String requirementType;

    private String requirementValue;

    private Boolean mandatory;

    private Integer displayOrder;
}
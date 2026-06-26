package com.fatayriTech.avarLMS.response.myTraining;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyTrainingDetailsResponse {

    private Long trainingCatalogueId;

    private String title;
    private String description;
    private String trainingType;
    private String trainer;
    private String trainerEmail;

    private Integer durationHours;
    private Integer validityMonths;
    private Boolean certificateEnabled;

    private Integer totalItems;
    private Integer completedItems;
    private Integer progressPercentage;

    private List<MyTrainingModuleResponse> modules;
}
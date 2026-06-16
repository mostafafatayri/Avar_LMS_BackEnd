package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingCatalogueRequest {

    private String title;
    private String description;
    private String trainingType;
    private String status;
    private Integer durationHours;
    private Integer validityMonths;
    private Integer passingScore;
    private String trainer;
    private String trainerEmail;
    private String materialUrl;
    private Boolean hasLiveSession;
    private Boolean certificateEnabled;
    private Boolean mandatory;
}
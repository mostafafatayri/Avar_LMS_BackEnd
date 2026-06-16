package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingCatalogueResponse {

    private Long id;
    private Long organizationId;
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
    private Boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private String materialFileName;
    private String materialContentType;
    private Long materialSize;
}
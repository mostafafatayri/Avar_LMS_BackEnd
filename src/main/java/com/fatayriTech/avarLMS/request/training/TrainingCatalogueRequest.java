package com.fatayriTech.avarLMS.request.training;

import com.fatayriTech.avarLMS.enums.TrainingCatalogueStatus;
import com.fatayriTech.avarLMS.enums.TrainingModule;
import com.fatayriTech.avarLMS.enums.TrainingType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TrainingCatalogueRequest {

    private TrainingModule module;

    private String title;
    private String description;

    private TrainingType trainingType;
    private TrainingCatalogueStatus status;

    private Integer durationHours;
    private Integer validityMonths;
    private Integer passingScore;
    private Integer kpiWeightPercentage;

    private Long trainerEmployeeId;
    private String trainer;
    private String trainerEmail;

    private String materialUrl;

    private Boolean hasLiveSession;
    private LocalDateTime liveSessionDateTime;
    private String meetingLink;
    private String recordingUrl;
    private String recordingAccess;

    private Boolean certificateEnabled;
    private Boolean refresher;
    private Boolean assessment;
    private Boolean approval;

    private Boolean autoRenew;
    private Integer renewalLeadTimeDays;
}
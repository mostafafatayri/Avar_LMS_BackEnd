package com.fatayriTech.avarLMS.response.training;

import com.fatayriTech.avarLMS.enums.TrainingCatalogueStatus;
import com.fatayriTech.avarLMS.enums.TrainingModule;
import com.fatayriTech.avarLMS.enums.TrainingType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingCatalogueResponse {

    private Long id;
    private Long organizationId;

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
    private String materialFileName;
    private String materialContentType;
    private Long materialSize;

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

    private Boolean active;
    private String joinToken;
    private String joinUrl;
    private LocalDateTime joinUrlGeneratedAt;
    private Long joinUrlGeneratedBy;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
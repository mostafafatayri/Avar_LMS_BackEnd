package com.fatayriTech.avarLMS.model;

import com.fatayriTech.avarLMS.enums.TrainingCatalogueStatus;
import com.fatayriTech.avarLMS.enums.TrainingModule;
import com.fatayriTech.avarLMS.enums.TrainingType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_catalogues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingCatalogue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    private TrainingModule module;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TrainingType trainingType;

    @Enumerated(EnumType.STRING)
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

    private Boolean hasLiveSession = false;
    private LocalDateTime liveSessionDateTime;
    private String meetingLink;
    private String recordingUrl;
    private String recordingAccess;

    private Boolean certificateEnabled = false;
    private Boolean refresher = false;
    private Boolean assessment = false;
    private Boolean approval = false;

    private Boolean autoRenew = false;
    private Integer renewalLeadTimeDays;

    private Boolean active = true;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    private String joinToken;

    @Column(length = 1000)
    private String joinUrl;

    private LocalDateTime joinUrlGeneratedAt;

    private Long joinUrlGeneratedBy;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (module == null) module = TrainingModule.L_AND_D;
        if (trainingType == null) trainingType = TrainingType.ONLINE;
        if (status == null) status = TrainingCatalogueStatus.DRAFT;
        if (active == null) active = true;

        if (hasLiveSession == null) hasLiveSession = false;
        if (certificateEnabled == null) certificateEnabled = false;
        if (refresher == null) refresher = false;
        if (assessment == null) assessment = false;
        if (approval == null) approval = false;
        if (autoRenew == null) autoRenew = false;
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
package com.fatayriTech.avarLMS.model;

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

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String trainingType; // Online, Classroom, Blended

    private String status; // Draft, Published, Archived

    private Integer durationHours;

    private Integer validityMonths;

    private Integer passingScore;

    private String trainer;

    private String trainerEmail;

    private String materialUrl;

    private Boolean hasLiveSession = false;

    private Boolean certificateEnabled = false;

    private Boolean mandatory = false;

    private Boolean active = true;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    private String materialFileName;
    private String materialContentType;
    private Long materialSize;



    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (active == null) active = true;
        if (hasLiveSession == null) hasLiveSession = false;
        if (certificateEnabled == null) certificateEnabled = false;
        if (mandatory == null) mandatory = false;
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
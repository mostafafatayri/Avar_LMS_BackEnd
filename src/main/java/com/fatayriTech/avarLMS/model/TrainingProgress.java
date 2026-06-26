package com.fatayriTech.avarLMS.model;

import com.fatayriTech.avarLMS.enums.TrainingDisplayItemType;
import com.fatayriTech.avarLMS.enums.TrainingProgressStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long organizationId;

    private Long userId;

    private Long employeeId;

    private Long trainingCatalogueId;

    private Long moduleId;

    private Long moduleItemId;

    @Enumerated(EnumType.STRING)
    private TrainingDisplayItemType itemType;

    private Long itemRefId;

    @Enumerated(EnumType.STRING)
    private TrainingProgressStatus status;

    private Integer progressPercentage;

    private Integer score;

    private Boolean passed;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (status == null) status = TrainingProgressStatus.NOT_STARTED;
        if (progressPercentage == null) progressPercentage = 0;
        if (passed == null) passed = false;
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
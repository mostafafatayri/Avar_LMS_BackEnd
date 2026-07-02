package com.fatayriTech.avarLMS.model;

import com.fatayriTech.avarLMS.enums.LearningPathCompletionType;
import com.fatayriTech.avarLMS.enums.LearningPathModule;
import com.fatayriTech.avarLMS.enums.LearningPathStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_paths")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    private LearningPathModule module;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_days")
    private Integer durationDays = 0;

    @Enumerated(EnumType.STRING)
    private LearningPathCompletionType completionType;

    private Integer completionPercentage;

    private Integer completionCount;

    private Boolean lockingEnabled = false;

    @Column(name = "completion_requirement")
    private String completionRequirement;

    @Enumerated(EnumType.STRING)
    private LearningPathStatus status;

    @Column(name = "approval_required")
    private Boolean approvalRequired = false;

    private Boolean active = true;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_learning_path_id")
    private LearningPath parentLearningPath;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (module == null) module = LearningPathModule.L_AND_D;
        if (completionType == null) completionType = LearningPathCompletionType.PERCENTAGE;
        if (completionPercentage == null) completionPercentage = 100;
        if (lockingEnabled == null) lockingEnabled = false;
        if (active == null) active = true;
        if (approvalRequired == null) approvalRequired = false;
        if (durationDays == null) durationDays = 0;
        if (status == null) status = LearningPathStatus.DRAFT;
        if (displayOrder == null) displayOrder = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
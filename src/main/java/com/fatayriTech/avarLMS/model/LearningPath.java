package com.fatayriTech.avarLMS.model;

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

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_days")
    private Integer durationDays = 0;

    @Column(name = "completion_requirement")
    private String completionRequirement; // ANY_TRAINING, ALL_TRAININGS, SEQUENTIAL

    private String status; // DRAFT, PUBLISHED, ARCHIVED

    @Column(name = "approval_required")
    private Boolean approvalRequired = false;

    private Boolean active = true;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (active == null) active = true;
        if (approvalRequired == null) approvalRequired = false;
        if (durationDays == null) durationDays = 0;
        if (status == null) status = "DRAFT";
        if (completionRequirement == null) completionRequirement = "ALL_TRAININGS";
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
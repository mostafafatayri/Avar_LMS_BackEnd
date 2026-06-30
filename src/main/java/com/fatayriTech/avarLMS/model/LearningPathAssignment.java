package com.fatayriTech.avarLMS.model;

import com.fatayriTech.avarLMS.enums.LearningPathAssignmentStatus;
import com.fatayriTech.avarLMS.enums.LearningPathAssignmentTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_path_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPathAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @Enumerated(EnumType.STRING)
    private LearningPathAssignmentTargetType targetType;

    private Long targetId;

    private Long assignedBy;

    private Integer validityDays;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private LearningPathAssignmentStatus status;

    private Integer progressPercentage;

    private LocalDateTime assignedDate;

    private LocalDateTime completionDate;

    private Boolean active;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.creationDate = now;
        this.modificationDate = now;

        if (this.assignedDate == null) {
            this.assignedDate = now;
        }

        if (this.validityDays != null && this.expiryDate == null) {
            this.expiryDate = this.assignedDate.toLocalDate().plusDays(this.validityDays);
        }

        if (this.status == null) {
            this.status = LearningPathAssignmentStatus.NOT_STARTED;
        }

        if (this.progressPercentage == null) {
            this.progressPercentage = 0;
        }

        if (this.active == null) {
            this.active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.modificationDate = LocalDateTime.now();

        if (this.validityDays != null && this.assignedDate != null) {
            this.expiryDate = this.assignedDate.toLocalDate().plusDays(this.validityDays);
        }
    }
}
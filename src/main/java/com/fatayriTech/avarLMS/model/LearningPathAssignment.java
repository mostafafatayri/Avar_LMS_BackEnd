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

    private LocalDate dueDate;

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
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (assignedDate == null) assignedDate = LocalDateTime.now();
        if (status == null) status = LearningPathAssignmentStatus.ASSIGNED;
        if (progressPercentage == null) progressPercentage = 0;
        if (active == null) active = true;
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
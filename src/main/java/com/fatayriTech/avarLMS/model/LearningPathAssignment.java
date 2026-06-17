package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @Column(name = "assignment_type", nullable = false)
    private String assignmentType; // EMPLOYEE, DEPARTMENT, ROLE

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    private String status; // ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED

    private Boolean active = true;

    private LocalDateTime assignedDate;

    private LocalDateTime completedDate;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (active == null) active = true;
        if (status == null) status = "ASSIGNED";
        if (assignedDate == null) assignedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
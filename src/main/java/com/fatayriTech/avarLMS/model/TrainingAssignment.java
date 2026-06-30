package com.fatayriTech.avarLMS.model;

import com.fatayriTech.avarLMS.enums.TrainingAssignmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "training_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_catalogue_id", nullable = false)
    private TrainingCatalogue trainingCatalogue;

    private Long assignedBy;

    // OLD: keep temporarily if existing code still uses it
    // Later we can remove dueDate fully.
    private LocalDate dueDate;

    // NEW: user enters number of days, system calculates expiryDate
    private Integer validityDays;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private TrainingAssignmentStatus status;

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
            this.status = TrainingAssignmentStatus.NOT_STARTED;
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
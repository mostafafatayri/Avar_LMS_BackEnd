package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "training_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"organization_id", "training_catalogue_id", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_catalogue_id", nullable = false)
    private TrainingCatalogue trainingCatalogue;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(nullable = false)
    private String status; // ENROLLED, IN_PROGRESS, COMPLETED, CANCELLED

    private Boolean active = true;

    private LocalDateTime enrolledDate;

    private LocalDateTime completedDate;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (active == null) active = true;
        if (status == null) status = "ENROLLED";
        if (enrolledDate == null) enrolledDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
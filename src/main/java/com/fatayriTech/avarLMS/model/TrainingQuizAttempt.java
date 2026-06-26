package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_quiz_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingQuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long organizationId;

    private Long userId;

    private Long employeeId;

    private Long trainingCatalogueId;

    private Long moduleId;

    private Long moduleItemId;

    private Long quizId;

    private Integer attemptNumber;

    private Integer score;

    private Boolean passed;

    private LocalDateTime submittedAt;

    private Boolean timeExpired;

    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();

        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }

        if (passed == null) {
            passed = false;
        }

        if (timeExpired == null) {
            timeExpired = false;
        }
    }
}
package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_catalogue_id", nullable = false)
    private TrainingCatalogue trainingCatalogue;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer passingScore = 70;

    private Integer timeLimitMinutes = 0;

    private Integer maxAttempts = 1;

    private Boolean shuffleQuestions = false;

    private Boolean active = true;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (active == null) active = true;
        if (passingScore == null) passingScore = 70;
        if (timeLimitMinutes == null) timeLimitMinutes = 0;
        if (maxAttempts == null) maxAttempts = 1;
        if (shuffleQuestions == null) shuffleQuestions = false;
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
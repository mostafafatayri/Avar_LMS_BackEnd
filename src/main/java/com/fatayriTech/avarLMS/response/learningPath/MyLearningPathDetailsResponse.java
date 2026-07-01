package com.fatayriTech.avarLMS.response.learningPath;

import com.fatayriTech.avarLMS.enums.LearningPathAssignmentStatus;
import com.fatayriTech.avarLMS.enums.TrainingAssignmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MyLearningPathDetailsResponse {

    private Long assignmentId;
    private Long learningPathId;

    private String title;
    private String description;
    private String type;

    private String assignedVia;
    private LocalDate expiryDate;
    private Integer validityDays;
    private LearningPathAssignmentStatus status;

    private Integer progress;
    private Integer completedTrainings;
    private Integer inProgressTrainings;
    private Integer remainingTrainings;
    private Integer totalTrainings;

    private String totalDuration;
    private Boolean assignmentRequired;
    private Boolean certificateEnabled;

    private TrainingItem nextTraining;

    private List<TrainingItem> trainings;

    @Getter
    @Builder
    public static class TrainingItem {
        private Long learningPathItemId;
        private Long trainingId;
        private Long trainingAssignmentId;

        private Integer step;
        private String title;
        private String type;
        private String duration;

        private TrainingAssignmentStatus status;
        private Integer progress;

        private Boolean unlocked;
        private Boolean locked;
        private Boolean mandatory;
        private String lockReason;
    }
}
package com.fatayriTech.avarLMS.response.myTraining;

import com.fatayriTech.avarLMS.enums.TrainingDisplayItemType;
import com.fatayriTech.avarLMS.enums.TrainingProgressStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyTrainingProgressResponse {

    private Long id;

    private Long organizationId;

    private Long userId;

    private Long employeeId;

    private Long trainingCatalogueId;

    private Long moduleId;

    private Long moduleItemId;

    private TrainingDisplayItemType itemType;

    private Long itemRefId;

    private TrainingProgressStatus status;

    private Integer progressPercentage;

    private Integer score;

    private Boolean passed;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;
}
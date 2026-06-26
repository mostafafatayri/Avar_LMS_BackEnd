package com.fatayriTech.avarLMS.request.training;

import com.fatayriTech.avarLMS.enums.TrainingProgressStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkTrainingItemProgressRequest {

    private TrainingProgressStatus status;

    private Integer progressPercentage;

    private Integer score;

    private Boolean passed;
}
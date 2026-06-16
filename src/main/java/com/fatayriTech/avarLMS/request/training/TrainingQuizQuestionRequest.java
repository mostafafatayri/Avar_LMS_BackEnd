package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingQuizQuestionRequest {
    private String questionText;
    private String questionType;
    private Integer points;
    private Integer displayOrder;
}
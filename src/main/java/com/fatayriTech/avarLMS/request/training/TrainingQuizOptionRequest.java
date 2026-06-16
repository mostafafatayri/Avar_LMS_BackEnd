package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingQuizOptionRequest {
    private String optionText;
    private Boolean correct;
    private Integer displayOrder;
}
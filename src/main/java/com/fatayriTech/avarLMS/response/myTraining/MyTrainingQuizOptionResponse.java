package com.fatayriTech.avarLMS.response.myTraining;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyTrainingQuizOptionResponse {
    private Long id;
    private Long questionId;
    private String optionText;
    private Integer displayOrder;
}
package com.fatayriTech.avarLMS.response.myTraining;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyTrainingQuizQuestionResponse {
    private Long id;
    private String questionText;
    private String questionType;
    private Integer points;
    private Integer displayOrder;
    private List<MyTrainingQuizOptionResponse> options;
}
package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmitMyTrainingQuizRequest {

    private Boolean timeExpired;

    private List<Answer> answers;

    @Getter
    @Setter
    public static class Answer {
        private Long questionId;
        private List<Long> selectedOptionIds;
    }
}
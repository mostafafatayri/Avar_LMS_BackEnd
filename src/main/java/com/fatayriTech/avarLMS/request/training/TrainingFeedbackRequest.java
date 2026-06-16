package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingFeedbackRequest {
    private Integer rating;
    private String comment;
    private Boolean anonymous;
}
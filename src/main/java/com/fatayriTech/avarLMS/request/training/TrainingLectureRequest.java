package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingLectureRequest {
    private String title;
    private String description;
    private Integer displayOrder;
}
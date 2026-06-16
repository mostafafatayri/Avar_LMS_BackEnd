package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingVideoRequest {
    private String title;
    private String description;
    private String videoType;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer durationMinutes;
    private Integer displayOrder;
}
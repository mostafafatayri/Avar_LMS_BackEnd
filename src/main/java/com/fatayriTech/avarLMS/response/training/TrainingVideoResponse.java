package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingVideoResponse {
    private Long id;
    private Long organizationId;
    private Long trainingCatalogueId;
    private String title;
    private String description;
    private String videoType;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer durationMinutes;
    private Integer displayOrder;
    private Boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
package com.fatayriTech.avarLMS.response.myTraining;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class MyTrainingResponse {
    private String id;
    private Long rawId;

    private String type;

    private Long learningPathId;
    private Long trainingCatalogueId;

    private String title;
    private String subtitle;
    private String assignedVia;

    private Integer validityDays;
    private LocalDate expiryDate;

    private String status;
    private Integer progressPercentage;
}
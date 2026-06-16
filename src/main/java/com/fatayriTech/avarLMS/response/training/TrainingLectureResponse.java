package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TrainingLectureResponse {
    private Long id;
    private Long organizationId;
    private Long trainingCatalogueId;
    private String title;
    private String description;
    private Integer displayOrder;
    private Boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private List<TrainingLectureAttachmentResponse> attachments;
}
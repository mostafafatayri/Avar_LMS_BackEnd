package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingQuizOptionResponse {
    private Long id;
    private Long organizationId;
    private Long questionId;
    private String optionText;
    private Boolean correct;
    private Integer displayOrder;
    private Boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
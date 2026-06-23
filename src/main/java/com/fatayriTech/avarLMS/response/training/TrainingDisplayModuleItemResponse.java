package com.fatayriTech.avarLMS.response.training;

import com.fatayriTech.avarLMS.enums.TrainingDisplayItemType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingDisplayModuleItemResponse {

    private Long id;

    private Long organizationId;

    private Long moduleId;

    private TrainingDisplayItemType itemType;

    private Long itemRefId;

    private String itemTitle;

    private String itemDescription;

    private Integer displayOrder;

    private Boolean required;

    private Boolean active;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;
}
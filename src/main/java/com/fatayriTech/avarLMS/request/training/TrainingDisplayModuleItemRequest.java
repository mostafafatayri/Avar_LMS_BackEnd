package com.fatayriTech.avarLMS.request.training;

import com.fatayriTech.avarLMS.enums.TrainingDisplayItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingDisplayModuleItemRequest {

    private Long id;

    private TrainingDisplayItemType itemType;

    private Long itemRefId;

    private Integer displayOrder;

    private Boolean required;
}
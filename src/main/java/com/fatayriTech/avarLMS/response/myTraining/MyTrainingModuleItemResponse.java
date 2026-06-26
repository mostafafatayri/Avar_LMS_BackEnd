package com.fatayriTech.avarLMS.response.myTraining;

import com.fatayriTech.avarLMS.enums.TrainingDisplayItemType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyTrainingModuleItemResponse {

    private Long id;
    private Long moduleId;

    private TrainingDisplayItemType itemType;
    private Long itemRefId;

    private String itemTitle;
    private String itemDescription;

    private Integer displayOrder;
    private Boolean required;

    private String progressStatus;
    private Integer progressPercentage;
    private Boolean completed;
    private Boolean locked;
}
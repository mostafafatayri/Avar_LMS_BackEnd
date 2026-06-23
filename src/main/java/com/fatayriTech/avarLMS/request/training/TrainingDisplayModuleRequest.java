package com.fatayriTech.avarLMS.request.training;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrainingDisplayModuleRequest {

    private Long id;

    private String title;

    private String description;

    private Integer displayOrder;

    private List<TrainingDisplayModuleItemRequest> items;
}
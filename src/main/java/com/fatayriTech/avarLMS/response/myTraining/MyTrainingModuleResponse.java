package com.fatayriTech.avarLMS.response.myTraining;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyTrainingModuleResponse {

    private Long id;
    private String title;
    private String description;
    private Integer displayOrder;

    private List<MyTrainingModuleItemResponse> items;
}
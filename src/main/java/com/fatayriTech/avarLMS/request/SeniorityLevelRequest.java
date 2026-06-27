package com.fatayriTech.avarLMS.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeniorityLevelRequest {

    private String name;

    private Integer displayOrder;

    private String description;

    private Boolean active;
}
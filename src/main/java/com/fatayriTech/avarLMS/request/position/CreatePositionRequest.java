package com.fatayriTech.avarLMS.request.position;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePositionRequest {

    private String code;

    private String name;

    private String description;

    private Long departmentId;
}
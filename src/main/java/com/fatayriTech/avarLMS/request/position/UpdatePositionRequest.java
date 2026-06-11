package com.fatayriTech.avarLMS.request.position;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePositionRequest {

    private String code;

    private String name;

    private String description;

    private boolean active;

    private Long departmentId;
}
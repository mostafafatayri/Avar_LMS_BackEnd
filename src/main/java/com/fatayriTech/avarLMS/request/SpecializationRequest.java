package com.fatayriTech.avarLMS.request;

import lombok.Data;

@Data
public class SpecializationRequest {
    private String name;
    private Long departmentId;
    private Long subTeamId;
    private String description;
    private Boolean active;
}
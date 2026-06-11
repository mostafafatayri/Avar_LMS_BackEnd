package com.fatayriTech.avarLMS.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpecializationResponse {
    private Long id;
    private String name;

    private Long departmentId;
    private String departmentName;

    private Long subTeamId;
    private String subTeamName;

    private String description;
    private boolean active;
}
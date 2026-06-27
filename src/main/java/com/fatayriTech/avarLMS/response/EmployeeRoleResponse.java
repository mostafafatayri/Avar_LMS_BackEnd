package com.fatayriTech.avarLMS.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeRoleResponse {
    private Long id;
    private String name;

    private Long departmentId;
    private String departmentName;

    private Long seniorityLevelId;
    private String seniorityLevelName;
    private Integer seniorityDisplayOrder;
    private String description;
    private boolean active;
}
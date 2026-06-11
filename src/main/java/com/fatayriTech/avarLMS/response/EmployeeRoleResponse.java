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

    private String seniority;
    private String description;
    private boolean active;
}
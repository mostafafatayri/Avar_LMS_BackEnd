package com.fatayriTech.avarLMS.request;

import lombok.Data;

@Data
public class EmployeeRoleRequest {
    private String name;
    private Long departmentId;
    private String seniority;
    private String description;
    private Boolean active;
}
package com.fatayriTech.avarLMS.request.Department;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDepartmentRequest {

    private String code;
    private String name;
    private String description;

    private Long headEmployeeId;
}
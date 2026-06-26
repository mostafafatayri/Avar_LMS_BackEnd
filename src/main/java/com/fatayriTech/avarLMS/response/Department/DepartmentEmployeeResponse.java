package com.fatayriTech.avarLMS.response.Department;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentEmployeeResponse {

    private Long id;

    private String employeeId;

    private String fullName;

    private String email;

    private String position;

    private String subTeam;

    private boolean active;
}
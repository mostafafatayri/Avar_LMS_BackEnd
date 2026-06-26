package com.fatayriTech.avarLMS.response.Department;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentSummaryResponse {

    private Integer employeeCount;

    private Integer subTeamCount;

    private Integer jobTitleCount;
}
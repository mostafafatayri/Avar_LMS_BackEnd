package com.fatayriTech.avarLMS.response.Department;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDistributionResponse {

    private Integer departmentEmployees;

    private Integer totalCompanyEmployees;

    private Double departmentPercentage;

    private Double otherPercentage;
}
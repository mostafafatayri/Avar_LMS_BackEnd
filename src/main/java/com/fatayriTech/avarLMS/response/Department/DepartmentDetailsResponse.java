package com.fatayriTech.avarLMS.response.Department;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DepartmentDetailsResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private boolean active;

    private LocalDateTime creationDate;

    private LocalDateTime modifiedDate;

    // Head
    private Long headId;
    private String headName;
    private String headEmail;
    private String headPosition;

    // Cards
    private DepartmentSummaryResponse summary;

    // Distribution
    private EmployeeDistributionResponse distribution;

    // Tabs
    private List<DepartmentSubTeamResponse> subTeams;

    private List<DepartmentJobTitleResponse> jobTitles;

    private List<DepartmentEmployeeResponse> employees;
}
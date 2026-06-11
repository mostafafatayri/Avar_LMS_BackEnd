package com.fatayriTech.avarLMS.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubTeamResponse {
    private Long id;
    private String name;

    private Long departmentId;
    private String departmentName;

    private Long leadEmployeeId;
    private String leadEmployeeName;
    private String leadEmployeeEmail;

    private String description;
    private boolean active;
}
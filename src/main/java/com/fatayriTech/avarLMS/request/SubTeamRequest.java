package com.fatayriTech.avarLMS.request;



import lombok.Data;

@Data
public class SubTeamRequest {
    private String name;
    private Long departmentId;
    private Long leadEmployeeId;
    private String description;
    private Boolean active;
}
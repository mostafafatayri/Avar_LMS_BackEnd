package com.fatayriTech.avarLMS.response.employee;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EmployeeCompanyInfoResponse {

    private Long systemId;
    private String employeeId;

    private Long departmentId;
    private String departmentName;

    private Long positionId;
    private String jobTitle;

    private Long managerId;
    private String managerName;

    private Long locationId;
    private String locationName;

    private Long organizationId;
    private String organizationName;

    private String subTeamName;
    private String specializationName;

    private String employmentStatus;

    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;
}
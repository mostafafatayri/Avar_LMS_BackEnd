package com.fatayriTech.avarLMS.request.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DashboardFilterRequest {

    private Long departmentId;
    private Long roleId;
    private Long trainingId;

    private String status;

    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;

    private LocalDate completionDateFrom;
    private LocalDate completionDateTo;
    private Long locationId;
    private String academyStatus;
    private String module;
}
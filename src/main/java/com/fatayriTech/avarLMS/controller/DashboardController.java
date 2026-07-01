package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.dashboard.DashboardFilterRequest;
import com.fatayriTech.avarLMS.response.dashboard.DashboardResponse;
import com.fatayriTech.avarLMS.service.Dashboard.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("${api.prefix}/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/overview")
    public DashboardResponse getDashboard(
            @RequestHeader("X-Organization-Id") Long organizationId,

            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Long trainingId,
            @RequestParam(required = false) String status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dueDateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dueDateTo,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate completionDateFrom,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) String academyStatus,
            @RequestParam(required = false) String module,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate completionDateTo
    ) {
        DashboardFilterRequest filter = new DashboardFilterRequest();
        filter.setDepartmentId(departmentId);
        filter.setRoleId(roleId);
        filter.setTrainingId(trainingId);
        filter.setStatus(status);
        filter.setDueDateFrom(dueDateFrom);
        filter.setDueDateTo(dueDateTo);
        filter.setCompletionDateFrom(completionDateFrom);
        filter.setCompletionDateTo(completionDateTo);
        filter.setLocationId(locationId);
        filter.setAcademyStatus(academyStatus);
        filter.setModule(module);

        return dashboardService.getDashboard(organizationId, filter);
    }
}
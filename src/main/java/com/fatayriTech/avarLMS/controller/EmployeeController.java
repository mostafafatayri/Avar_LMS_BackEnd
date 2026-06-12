package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.Employees.CreateEmployeeRequest;
import com.fatayriTech.avarLMS.request.Employees.LinkEmployeeUserRequest;
import com.fatayriTech.avarLMS.request.Employees.UpdateEmployeeRequest;
import com.fatayriTech.avarLMS.response.employee.EmployeeBulkUploadResponse;
import com.fatayriTech.avarLMS.response.employee.EmployeeResponse;
import com.fatayriTech.avarLMS.service.Employee.EmployeeBulkUploadService;
import com.fatayriTech.avarLMS.service.Employee.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/employees")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeBulkUploadService employeeBulkUploadService;
    private final EmployeeService employeeService;

    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE')")
    @PostMapping
    public EmployeeResponse createEmployee(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody CreateEmployeeRequest request
    ) {
        return employeeService.createEmployee(organizationId, request);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    @GetMapping
    public List<EmployeeResponse> getAllEmployees(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return employeeService.getAllEmployees(organizationId);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return employeeService.getEmployeeById(organizationId, id);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PutMapping("/{id}")
    public EmployeeResponse updateEmployee(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestBody UpdateEmployeeRequest request
    ) {
        return employeeService.updateEmployee(organizationId, id, request);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PatchMapping("/{id}/inactive")
    public EmployeeResponse setEmployeeInactive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return employeeService.setEmployeeInactive(organizationId, id);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_BULK_UPLOAD')")
    @PostMapping("/bulk-upload")
    public EmployeeBulkUploadResponse bulkUploadEmployees(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestParam("file") MultipartFile file
    ) {
        return employeeBulkUploadService.uploadEmployees(organizationId, file);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_DELETE')")
    @DeleteMapping("/{id}")
    public void deleteEmployee(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        employeeService.deleteEmployee(organizationId, id);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PatchMapping("/{employeeId}/link-user")
    public EmployeeResponse linkEmployeeToUser(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long employeeId,
            @RequestBody LinkEmployeeUserRequest request
    ) {
        return employeeService.linkEmployeeToUser(organizationId, employeeId, request);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PatchMapping("/{employeeId}/unlink-user")
    public EmployeeResponse unlinkEmployeeUser(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long employeeId
    ) {
        return employeeService.unlinkEmployeeUser(organizationId, employeeId);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    @GetMapping("/manager/{managerId}/team")
    public List<EmployeeResponse> getEmployeesByManager(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long managerId
    ) {
        return employeeService.getEmployeesByManager(organizationId, managerId);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PostMapping("/{employeeId}/invite")
    public EmployeeResponse inviteEmployee(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long employeeId
    ) {
        return employeeService.inviteEmployee(organizationId, employeeId);
    }
}
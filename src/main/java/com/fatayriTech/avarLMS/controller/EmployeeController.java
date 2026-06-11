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
import com.fatayriTech.avarLMS.request.Employees.LinkEmployeeUserRequest;
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
    public EmployeeResponse createEmployee(@RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PutMapping("/{id}")
    public EmployeeResponse updateEmployee(
            @PathVariable Long id,
            @RequestBody UpdateEmployeeRequest request
    ) {
        return employeeService.updateEmployee(id, request);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PatchMapping("/{id}/inactive")
    public EmployeeResponse setEmployeeInactive(@PathVariable Long id) {
        return employeeService.setEmployeeInactive(id);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_BULK_UPLOAD')")
    @PostMapping("/bulk-upload")
    public EmployeeBulkUploadResponse bulkUploadEmployees(
            @RequestParam("file") MultipartFile file
    ) {
        return employeeBulkUploadService.uploadEmployees(file);
    }


    @PreAuthorize("hasAuthority('EMPLOYEE_DELETE')")
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PatchMapping("/{employeeId}/link-user")
    public EmployeeResponse linkEmployeeToUser(
            @PathVariable Long employeeId,
            @RequestBody LinkEmployeeUserRequest request
    ) {
        return employeeService.linkEmployeeToUser(employeeId, request);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PatchMapping("/{employeeId}/unlink-user")
    public EmployeeResponse unlinkEmployeeUser(@PathVariable Long employeeId) {
        return employeeService.unlinkEmployeeUser(employeeId);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    @GetMapping("/manager/{managerId}/team")
    public List<EmployeeResponse> getEmployeesByManager(@PathVariable Long managerId) {
        return employeeService.getEmployeesByManager(managerId);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PostMapping("/{employeeId}/invite")
    public EmployeeResponse inviteEmployee(@PathVariable Long employeeId) {
        return employeeService.inviteEmployee(employeeId);
    }

}
package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.Department.CreateDepartmentRequest;
import com.fatayriTech.avarLMS.request.Department.UpdateDepartmentRequest;
import com.fatayriTech.avarLMS.response.Department.DepartmentBulkUploadResponse;
import com.fatayriTech.avarLMS.response.Department.DepartmentResponse;
import com.fatayriTech.avarLMS.service.WorkStructure.DepartmentBulkUploadService;
import com.fatayriTech.avarLMS.service.WorkStructure.DepartmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/departments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentBulkUploadService departmentBulkUploadService;

    @PreAuthorize("hasAuthority('DEPARTMENT_CREATE')")
    @PostMapping
    public DepartmentResponse createDepartment(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody CreateDepartmentRequest request
    ) {
        return departmentService.createDepartment(organizationId, request);
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_VIEW')")
    @GetMapping
    public List<DepartmentResponse> getAllDepartments(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return departmentService.getAllDepartments(organizationId);
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_VIEW')")
    @GetMapping("/{id}")
    public DepartmentResponse getDepartmentById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return departmentService.getDepartmentById(organizationId, id);
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE')")
    @PutMapping("/{id}")
    public DepartmentResponse updateDepartment(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestBody UpdateDepartmentRequest request
    ) {
        return departmentService.updateDepartment(organizationId, id, request);
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_DELETE')")
    @DeleteMapping("/{id}")
    public String deleteDepartment(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        departmentService.deleteDepartment(organizationId, id);
        return "Department deleted successfully";
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE')")
    @PatchMapping("/{id}/inactive")
    public DepartmentResponse setDepartmentInactive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return departmentService.setDepartmentInactive(organizationId, id);
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE')")
    @PatchMapping("/{id}/active")
    public DepartmentResponse setDepartmentActive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return departmentService.setDepartmentActive(organizationId, id);
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_CREATE')")
    @PostMapping("/bulk-upload")
    public DepartmentBulkUploadResponse bulkUploadDepartments(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestParam("file") MultipartFile file
    ) {
        return departmentBulkUploadService.uploadDepartments(organizationId, file);
    }
}
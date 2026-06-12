package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.EmployeeRoleRequest;
import com.fatayriTech.avarLMS.response.EmployeeRoleResponse;
import com.fatayriTech.avarLMS.service.WorkStructure.EmployeeRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/employee-roles")
@RequiredArgsConstructor
public class EmployeeRoleController {

    private final EmployeeRoleService employeeRoleService;

    @GetMapping
    public List<EmployeeRoleResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return employeeRoleService.getAll(organizationId);
    }

    @GetMapping("/{id}")
    public EmployeeRoleResponse getById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return employeeRoleService.getById(organizationId, id);
    }

    @PostMapping
    public EmployeeRoleResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody EmployeeRoleRequest request
    ) {
        return employeeRoleService.create(organizationId, request);
    }

    @PutMapping("/{id}")
    public EmployeeRoleResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestBody EmployeeRoleRequest request
    ) {
        return employeeRoleService.update(organizationId, id, request);
    }

    @PatchMapping("/{id}/active")
    public EmployeeRoleResponse setActive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return employeeRoleService.setActive(organizationId, id);
    }

    @PatchMapping("/{id}/inactive")
    public EmployeeRoleResponse setInactive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return employeeRoleService.setInactive(organizationId, id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        employeeRoleService.delete(organizationId, id);
    }
}
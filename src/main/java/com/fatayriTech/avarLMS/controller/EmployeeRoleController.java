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
    public List<EmployeeRoleResponse> getAll() {
        return employeeRoleService.getAll();
    }

    @GetMapping("/{id}")
    public EmployeeRoleResponse getById(@PathVariable Long id) {
        return employeeRoleService.getById(id);
    }

    @PostMapping
    public EmployeeRoleResponse create(@RequestBody EmployeeRoleRequest request) {
        return employeeRoleService.create(request);
    }

    @PutMapping("/{id}")
    public EmployeeRoleResponse update(
            @PathVariable Long id,
            @RequestBody EmployeeRoleRequest request
    ) {
        return employeeRoleService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public EmployeeRoleResponse setActive(@PathVariable Long id) {
        return employeeRoleService.setActive(id);
    }

    @PatchMapping("/{id}/inactive")
    public EmployeeRoleResponse setInactive(@PathVariable Long id) {
        return employeeRoleService.setInactive(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeeRoleService.delete(id);
    }
}
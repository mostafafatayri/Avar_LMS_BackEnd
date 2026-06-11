package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.EmployeeRole;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.EmployeeRoleRepo;
import com.fatayriTech.avarLMS.request.EmployeeRoleRequest;
import com.fatayriTech.avarLMS.response.EmployeeRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeRoleService {

    private final EmployeeRoleRepo employeeRoleRepo;
    private final DepartmentRepo departmentRepo;

    public List<EmployeeRoleResponse> getAll() {
        return employeeRoleRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeRoleResponse getById(Long id) {
        return mapToResponse(findEmployeeRole(id));
    }

    public EmployeeRoleResponse create(EmployeeRoleRequest request) {
        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (employeeRoleRepo.existsByNameIgnoreCaseAndDepartmentId(
                request.getName(),
                request.getDepartmentId()
        )) {
            throw new RuntimeException("Employee role already exists in this department");
        }

        EmployeeRole employeeRole = new EmployeeRole();
        employeeRole.setName(request.getName());
        employeeRole.setDepartment(department);
        employeeRole.setSeniority(request.getSeniority());
        employeeRole.setDescription(request.getDescription());
        employeeRole.setActive(request.getActive() == null || request.getActive());

        return mapToResponse(employeeRoleRepo.save(employeeRole));
    }

    public EmployeeRoleResponse update(Long id, EmployeeRoleRequest request) {
        EmployeeRole employeeRole = findEmployeeRole(id);

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        employeeRole.setName(request.getName());
        employeeRole.setDepartment(department);
        employeeRole.setSeniority(request.getSeniority());
        employeeRole.setDescription(request.getDescription());

        if (request.getActive() != null) {
            employeeRole.setActive(request.getActive());
        }

        return mapToResponse(employeeRoleRepo.save(employeeRole));
    }

    public EmployeeRoleResponse setActive(Long id) {
        EmployeeRole employeeRole = findEmployeeRole(id);
        employeeRole.setActive(true);
        return mapToResponse(employeeRoleRepo.save(employeeRole));
    }

    public EmployeeRoleResponse setInactive(Long id) {
        EmployeeRole employeeRole = findEmployeeRole(id);
        employeeRole.setActive(false);
        return mapToResponse(employeeRoleRepo.save(employeeRole));
    }

    public void delete(Long id) {
        EmployeeRole employeeRole = findEmployeeRole(id);
        employeeRoleRepo.delete(employeeRole);
    }

    private EmployeeRole findEmployeeRole(Long id) {
        return employeeRoleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee role not found"));
    }

    private EmployeeRoleResponse mapToResponse(EmployeeRole employeeRole) {
        return EmployeeRoleResponse.builder()
                .id(employeeRole.getId())
                .name(employeeRole.getName())
                .departmentId(employeeRole.getDepartment().getId())
                .departmentName(employeeRole.getDepartment().getName())
                .seniority(employeeRole.getSeniority())
                .description(employeeRole.getDescription())
                .active(employeeRole.isActive())
                .build();
    }
}
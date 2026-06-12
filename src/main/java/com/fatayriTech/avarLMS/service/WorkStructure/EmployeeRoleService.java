package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.EmployeeRole;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.EmployeeRoleRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
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
    private final OrganizationRepo organizationRepo;

    public List<EmployeeRoleResponse> getAll(Long organizationId) {
        return employeeRoleRepo.findByOrganizationId(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeRoleResponse getById(
            Long organizationId,
            Long id
    ) {
        return mapToResponse(findEmployeeRole(organizationId, id));
    }

    public EmployeeRoleResponse create(
            Long organizationId,
            EmployeeRoleRequest request
    ) {

        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Department department = departmentRepo
                .findByIdAndOrganizationId(
                        request.getDepartmentId(),
                        organizationId
                )
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (employeeRoleRepo.existsByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
                request.getName(),
                request.getDepartmentId(),
                organizationId
        )) {
            throw new RuntimeException(
                    "Employee role already exists in this department"
            );
        }

        EmployeeRole employeeRole = new EmployeeRole();
        employeeRole.setOrganization(organization);
        employeeRole.setName(request.getName());
        employeeRole.setDepartment(department);
        employeeRole.setSeniority(request.getSeniority());
        employeeRole.setDescription(request.getDescription());
        employeeRole.setActive(
                request.getActive() == null || request.getActive()
        );

        return mapToResponse(employeeRoleRepo.save(employeeRole));
    }

    public EmployeeRoleResponse update(
            Long organizationId,
            Long id,
            EmployeeRoleRequest request
    ) {

        EmployeeRole employeeRole =
                findEmployeeRole(organizationId, id);

        Department department = departmentRepo
                .findByIdAndOrganizationId(
                        request.getDepartmentId(),
                        organizationId
                )
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (
                (!employeeRole.getName().equalsIgnoreCase(request.getName())
                        || !employeeRole.getDepartment().getId()
                        .equals(request.getDepartmentId()))
                        &&
                        employeeRoleRepo
                                .existsByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
                                        request.getName(),
                                        request.getDepartmentId(),
                                        organizationId
                                )
        ) {
            throw new RuntimeException(
                    "Employee role already exists in this department"
            );
        }

        employeeRole.setName(request.getName());
        employeeRole.setDepartment(department);
        employeeRole.setSeniority(request.getSeniority());
        employeeRole.setDescription(request.getDescription());

        if (request.getActive() != null) {
            employeeRole.setActive(request.getActive());
        }

        return mapToResponse(employeeRoleRepo.save(employeeRole));
    }

    public EmployeeRoleResponse setActive(
            Long organizationId,
            Long id
    ) {
        EmployeeRole employeeRole =
                findEmployeeRole(organizationId, id);

        employeeRole.setActive(true);

        return mapToResponse(employeeRoleRepo.save(employeeRole));
    }

    public EmployeeRoleResponse setInactive(
            Long organizationId,
            Long id
    ) {
        EmployeeRole employeeRole =
                findEmployeeRole(organizationId, id);

        employeeRole.setActive(false);

        return mapToResponse(employeeRoleRepo.save(employeeRole));
    }

    public void delete(
            Long organizationId,
            Long id
    ) {
        EmployeeRole employeeRole =
                findEmployeeRole(organizationId, id);

        employeeRoleRepo.delete(employeeRole);
    }

    private EmployeeRole findEmployeeRole(
            Long organizationId,
            Long id
    ) {
        return employeeRoleRepo
                .findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() ->
                        new RuntimeException("Employee role not found"));
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
package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.request.Department.CreateDepartmentRequest;
import com.fatayriTech.avarLMS.request.Department.UpdateDepartmentRequest;
import com.fatayriTech.avarLMS.response.Department.DepartmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepo departmentRepo;
    private final OrganizationRepo organizationRepo;
    private final EmployeeRepo employeeRepo;

    public DepartmentResponse createDepartment(
            Long organizationId,
            CreateDepartmentRequest request
    ) {
        if (departmentRepo.existsByCodeAndOrganizationId(request.getCode(), organizationId)) {
            throw new RuntimeException("Department code already exists in this organization");
        }

        if (departmentRepo.existsByNameAndOrganizationId(request.getName(), organizationId)) {
            throw new RuntimeException("Department name already exists in this organization");
        }

        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Department department = new Department();
        department.setOrganization(organization);
        department.setCode(request.getCode());
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setHead(resolveHead(organizationId, request.getHeadEmployeeId()));

        return mapToResponse(departmentRepo.save(department));
    }

    public List<DepartmentResponse> getAllDepartments(Long organizationId) {
        return departmentRepo.findByOrganizationId(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DepartmentResponse getDepartmentById(
            Long organizationId,
            Long id
    ) {
        Department department = departmentRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        return mapToResponse(department);
    }

    public DepartmentResponse updateDepartment(
            Long organizationId,
            Long id,
            UpdateDepartmentRequest request
    ) {
        Department department = departmentRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (!department.getCode().equalsIgnoreCase(request.getCode())
                && departmentRepo.existsByCodeAndOrganizationId(request.getCode(), organizationId)) {
            throw new RuntimeException("Department code already exists in this organization");
        }

        if (!department.getName().equalsIgnoreCase(request.getName())
                && departmentRepo.existsByNameAndOrganizationId(request.getName(), organizationId)) {
            throw new RuntimeException("Department name already exists in this organization");
        }

        department.setCode(request.getCode());
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setActive(request.isActive());
        department.setHead(resolveHead(organizationId, request.getHeadEmployeeId()));

        return mapToResponse(departmentRepo.save(department));
    }

    public void deleteDepartment(
            Long organizationId,
            Long id
    ) {
        Department department = departmentRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        departmentRepo.delete(department);
    }

    public DepartmentResponse setDepartmentInactive(
            Long organizationId,
            Long id
    ) {
        Department department = departmentRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setActive(false);

        return mapToResponse(departmentRepo.save(department));
    }

    public DepartmentResponse setDepartmentActive(
            Long organizationId,
            Long id
    ) {
        Department department = departmentRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setActive(true);

        return mapToResponse(departmentRepo.save(department));
    }

    private Employee resolveHead(Long organizationId, Long headEmployeeId) {
        if (headEmployeeId == null) {
            return null;
        }

        return employeeRepo.findByIdAndOrganizationId(headEmployeeId, organizationId)
                .orElseThrow(() -> new RuntimeException("Selected department head not found in this organization"));
    }

    private DepartmentResponse mapToResponse(Department department) {
        Employee head = department.getHead();

        return new DepartmentResponse(
                department.getId(),
                department.getCode(),
                department.getName(),
                department.getDescription(),
                department.isActive(),
                head != null ? head.getId() : null,
                head != null ? buildEmployeeName(head) : null,
                head != null ? head.getEmail() : null,
                department.getCreationDate(),
                department.getModifiedDate()
        );
    }

    private String buildEmployeeName(Employee employee) {
        String fullName = String.join(" ",
                employee.getFirstName() != null ? employee.getFirstName() : "",
                employee.getMiddleName() != null ? employee.getMiddleName() : "",
                employee.getLastName() != null ? employee.getLastName() : ""
        ).trim();

        return fullName.isBlank() ? employee.getEmail() : fullName;
    }
}
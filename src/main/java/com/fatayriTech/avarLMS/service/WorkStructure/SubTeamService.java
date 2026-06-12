package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.model.SubTeam;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.repository.SubTeamRepo;
import com.fatayriTech.avarLMS.request.SubTeamRequest;
import com.fatayriTech.avarLMS.response.SubTeamResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubTeamService {

    private final SubTeamRepo subTeamRepo;
    private final DepartmentRepo departmentRepo;
    private final EmployeeRepo employeeRepo;
    private final OrganizationRepo organizationRepo;

    public List<SubTeamResponse> getAll(Long organizationId) {
        return subTeamRepo.findByOrganizationId(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SubTeamResponse getById(Long organizationId, Long id) {
        SubTeam subTeam = findSubTeam(organizationId, id);
        return mapToResponse(subTeam);
    }

    public SubTeamResponse create(Long organizationId, SubTeamRequest request) {
        if (subTeamRepo.existsByNameIgnoreCaseAndOrganizationId(
                request.getName(),
                organizationId
        )) {
            throw new RuntimeException("Sub-Team name already exists in this organization");
        }

        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Department department = departmentRepo
                .findByIdAndOrganizationId(request.getDepartmentId(), organizationId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Employee leadEmployee = null;

        if (request.getLeadEmployeeId() != null) {
            leadEmployee = employeeRepo
                    .findByIdAndOrganizationId(request.getLeadEmployeeId(), organizationId)
                    .orElseThrow(() -> new RuntimeException("Lead employee not found"));
        }

        SubTeam subTeam = SubTeam.builder()
                .organization(organization)
                .name(request.getName())
                .department(department)
                .leadEmployee(leadEmployee)
                .description(request.getDescription())
                .active(request.getActive() == null || request.getActive())
                .build();

        return mapToResponse(subTeamRepo.save(subTeam));
    }

    public SubTeamResponse update(
            Long organizationId,
            Long id,
            SubTeamRequest request
    ) {
        SubTeam subTeam = findSubTeam(organizationId, id);

        if (!subTeam.getName().equalsIgnoreCase(request.getName())
                && subTeamRepo.existsByNameIgnoreCaseAndOrganizationId(
                request.getName(),
                organizationId
        )) {
            throw new RuntimeException("Sub-Team name already exists in this organization");
        }

        Department department = departmentRepo
                .findByIdAndOrganizationId(request.getDepartmentId(), organizationId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Employee leadEmployee = null;

        if (request.getLeadEmployeeId() != null) {
            leadEmployee = employeeRepo
                    .findByIdAndOrganizationId(request.getLeadEmployeeId(), organizationId)
                    .orElseThrow(() -> new RuntimeException("Lead employee not found"));
        }

        subTeam.setName(request.getName());
        subTeam.setDepartment(department);
        subTeam.setLeadEmployee(leadEmployee);
        subTeam.setDescription(request.getDescription());

        if (request.getActive() != null) {
            subTeam.setActive(request.getActive());
        }

        return mapToResponse(subTeamRepo.save(subTeam));
    }

    public void delete(Long organizationId, Long id) {
        SubTeam subTeam = findSubTeam(organizationId, id);
        subTeamRepo.delete(subTeam);
    }

    public SubTeamResponse setActive(Long organizationId, Long id) {
        SubTeam subTeam = findSubTeam(organizationId, id);
        subTeam.setActive(true);
        return mapToResponse(subTeamRepo.save(subTeam));
    }

    public SubTeamResponse setInactive(Long organizationId, Long id) {
        SubTeam subTeam = findSubTeam(organizationId, id);
        subTeam.setActive(false);
        return mapToResponse(subTeamRepo.save(subTeam));
    }

    private SubTeam findSubTeam(Long organizationId, Long id) {
        return subTeamRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Sub-Team not found"));
    }

    private SubTeamResponse mapToResponse(SubTeam subTeam) {
        Employee lead = subTeam.getLeadEmployee();

        return SubTeamResponse.builder()
                .id(subTeam.getId())
                .name(subTeam.getName())
                .departmentId(subTeam.getDepartment() != null ? subTeam.getDepartment().getId() : null)
                .departmentName(subTeam.getDepartment() != null ? subTeam.getDepartment().getName() : null)
                .leadEmployeeId(lead != null ? lead.getId() : null)
                .leadEmployeeName(lead != null ? buildFullName(lead) : null)
                .leadEmployeeEmail(lead != null ? lead.getEmail() : null)
                .description(subTeam.getDescription())
                .active(subTeam.isActive())
                .build();
    }

    private String buildFullName(Employee employee) {
        return String.join(" ",
                employee.getFirstName() != null ? employee.getFirstName() : "",
                employee.getMiddleName() != null ? employee.getMiddleName() : "",
                employee.getLastName() != null ? employee.getLastName() : ""
        ).trim().replaceAll(" +", " ");
    }
}
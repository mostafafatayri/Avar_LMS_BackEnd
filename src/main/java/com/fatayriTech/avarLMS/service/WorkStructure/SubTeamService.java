package com.fatayriTech.avarLMS.service.WorkStructure;



import com.fatayriTech.avarLMS.request.SubTeamRequest;
import com.fatayriTech.avarLMS.response.SubTeamResponse;
import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.SubTeam;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.SubTeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubTeamService {

    private final SubTeamRepo subTeamRepo;
    private final DepartmentRepo departmentRepo;
    private final EmployeeRepo employeeRepo;

    public List<SubTeamResponse> getAll() {
        return subTeamRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SubTeamResponse getById(Long id) {
        SubTeam subTeam = findSubTeam(id);
        return mapToResponse(subTeam);
    }

    public SubTeamResponse create(SubTeamRequest request) {
        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Employee leadEmployee = null;

        if (request.getLeadEmployeeId() != null) {
            leadEmployee = employeeRepo.findById(request.getLeadEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Lead employee not found"));
        }

        SubTeam subTeam = SubTeam.builder()
                .name(request.getName())
                .department(department)
                .leadEmployee(leadEmployee)
                .description(request.getDescription())
                .active(request.getActive() == null || request.getActive())
                .build();

        return mapToResponse(subTeamRepo.save(subTeam));
    }

    public SubTeamResponse update(Long id, SubTeamRequest request) {
        SubTeam subTeam = findSubTeam(id);

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Employee leadEmployee = null;

        if (request.getLeadEmployeeId() != null) {
            leadEmployee = employeeRepo.findById(request.getLeadEmployeeId())
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

    public void delete(Long id) {
        SubTeam subTeam = findSubTeam(id);
        subTeamRepo.delete(subTeam);
    }

    public SubTeamResponse setActive(Long id) {
        SubTeam subTeam = findSubTeam(id);
        subTeam.setActive(true);
        return mapToResponse(subTeamRepo.save(subTeam));
    }

    public SubTeamResponse setInactive(Long id) {
        SubTeam subTeam = findSubTeam(id);
        subTeam.setActive(false);
        return mapToResponse(subTeamRepo.save(subTeam));
    }

    private SubTeam findSubTeam(Long id) {
        return subTeamRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Sub-Team not found"));
    }

    private SubTeamResponse mapToResponse(SubTeam subTeam) {
        Employee lead = subTeam.getLeadEmployee();

        return SubTeamResponse.builder()
                .id(subTeam.getId())
                .name(subTeam.getName())
                .departmentId(subTeam.getDepartment().getId())
                .departmentName(subTeam.getDepartment().getName())
                .leadEmployeeId(lead != null ? lead.getId() : null)
                .leadEmployeeName(lead != null ? lead.getFirstName()+" "+lead.getLastName() : null)
                .leadEmployeeEmail(lead != null ? lead.getEmail() : null)
                .description(subTeam.getDescription())
                .active(subTeam.isActive())
                .build();
    }
}
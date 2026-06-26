package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.projection.NameCountProjection;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.PositionRepo;
import com.fatayriTech.avarLMS.repository.SubTeamRepo;
import com.fatayriTech.avarLMS.response.Department.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentDetailsService {

    private final DepartmentRepo departmentRepo;
    private final EmployeeRepo employeeRepo;
    private final PositionRepo positionRepo;
    private final SubTeamRepo subTeamRepo;

    public DepartmentDetailsResponse getDepartmentDetails(
            Long organizationId,
            Long departmentId
    ) {

        Department department = departmentRepo
                .findByIdAndOrganizationId(departmentId, organizationId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        List<Employee> employees =
                employeeRepo.findByDepartmentIdAndOrganizationId(
                        departmentId,
                        organizationId
                );

        List<NameCountProjection> subTeams =
                subTeamRepo.countEmployeesBySubTeamInDepartment(
                        organizationId,
                        departmentId
                );

        List<NameCountProjection> positions =
                positionRepo.countEmployeesByPositionInDepartment(
                        organizationId,
                        departmentId
                );

        long totalEmployees =
                employeeRepo.countByOrganizationId(organizationId);

        return buildResponse(
                department,
                employees,
                subTeams,
                positions,
                totalEmployees
        );
    }

    private DepartmentDetailsResponse buildResponse(
            Department department,
            List<Employee> employees,
            List<NameCountProjection> subTeams,
            List<NameCountProjection> positions,
            long totalEmployees
    ) {

        DepartmentDetailsResponse response =
                new DepartmentDetailsResponse();

        response.setId(department.getId());
        response.setCode(department.getCode());
        response.setName(department.getName());
        response.setDescription(department.getDescription());
        response.setActive(department.isActive());

        response.setCreationDate(department.getCreationDate());
        response.setModifiedDate(department.getModifiedDate());

        if (department.getHead() != null) {

            Employee head = department.getHead();

            response.setHeadId(head.getId());

            response.setHeadName(
                    head.getFirstName() + " " + head.getLastName()
            );

            response.setHeadEmail(head.getEmail());

            response.setHeadPosition(
                    head.getPosition() != null
                            ? head.getPosition().getName()
                            : "-"
            );
        }

        response.setSummary(buildSummary(
                employees,
                subTeams,
                positions
        ));

        response.setDistribution(buildDistribution(
                employees.size(),
                totalEmployees
        ));

        response.setSubTeams(buildSubTeams(subTeams));

        response.setJobTitles(buildPositions(positions));

        response.setEmployees(buildEmployees(employees));

        return response;
    }

    private DepartmentSummaryResponse buildSummary(
            List<Employee> employees,
            List<NameCountProjection> subTeams,
            List<NameCountProjection> positions
    ) {

        DepartmentSummaryResponse summary =
                new DepartmentSummaryResponse();

        summary.setEmployeeCount(employees.size());

        summary.setSubTeamCount(subTeams.size());

        summary.setJobTitleCount(positions.size());

        return summary;
    }

    private EmployeeDistributionResponse buildDistribution(
            long departmentEmployees,
            long totalEmployees
    ) {

        EmployeeDistributionResponse distribution =
                new EmployeeDistributionResponse();

        distribution.setDepartmentEmployees((int) departmentEmployees);

        distribution.setTotalCompanyEmployees((int) totalEmployees);

        double percent =
                totalEmployees == 0
                        ? 0
                        : (departmentEmployees * 100.0) / totalEmployees;

        distribution.setDepartmentPercentage(percent);

        distribution.setOtherPercentage(100 - percent);

        return distribution;
    }

    private List<DepartmentEmployeeResponse> buildEmployees(
            List<Employee> employees
    ) {

        return employees.stream().map(e -> {

            DepartmentEmployeeResponse dto =
                    new DepartmentEmployeeResponse();

            dto.setId(e.getId());

            dto.setEmployeeId(e.getEmployeeId());

            dto.setFullName(
                    e.getFirstName() + " " + e.getLastName()
            );

            dto.setEmail(e.getEmail());

            dto.setActive(e.isActive());

            dto.setPosition(
                    e.getPosition() != null
                            ? e.getPosition().getName()
                            : "-"
            );

            dto.setSubTeam(
                    e.getSubTeam() != null
                            ? e.getSubTeam().getName()
                            : "-"
            );

            return dto;

        }).toList();
    }

    private List<DepartmentJobTitleResponse> buildPositions(
            List<NameCountProjection> list
    ) {

        return list.stream().map(item -> {

            DepartmentJobTitleResponse dto =
                    new DepartmentJobTitleResponse();

            dto.setId(item.getId());

            dto.setName(item.getName());

            dto.setEmployees(item.getTotal().intValue());

            return dto;

        }).toList();
    }

    private List<DepartmentSubTeamResponse> buildSubTeams(
            List<NameCountProjection> list
    ) {

        return list.stream().map(item -> {

            DepartmentSubTeamResponse dto =
                    new DepartmentSubTeamResponse();

            dto.setId(item.getId());

            dto.setName(item.getName());

            dto.setEmployees(item.getTotal().intValue());

            return dto;

        }).toList();
    }

}
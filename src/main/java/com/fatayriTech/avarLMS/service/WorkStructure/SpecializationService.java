package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.model.Specialization;
import com.fatayriTech.avarLMS.model.SubTeam;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.repository.SpecializationRepo;
import com.fatayriTech.avarLMS.repository.SubTeamRepo;
import com.fatayriTech.avarLMS.request.SpecializationRequest;
import com.fatayriTech.avarLMS.response.SpecializationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepo specializationRepo;
    private final DepartmentRepo departmentRepo;
    private final SubTeamRepo subTeamRepo;
    private final OrganizationRepo organizationRepo;

    public List<SpecializationResponse> getAll(Long organizationId) {
        return specializationRepo.findByOrganizationId(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SpecializationResponse getById(Long organizationId, Long id) {
        return mapToResponse(findSpecialization(organizationId, id));
    }

    public SpecializationResponse create(
            Long organizationId,
            SpecializationRequest request
    ) {
        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Department department = departmentRepo
                .findByIdAndOrganizationId(request.getDepartmentId(), organizationId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        SubTeam subTeam = null;

        if (request.getSubTeamId() != null) {
            subTeam = subTeamRepo
                    .findByIdAndOrganizationId(request.getSubTeamId(), organizationId)
                    .orElseThrow(() -> new RuntimeException("Sub-Team not found"));
        }

        if (specializationRepo.existsByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
                request.getName(),
                request.getDepartmentId(),
                organizationId
        )) {
            throw new RuntimeException("Specialization already exists in this department");
        }

        Specialization specialization = new Specialization();
        specialization.setOrganization(organization);
        specialization.setName(request.getName());
        specialization.setDepartment(department);
        specialization.setSubTeam(subTeam);
        specialization.setDescription(request.getDescription());
        specialization.setActive(request.getActive() == null || request.getActive());

        return mapToResponse(specializationRepo.save(specialization));
    }

    public SpecializationResponse update(
            Long organizationId,
            Long id,
            SpecializationRequest request
    ) {
        Specialization specialization = findSpecialization(organizationId, id);

        Department department = departmentRepo
                .findByIdAndOrganizationId(request.getDepartmentId(), organizationId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        SubTeam subTeam = null;

        if (request.getSubTeamId() != null) {
            subTeam = subTeamRepo
                    .findByIdAndOrganizationId(request.getSubTeamId(), organizationId)
                    .orElseThrow(() -> new RuntimeException("Sub-Team not found"));
        }

        specialization.setName(request.getName());
        specialization.setDepartment(department);
        specialization.setSubTeam(subTeam);
        specialization.setDescription(request.getDescription());

        if (request.getActive() != null) {
            specialization.setActive(request.getActive());
        }

        return mapToResponse(specializationRepo.save(specialization));
    }

    public SpecializationResponse setActive(Long organizationId, Long id) {
        Specialization specialization = findSpecialization(organizationId, id);
        specialization.setActive(true);
        return mapToResponse(specializationRepo.save(specialization));
    }

    public SpecializationResponse setInactive(Long organizationId, Long id) {
        Specialization specialization = findSpecialization(organizationId, id);
        specialization.setActive(false);
        return mapToResponse(specializationRepo.save(specialization));
    }

    public void delete(Long organizationId, Long id) {
        specializationRepo.delete(findSpecialization(organizationId, id));
    }

    private Specialization findSpecialization(Long organizationId, Long id) {
        return specializationRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Specialization not found"));
    }

    private SpecializationResponse mapToResponse(Specialization specialization) {
        SubTeam subTeam = specialization.getSubTeam();

        return SpecializationResponse.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .departmentId(specialization.getDepartment() != null ? specialization.getDepartment().getId() : null)
                .departmentName(specialization.getDepartment() != null ? specialization.getDepartment().getName() : null)
                .subTeamId(subTeam != null ? subTeam.getId() : null)
                .subTeamName(subTeam != null ? subTeam.getName() : null)
                .description(specialization.getDescription())
                .active(specialization.isActive())
                .build();
    }
}
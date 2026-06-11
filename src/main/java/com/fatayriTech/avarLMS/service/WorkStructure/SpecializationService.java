package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Specialization;
import com.fatayriTech.avarLMS.model.SubTeam;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
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

    public List<SpecializationResponse> getAll() {
        return specializationRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SpecializationResponse getById(Long id) {
        return mapToResponse(findSpecialization(id));
    }

    public SpecializationResponse create(SpecializationRequest request) {
        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        SubTeam subTeam = null;

        if (request.getSubTeamId() != null) {
            subTeam = subTeamRepo.findById(request.getSubTeamId())
                    .orElseThrow(() -> new RuntimeException("Sub-Team not found"));
        }

        if (specializationRepo.existsByNameIgnoreCaseAndDepartmentId(
                request.getName(),
                request.getDepartmentId()
        )) {
            throw new RuntimeException("Specialization already exists in this department");
        }

        Specialization specialization = new Specialization();
        specialization.setName(request.getName());
        specialization.setDepartment(department);
        specialization.setSubTeam(subTeam);
        specialization.setDescription(request.getDescription());
        specialization.setActive(request.getActive() == null || request.getActive());

        return mapToResponse(specializationRepo.save(specialization));
    }

    public SpecializationResponse update(Long id, SpecializationRequest request) {
        Specialization specialization = findSpecialization(id);

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        SubTeam subTeam = null;

        if (request.getSubTeamId() != null) {
            subTeam = subTeamRepo.findById(request.getSubTeamId())
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

    public SpecializationResponse setActive(Long id) {
        Specialization specialization = findSpecialization(id);
        specialization.setActive(true);
        return mapToResponse(specializationRepo.save(specialization));
    }

    public SpecializationResponse setInactive(Long id) {
        Specialization specialization = findSpecialization(id);
        specialization.setActive(false);
        return mapToResponse(specializationRepo.save(specialization));
    }

    public void delete(Long id) {
        specializationRepo.delete(findSpecialization(id));
    }

    private Specialization findSpecialization(Long id) {
        return specializationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialization not found"));
    }

    private SpecializationResponse mapToResponse(Specialization specialization) {
        SubTeam subTeam = specialization.getSubTeam();

        return SpecializationResponse.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .departmentId(specialization.getDepartment().getId())
                .departmentName(specialization.getDepartment().getName())
                .subTeamId(subTeam != null ? subTeam.getId() : null)
                .subTeamName(subTeam != null ? subTeam.getName() : null)
                .description(specialization.getDescription())
                .active(specialization.isActive())
                .build();
    }
}
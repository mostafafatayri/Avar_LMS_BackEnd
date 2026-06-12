package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.model.Position;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.PositionRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.request.position.CreatePositionRequest;
import com.fatayriTech.avarLMS.request.position.UpdatePositionRequest;
import com.fatayriTech.avarLMS.response.position.PositionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepo positionRepo;
    private final DepartmentRepo departmentRepo;
    private final OrganizationRepo organizationRepo;

    public PositionResponse createPosition(Long organizationId, CreatePositionRequest request) {

        if (positionRepo.existsByCodeAndOrganizationId(request.getCode(), organizationId)) {
            throw new RuntimeException("Position code already exists in this organization");
        }

        if (positionRepo.existsByNameAndOrganizationId(request.getName(), organizationId)) {
            throw new RuntimeException("Position name already exists in this organization");
        }

        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Department department = departmentRepo.findByIdAndOrganizationId(
                        request.getDepartmentId(),
                        organizationId
                )
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Position position = new Position();
        position.setOrganization(organization);
        position.setCode(request.getCode());
        position.setName(request.getName());
        position.setDescription(request.getDescription());
        position.setDepartment(department);

        return mapToResponse(positionRepo.save(position));
    }

    public List<PositionResponse> getAllPositions(Long organizationId) {
        return positionRepo.findByOrganizationId(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PositionResponse getPositionById(Long organizationId, Long id) {

        Position position = positionRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        return mapToResponse(position);
    }

    public PositionResponse updatePosition(
            Long organizationId,
            Long id,
            UpdatePositionRequest request
    ) {

        Position position = positionRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        if (!position.getCode().equalsIgnoreCase(request.getCode())
                && positionRepo.existsByCodeAndOrganizationId(request.getCode(), organizationId)) {
            throw new RuntimeException("Position code already exists in this organization");
        }

        if (!position.getName().equalsIgnoreCase(request.getName())
                && positionRepo.existsByNameAndOrganizationId(request.getName(), organizationId)) {
            throw new RuntimeException("Position name already exists in this organization");
        }

        Department department = departmentRepo.findByIdAndOrganizationId(
                        request.getDepartmentId(),
                        organizationId
                )
                .orElseThrow(() -> new RuntimeException("Department not found"));

        position.setCode(request.getCode());
        position.setName(request.getName());
        position.setDescription(request.getDescription());
        position.setActive(request.isActive());
        position.setDepartment(department);

        return mapToResponse(positionRepo.save(position));
    }

    public void deletePosition(Long organizationId, Long id) {

        Position position = positionRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        positionRepo.delete(position);
    }

    public PositionResponse setPositionInactive(Long organizationId, Long id) {
        Position position = positionRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        position.setActive(false);

        return mapToResponse(positionRepo.save(position));
    }

    public PositionResponse setPositionActive(Long organizationId, Long id) {
        Position position = positionRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        position.setActive(true);

        return mapToResponse(positionRepo.save(position));
    }

    private PositionResponse mapToResponse(Position position) {

        return new PositionResponse(
                position.getId(),
                position.getCode(),
                position.getName(),
                position.getDescription(),
                position.isActive(),
                position.getDepartment() != null ? position.getDepartment().getId() : null,
                position.getDepartment() != null ? position.getDepartment().getName() : null,
                position.getCreationDate(),
                position.getModifiedDate()
        );
    }
}
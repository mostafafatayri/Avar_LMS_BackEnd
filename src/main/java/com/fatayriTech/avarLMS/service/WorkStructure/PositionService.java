package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Position;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.PositionRepo;
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

    public PositionResponse createPosition(CreatePositionRequest request) {

        if (positionRepo.existsByCode(request.getCode())) {
            throw new RuntimeException("Position code already exists");
        }

        if (positionRepo.existsByName(request.getName())) {
            throw new RuntimeException("Position name already exists");
        }

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Position position = new Position();
        position.setCode(request.getCode());
        position.setName(request.getName());
        position.setDescription(request.getDescription());
        position.setDepartment(department);

        return mapToResponse(positionRepo.save(position));
    }

    public List<PositionResponse> getAllPositions() {
        return positionRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PositionResponse getPositionById(Long id) {

        Position position = positionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        return mapToResponse(position);
    }

    public PositionResponse updatePosition(
            Long id,
            UpdatePositionRequest request
    ) {

        Position position = positionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        position.setCode(request.getCode());
        position.setName(request.getName());
        position.setDescription(request.getDescription());
        position.setActive(request.isActive());
        position.setDepartment(department);

        return mapToResponse(positionRepo.save(position));
    }

    public void deletePosition(Long id) {

        Position position = positionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        positionRepo.delete(position);
    }

    public PositionResponse setPositionInactive(Long id) {
        Position position = positionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        position.setActive(false);

        return mapToResponse(positionRepo.save(position));
    }

    public PositionResponse setPositionActive(Long id) {
        Position position = positionRepo.findById(id)
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
                position.getDepartment().getId(),
                position.getDepartment().getName(),
                position.getCreationDate(),
                position.getModifiedDate()
        );
    }
}
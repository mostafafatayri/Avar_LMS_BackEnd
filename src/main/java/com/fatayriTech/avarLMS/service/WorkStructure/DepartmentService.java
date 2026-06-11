package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
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

    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {

        if (departmentRepo.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists");
        }

        if (departmentRepo.existsByName(request.getName())) {
            throw new RuntimeException("Department name already exists");
        }

        Department department = new Department();
        department.setCode(request.getCode());
        department.setName(request.getName());
        department.setDescription(request.getDescription());

        return mapToResponse(departmentRepo.save(department));
    }

    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DepartmentResponse getDepartmentById(Long id) {

        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        return mapToResponse(department);
    }

    public DepartmentResponse updateDepartment(
            Long id,
            UpdateDepartmentRequest request
    ) {

        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setCode(request.getCode());
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setActive(request.isActive());

        return mapToResponse(departmentRepo.save(department));
    }

    public void deleteDepartment(Long id) {

        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        departmentRepo.delete(department);
    }



    /*public DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request) {
        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());
        department.setCostCenter(request.getCostCenter());

        return mapToResponse(departmentRepo.save(department));
    }*/

    public DepartmentResponse setDepartmentInactive(Long id) {
        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setActive(false);

        return mapToResponse(departmentRepo.save(department));
    }

    public DepartmentResponse setDepartmentActive(Long id) {
        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setActive(true);

        return mapToResponse(departmentRepo.save(department));
    }
    private DepartmentResponse mapToResponse(Department department) {

        return new DepartmentResponse(
                department.getId(),
                department.getCode(),
                department.getName(),
                department.getDescription(),
                department.isActive(),
                department.getCreationDate(),
                department.getModifiedDate()
        );
    }
}

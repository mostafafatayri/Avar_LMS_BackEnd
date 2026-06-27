package com.fatayriTech.avarLMS.repository.DepartmentRepo;

import com.fatayriTech.avarLMS.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepo extends JpaRepository<Department, Long> {

    List<Department> findByOrganizationId(Long organizationId);

    Optional<Department> findByIdAndOrganizationId(Long id, Long organizationId);

    Optional<Department> findByCodeAndOrganizationId(String code, Long organizationId);

    boolean existsByCodeAndOrganizationId(String code, Long organizationId);

    boolean existsByNameAndOrganizationId(String name, Long organizationId);

    // Keep only if needed globally
    Optional<Department> findByCode(String code);


}
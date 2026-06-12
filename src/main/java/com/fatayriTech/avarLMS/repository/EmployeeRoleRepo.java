package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRoleRepo extends JpaRepository<EmployeeRole, Long> {

    List<EmployeeRole> findByOrganizationId(Long organizationId);

    Optional<EmployeeRole> findByIdAndOrganizationId(Long id, Long organizationId);

    boolean existsByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
            String name,
            Long departmentId,
            Long organizationId
    );
}
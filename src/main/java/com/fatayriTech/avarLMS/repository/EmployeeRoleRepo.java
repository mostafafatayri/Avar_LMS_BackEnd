package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRoleRepo extends JpaRepository<EmployeeRole, Long> {
    boolean existsByNameIgnoreCaseAndDepartmentId(String name, Long departmentId);
}
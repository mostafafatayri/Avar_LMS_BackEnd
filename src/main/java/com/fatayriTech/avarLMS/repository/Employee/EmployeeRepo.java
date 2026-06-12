package com.fatayriTech.avarLMS.repository.Employee;

import com.fatayriTech.avarLMS.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    List<Employee> findByOrganizationId(Long organizationId);

    Optional<Employee> findByIdAndOrganizationId(Long id, Long organizationId);

    Optional<Employee> findByEmployeeIdAndOrganizationId(
            String employeeId,
            Long organizationId
    );

    Optional<Employee> findByEmailAndOrganizationId(
            String email,
            Long organizationId
    );

    Optional<Employee> findByUsernameAndOrganizationId(
            String username,
            Long organizationId
    );

    Optional<Employee> findByMasterUserIdAndOrganizationId(
            Long masterUserId,
            Long organizationId
    );

    List<Employee> findByManagerIdAndOrganizationId(
            Long managerId,
            Long organizationId
    );

    boolean existsByEmailAndOrganizationId(
            String email,
            Long organizationId
    );

    boolean existsByEmployeeIdAndOrganizationId(
            String employeeId,
            Long organizationId
    );

    boolean existsByUsernameAndOrganizationId(
            String username,
            Long organizationId
    );

    // Keep these only if you need global checks across all orgs
    Optional<Employee> findByMasterUserId(Long masterUserId);
}
package com.fatayriTech.avarLMS.repository.Employee;

import com.fatayriTech.avarLMS.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByUsername(String username);
    boolean existsByUsername(String username);
    List<Employee> findByManagerId(Long managerId);
    Optional<Employee> findByMasterUserId(Long masterUserId);
}
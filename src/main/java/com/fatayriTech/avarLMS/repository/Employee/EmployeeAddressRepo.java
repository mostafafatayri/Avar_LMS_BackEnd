package com.fatayriTech.avarLMS.repository.Employee;

import com.fatayriTech.avarLMS.model.EmployeeAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeAddressRepo extends JpaRepository<EmployeeAddress, Long> {

    List<EmployeeAddress> findByOrganizationIdAndEmployeeIdAndActiveTrueOrderByPrimaryAddressDescCreationDateDesc(
            Long organizationId,
            Long employeeId
    );

    Optional<EmployeeAddress> findByIdAndOrganizationIdAndEmployeeIdAndActiveTrue(
            Long id,
            Long organizationId,
            Long employeeId
    );

    Optional<EmployeeAddress> findByOrganizationIdAndEmployeeIdAndPrimaryAddressTrueAndActiveTrue(
            Long organizationId,
            Long employeeId
    );
}
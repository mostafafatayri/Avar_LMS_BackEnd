package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpecializationRepo extends JpaRepository<Specialization, Long> {

    List<Specialization> findByOrganizationId(Long organizationId);

    Optional<Specialization> findByIdAndOrganizationId(Long id, Long organizationId);

    boolean existsByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
            String name,
            Long departmentId,
            Long organizationId
    );

    Optional<Specialization> findByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
            String name,
            Long departmentId,
            Long organizationId
    );
}
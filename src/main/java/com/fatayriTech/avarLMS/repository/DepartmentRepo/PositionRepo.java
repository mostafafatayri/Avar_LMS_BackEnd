package com.fatayriTech.avarLMS.repository.DepartmentRepo;

import com.fatayriTech.avarLMS.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepo extends JpaRepository<Position, Long> {

    List<Position> findByOrganizationId(Long organizationId);

    Optional<Position> findByIdAndOrganizationId(Long id, Long organizationId);

    Optional<Position> findByCodeAndOrganizationId(String code, Long organizationId);

    boolean existsByCodeAndOrganizationId(String code, Long organizationId);

    boolean existsByNameAndOrganizationId(String name, Long organizationId);

    // Keep only if needed globally
    Optional<Position> findByCode(String code);
}
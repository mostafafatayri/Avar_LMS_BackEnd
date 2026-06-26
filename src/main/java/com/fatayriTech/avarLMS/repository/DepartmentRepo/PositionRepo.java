package com.fatayriTech.avarLMS.repository.DepartmentRepo;

import com.fatayriTech.avarLMS.model.Position;
import com.fatayriTech.avarLMS.projection.NameCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
        select 
            p.id as id,
            p.name as name,
            count(e.id) as total
        from Employee e
        join e.position p
        where e.department.id = :departmentId
          and e.organization.id = :organizationId
        group by p.id, p.name
        order by p.name asc
    """)
    List<NameCountProjection> countEmployeesByPositionInDepartment(
            @Param("organizationId") Long organizationId,
            @Param("departmentId") Long departmentId
    );



}
package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.SubTeam;
import com.fatayriTech.avarLMS.projection.NameCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubTeamRepo extends JpaRepository<SubTeam, Long> {

    List<SubTeam> findByOrganizationId(Long organizationId);

    Optional<SubTeam> findByIdAndOrganizationId(Long id, Long organizationId);

    boolean existsByNameIgnoreCaseAndOrganizationId(String name, Long organizationId);

    @Query("""
        select 
            st.id as id,
            st.name as name,
            count(e.id) as total
        from Employee e
        join e.subTeam st
        where e.department.id = :departmentId
          and e.organization.id = :organizationId
        group by st.id, st.name
        order by st.name asc
    """)
    List<NameCountProjection> countEmployeesBySubTeamInDepartment(
            @Param("organizationId") Long organizationId,
            @Param("departmentId") Long departmentId
    );

    Optional<SubTeam> findByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
            String name,
            Long departmentId,
            Long organizationId
    );
}
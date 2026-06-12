package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.SubTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubTeamRepo extends JpaRepository<SubTeam, Long> {

    List<SubTeam> findByOrganizationId(Long organizationId);

    Optional<SubTeam> findByIdAndOrganizationId(Long id, Long organizationId);

    boolean existsByNameIgnoreCaseAndOrganizationId(String name, Long organizationId);
}
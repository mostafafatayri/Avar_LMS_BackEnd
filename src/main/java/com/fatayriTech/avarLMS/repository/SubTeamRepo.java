package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.SubTeam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubTeamRepo extends JpaRepository<SubTeam, Long> {
    boolean existsByNameIgnoreCase(String name);
}
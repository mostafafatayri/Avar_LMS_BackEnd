package com.fatayriTech.avarLMS.repository.DepartmentRepo;

import com.fatayriTech.avarLMS.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PositionRepo extends JpaRepository<Position, Long> {

    Optional<Position> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);
}
package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NationalityRepo extends JpaRepository<Nationality, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByCodeIgnoreCase(String code);
}
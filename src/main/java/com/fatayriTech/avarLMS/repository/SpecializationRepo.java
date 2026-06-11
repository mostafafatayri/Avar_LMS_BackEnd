package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecializationRepo extends JpaRepository<Specialization, Long> {
    boolean existsByNameIgnoreCaseAndDepartmentId(String name, Long departmentId);
}
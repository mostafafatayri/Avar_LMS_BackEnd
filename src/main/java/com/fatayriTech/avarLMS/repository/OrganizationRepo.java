package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepo extends JpaRepository<Organization, Long> {

    boolean existsByCode(String code);

    Optional<Organization> findByCode(String code);
}
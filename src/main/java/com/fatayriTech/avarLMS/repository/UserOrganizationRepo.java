package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserOrganizationRepo extends JpaRepository<UserOrganization, Long> {

    List<UserOrganization> findByUserIdAndActiveTrue(Long userId);

    boolean existsByUserIdAndOrganizationIdAndActiveTrue(Long userId, Long organizationId);

    Optional<UserOrganization> findByUserIdAndOrganizationId(Long userId, Long organizationId);

    List<UserOrganization> findByOrganizationIdAndActiveTrue(Long organizationId);
}
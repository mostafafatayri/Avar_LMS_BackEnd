package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOrganizationRepo extends JpaRepository<UserOrganization, Long> {

    List<UserOrganization> findByUserIdAndActiveTrue(Long userId);

    boolean existsByUserIdAndOrganizationIdAndActiveTrue(Long userId, Long organizationId);
}
package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.SecurityRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
/// /check the query
public interface SecurityRoleRepo extends JpaRepository<SecurityRole, Long> {

    Optional<SecurityRole> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);
    @Query("""
        SELECT DISTINCT usr.role
        FROM UserSecurityRole usr
        LEFT JOIN FETCH usr.role.permissions
        WHERE usr.userId = :userId
    """)
    List<SecurityRole> findByMasterUserId(
            @Param("userId") Long userId
    );
}
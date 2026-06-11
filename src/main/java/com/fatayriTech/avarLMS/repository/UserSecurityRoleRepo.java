package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.SecurityRole;
import com.fatayriTech.avarLMS.model.UserSecurityRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSecurityRoleRepo extends JpaRepository<UserSecurityRole, Long> {

    @Query("""
        SELECT DISTINCT usr.role
        FROM UserSecurityRole usr
        LEFT JOIN FETCH usr.role.permissions
        WHERE usr.userId = :userId
    """)
    List<SecurityRole> findRolesWithPermissionsByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);


    List<UserSecurityRole> findByRoleId(Long roleId);

    long countByRoleId(Long roleId);



    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}
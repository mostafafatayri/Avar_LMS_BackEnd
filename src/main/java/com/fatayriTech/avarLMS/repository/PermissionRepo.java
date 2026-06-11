package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PermissionRepo extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    boolean existsByName(String name);

    @Query("""
           SELECT p FROM Permission p
           WHERE p.id NOT IN (
               SELECT permission.id FROM SecurityRole role
               JOIN role.permissions permission
               WHERE role.id = :groupId
           )
           """)
    List<Permission> findPermissionsNotInSecurityGroup(@Param("groupId") Long groupId);


}
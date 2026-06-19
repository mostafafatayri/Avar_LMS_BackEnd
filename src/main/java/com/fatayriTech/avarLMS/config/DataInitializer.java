package com.fatayriTech.avarLMS.config;

import com.fatayriTech.avarLMS.model.Permission;
import com.fatayriTech.avarLMS.model.SecurityRole;
import com.fatayriTech.avarLMS.model.User;
import com.fatayriTech.avarLMS.repository.PermissionRepo;
import com.fatayriTech.avarLMS.repository.SecurityRoleRepo;
import com.fatayriTech.avarLMS.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@AllArgsConstructor
public class DataInitializer {

    private final PermissionRepo permissionRepo;
    private final SecurityRoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDefaultUsersAndRoles() {
        return args -> {

            List<String> adminPermissionNames = List.of(
                    "USER_MANAGE",
                    "EMPLOYEE_VIEW",
                    "EMPLOYEE_CREATE",
                    "EMPLOYEE_UPDATE",
                    "EMPLOYEE_DELETE",
                    "EMPLOYEE_BULK_UPLOAD",
                    "DEPARTMENT_VIEW",
                    "DEPARTMENT_CREATE",
                    "DEPARTMENT_UPDATE",
                    "DEPARTMENT_DELETE",
                    "POSITION_VIEW",
                    "POSITION_CREATE",
                    "POSITION_UPDATE",
                    "POSITION_DELETE",
                    "ORGANIZATION_VIEW",
                    "TRAINING_ASSIGNMENT_VIEW",
                    "TRAINING_ASSIGNMENT_CREATE",
                    "TRAINING_ASSIGNMENT_UPDATE",
                    "TRAINING_ASSIGNMENT_DELETE",
                    "LEARNING_PATH_ASSIGNMENT_VIEW",
                    "LEARNING_PATH_ASSIGNMENT_CREATE",
                    "LEARNING_PATH_ASSIGNMENT_UPDATE",
                    "LEARNING_PATH_ASSIGNMENT_DELETE"
            );

            List<String> superAdminPermissionNames = List.of(
                    "USER_MANAGE",
                    "EMPLOYEE_VIEW",
                    "EMPLOYEE_CREATE",
                    "EMPLOYEE_UPDATE",
                    "EMPLOYEE_DELETE",
                    "EMPLOYEE_BULK_UPLOAD",
                    "DEPARTMENT_VIEW",
                    "DEPARTMENT_CREATE",
                    "DEPARTMENT_UPDATE",
                    "DEPARTMENT_DELETE",
                    "POSITION_VIEW",
                    "POSITION_CREATE",
                    "POSITION_UPDATE",
                    "POSITION_DELETE",
                    "ORGANIZATION_VIEW",
                    "ORGANIZATION_CREATE",
                    "ORGANIZATION_UPDATE",
                    "ORGANIZATION_DELETE"
            );

            Set<Permission> adminPermissions = createPermissions(adminPermissionNames);
            Set<Permission> superAdminPermissions = createPermissions(superAdminPermissionNames);

            SecurityRole adminRole = createOrUpdateRole(
                    "ADMIN",
                    "Administrator",
                    "Admin access to LMS modules without organization creation",
                    adminPermissions
            );

            SecurityRole superAdminRole = createOrUpdateRole(
                    "SUPER_ADMIN",
                    "Super Administrator",
                    "Full platform access including organization management",
                    superAdminPermissions
            );

            userRepo.findByEmail("admin@avar.com")
                    .orElseGet(() -> {
                        User admin = new User();
                        admin.setUsername("admin");
                        admin.setEmail("admin@avar.com");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setFirstName("System");
                        admin.setLastName("Admin");
                        admin.setConfirmed(true);
                        admin.setRoles(Set.of(adminRole));
                        return userRepo.save(admin);
                    });

            userRepo.findByEmail("superadmin@avar.com")
                    .orElseGet(() -> {
                        User superAdmin = new User();
                        superAdmin.setUsername("superadmin");
                        superAdmin.setEmail("superadmin@avar.com");
                        superAdmin.setPassword(passwordEncoder.encode("superadmin123"));
                        superAdmin.setFirstName("System");
                        superAdmin.setLastName("Super Admin");
                        superAdmin.setConfirmed(true);
                        superAdmin.setRoles(Set.of(superAdminRole));
                        return userRepo.save(superAdmin);
                    });
        };
    }

    private Set<Permission> createPermissions(List<String> permissionNames) {
        return permissionNames.stream()
                .map(name -> permissionRepo.findByName(name)
                        .orElseGet(() -> {
                            Permission permission = new Permission();
                            permission.setName(name);
                            permission.setDescription("Permission: " + name);
                            return permissionRepo.save(permission);
                        }))
                .collect(Collectors.toSet());
    }

    private SecurityRole createOrUpdateRole(
            String code,
            String name,
            String description,
            Set<Permission> permissions
    ) {
        SecurityRole role = roleRepo.findByCode(code)
                .orElseGet(() -> {
                    SecurityRole newRole = new SecurityRole();
                    newRole.setCode(code);
                    return newRole;
                });

        role.setName(name);
        role.setDescription(description);
        role.setActive(true);
        role.setPermissions(permissions);

        return roleRepo.save(role);
    }
}
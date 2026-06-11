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
    CommandLineRunner initDefaultAdminUser() {
        return args -> {
            List<String> adminPermissions = List.of(
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
                    "POSITION_DELETE"
            );

            Set<Permission> permissions = adminPermissions.stream()
                    .map(name -> permissionRepo.findByName(name)
                            .orElseGet(() -> {
                                Permission permission = new Permission();
                                permission.setName(name);
                                permission.setDescription("Permission: " + name);
                                return permissionRepo.save(permission);
                            }))
                    .collect(Collectors.toSet());

            SecurityRole adminRole = roleRepo.findByCode("ADMIN")
                    .orElseGet(() -> {
                        SecurityRole role = new SecurityRole();
                        role.setName("Administrator");
                        role.setCode("ADMIN");
                        role.setDescription("Full access to all LMS modules");
                        role.setActive(true);
                        return roleRepo.save(role);
                    });

            adminRole.setName("Administrator");
            adminRole.setCode("ADMIN");
            adminRole.setDescription("Full access to all LMS modules");
            adminRole.setActive(true);
            adminRole.setPermissions(permissions);

            adminRole = roleRepo.save(adminRole);

            SecurityRole finalAdminRole = adminRole;

            userRepo.findByEmail("admin@avar.com")
                    .orElseGet(() -> {
                        User admin = new User();
                        admin.setUsername("admin");
                        admin.setEmail("admin@avar.com");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setFirstName("System");
                        admin.setLastName("Admin");
                        admin.setConfirmed(true);
                        admin.setRoles(Set.of(finalAdminRole));

                        return userRepo.save(admin);
                    });
        };
    }
}
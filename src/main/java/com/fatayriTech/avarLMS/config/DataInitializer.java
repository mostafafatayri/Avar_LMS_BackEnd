package com.fatayriTech.avarLMS.config;

import com.fatayriTech.avarLMS.enums.DataScope;
import com.fatayriTech.avarLMS.model.Permission;
import com.fatayriTech.avarLMS.model.SecurityRole;
import com.fatayriTech.avarLMS.model.SeniorityLevel;
import com.fatayriTech.avarLMS.model.User;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.repository.PermissionRepo;
import com.fatayriTech.avarLMS.repository.SecurityRoleRepo;
import com.fatayriTech.avarLMS.repository.SeniorityLevelRepo;
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
    private final SeniorityLevelRepo seniorityLevelRepo;

    @Bean
    CommandLineRunner initDefaultUsersAndRoles() {
        return args -> {

            List<String> adminAndHrPermissions = List.of(
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

                    "TRAINING_CATALOGUE_VIEW",
                    "TRAINING_CATALOGUE_CREATE",
                    "TRAINING_CATALOGUE_UPDATE",
                    "TRAINING_CATALOGUE_DELETE",
                    "TRAINING_CATALOGUE_BULK_UPLOAD",

                    "TRAINING_ASSIGNMENT_VIEW",
                    "TRAINING_ASSIGNMENT_CREATE",
                    "TRAINING_ASSIGNMENT_UPDATE",
                    "TRAINING_ASSIGNMENT_DELETE",

                    "LEARNING_PATH_ASSIGNMENT_VIEW",
                    "LEARNING_PATH_ASSIGNMENT_CREATE",
                    "LEARNING_PATH_ASSIGNMENT_UPDATE",
                    "LEARNING_PATH_ASSIGNMENT_DELETE",

                    // Admin and HR can only view roles
                    "ROLE_VIEW"
            );

            List<String> superAdminPermissions = List.of(
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
                    "ORGANIZATION_DELETE",

                    "TRAINING_CATALOGUE_VIEW",
                    "TRAINING_CATALOGUE_CREATE",
                    "TRAINING_CATALOGUE_UPDATE",
                    "TRAINING_CATALOGUE_DELETE",
                    "TRAINING_CATALOGUE_BULK_UPLOAD",

                    "TRAINING_ASSIGNMENT_VIEW",
                    "TRAINING_ASSIGNMENT_CREATE",
                    "TRAINING_ASSIGNMENT_UPDATE",
                    "TRAINING_ASSIGNMENT_DELETE",

                    "LEARNING_PATH_ASSIGNMENT_VIEW",
                    "LEARNING_PATH_ASSIGNMENT_CREATE",
                    "LEARNING_PATH_ASSIGNMENT_UPDATE",
                    "LEARNING_PATH_ASSIGNMENT_DELETE",

                    // Only Super Admin can manage roles
                    "ROLE_VIEW",
                    "ROLE_CREATE",
                    "ROLE_UPDATE",
                    "ROLE_DELETE",
                    "ROLE_ASSIGN_PERMISSION"
            );

            List<String> managerPermissions = List.of(
                    "EMPLOYEE_VIEW",

                    "DEPARTMENT_VIEW",
                    "POSITION_VIEW",
                    "SUB_TEAM_VIEW",
                    "SPECIALIZATION_VIEW",
                    "SENIORITY_LEVEL_VIEW",
                    "LOCATION_VIEW",

                    "TRAINING_CATALOGUE_VIEW",
                    "TRAINING_ASSIGNMENT_VIEW",
                    "LEARNING_PATH_ASSIGNMENT_VIEW",
                    "MY_TRAINING_VIEW"

            );

            List<String> employeePermissions = List.of(
                    "EMPLOYEE_VIEW",
                    "MY_TRAINING_VIEW",
                    "TRAINING_CATALOGUE_VIEW",
                    "DEPARTMENT_VIEW"
            );

            SecurityRole superAdminRole = createOrUpdateRole(
                    "SUPER_ADMIN",
                    "Super Administrator",
                    "Full platform access including role and permission management",
                    DataScope.ALL,
                    createPermissions(superAdminPermissions)
            );

            SecurityRole adminRole = createOrUpdateRole(
                    "ADMIN",
                    "Administrator",
                    "Full LMS access except role creation and permission management",
                    DataScope.ALL,
                    createPermissions(adminAndHrPermissions)
            );

            SecurityRole hrRole = createOrUpdateRole(
                    "HR",
                    "HR",
                    "HR full access except role creation and permission management",
                    DataScope.ALL,
                    createPermissions(adminAndHrPermissions)
            );

            SecurityRole managerRole = createOrUpdateRole(
                    "MANAGER",
                    "Manager",
                    "Manager access to own team only",
                    DataScope.TEAM,
                    createPermissions(managerPermissions)
            );

            SecurityRole employeeRole = createOrUpdateRole(
                    "EMPLOYEE",
                    "Employee",
                    "Employee access to own profile and own trainings only",
                    DataScope.SELF,
                    createPermissions(employeePermissions)
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
            DataScope dataScope,
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
        role.setGlobal(true);
        role.setDataScope(dataScope);
        role.setOrganization(null);
        role.setPermissions(permissions);

        return roleRepo.save(role);
    }

    private void seedSeniorityLevels(Organization organization) {
        Long organizationId = organization.getId();

        if (!seniorityLevelRepo.findByOrganizationIdOrderByDisplayOrderAsc(organizationId).isEmpty()) {
            return;
        }

        List<SeniorityLevel> levels = List.of(
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Intern")
                        .displayOrder(1)
                        .description("Entry-level position, typically for students or recent graduates.")
                        .active(true)
                        .build(),
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Junior")
                        .displayOrder(2)
                        .description("Early-career position with limited experience.")
                        .active(true)
                        .build(),
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Mid-Level")
                        .displayOrder(3)
                        .description("Professional with solid experience and skills.")
                        .active(true)
                        .build(),
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Senior")
                        .displayOrder(4)
                        .description("Experienced professional, may lead projects or small teams.")
                        .active(true)
                        .build(),
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Lead")
                        .displayOrder(5)
                        .description("Leads teams or major functions.")
                        .active(true)
                        .build(),
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Executive")
                        .displayOrder(6)
                        .description("Strategic leadership and decision-making role.")
                        .active(true)
                        .build()
        );

        seniorityLevelRepo.saveAll(levels);
    }
}
/*package com.fatayriTech.avarLMS.config;

import com.fatayriTech.avarLMS.model.*;
import com.fatayriTech.avarLMS.repository.PermissionRepo;
import com.fatayriTech.avarLMS.repository.SecurityRoleRepo;
import com.fatayriTech.avarLMS.repository.SeniorityLevelRepo;
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
    private final SeniorityLevelRepo seniorityLevelRepo;

    private void seedSeniorityLevels(Organization organization) {
        Long organizationId = organization.getId();

        if (!seniorityLevelRepo.findByOrganizationIdOrderByDisplayOrderAsc(organizationId).isEmpty()) {
            return;
        }

        List<SeniorityLevel> levels = List.of(
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Intern")
                        .displayOrder(1)
                        .description("Entry-level position, typically for students or recent graduates.")
                        .active(true)
                        .build(),
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Junior")
                        .displayOrder(2)
                        .description("Early-career position with limited experience.")
                        .active(true)
                        .build(),
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Mid-Level")
                        .displayOrder(3)
                        .description("Professional with solid experience and skills.")
                        .active(true)
                        .build(),
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Senior")
                        .displayOrder(4)
                        .description("Experienced professional, may lead projects or small teams.")
                        .active(true)
                        .build(),
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Lead")
                        .displayOrder(5)
                        .description("Leads teams or major functions.")
                        .active(true)
                        .build(),
                SeniorityLevel.builder()
                        .organization(organization)
                        .name("Executive")
                        .displayOrder(6)
                        .description("Strategic leadership and decision-making role.")
                        .active(true)
                        .build()
        );

        seniorityLevelRepo.saveAll(levels);
    }
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
}*/
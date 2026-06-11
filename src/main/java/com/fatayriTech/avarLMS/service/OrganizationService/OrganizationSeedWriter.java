package com.fatayriTech.avarLMS.service.OrganizationService;

import com.fatayriTech.avarLMS.model.Permission;
import com.fatayriTech.avarLMS.model.SecurityRole;
import com.fatayriTech.avarLMS.model.UserSecurityRole;
import com.fatayriTech.avarLMS.repository.PermissionRepo;
import com.fatayriTech.avarLMS.repository.SecurityRoleRepo;
import com.fatayriTech.avarLMS.repository.UserSecurityRoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationSeedWriter {

    private final PermissionRepo permissionRepo;
    private final SecurityRoleRepo securityRoleRepo;
    private final UserSecurityRoleRepo userSecurityRoleRepo;

    @Transactional(transactionManager = "mainTransactionManager")
    public void seedPermissionsAndRoles(Long creatorUserId) {

        List<String> adminPermissions = List.of(
                "WORK_ORDER_VIEW",
                "WORK_ORDER_CREATE",
                "WORK_ORDER_UPDATE",
                "WORK_ORDER_DELETE",
                "TERRITORY_VIEW",
                "TERRITORY_CREATE",
                "TERRITORY_UPDATE",
                "TERRITORY_DELETE",
                "USER_MANAGE",
                "DEPARTMENT_VIEW",
                "DEPARTMENT_CREATE",
                "DEPARTMENT_UPDATE",
                "DEPARTMENT_DELETE",
                "POSITION_VIEW",
                "POSITION_CREATE",
                "POSITION_UPDATE",
                "POSITION_DELETE",
                "WORK_ORDER_COMMENT_VIEW",
                "WORK_ORDER_COMMENT_CREATE",
                "WORK_ORDER_COMMENT_UPDATE",
                "WORK_ORDER_COMMENT_DELETE",
                "WORK_ORDER_TASK_VIEW",
                "WORK_ORDER_TASK_CREATE",
                "WORK_ORDER_TASK_UPDATE",
                "WORK_ORDER_TASK_DELETE",
                "ATTACHMENT_DELETE",
                "ATTACHMENT_VIEW",
                "ATTACHMENT_UPLOAD",
                "CHECKLIST_TEMPLATE_VIEW",
                "CHECKLIST_TEMPLATE_CREATE",
                "CHECKLIST_TEMPLATE_UPDATE",
                "CHECKLIST_TEMPLATE_DELETE",
                "CHECKLIST_QUESTION_VIEW",
                "CHECKLIST_QUESTION_CREATE",
                "CHECKLIST_QUESTION_UPDATE",
                "CHECKLIST_QUESTION_DELETE",
                "CHECKLIST_ANSWER_VIEW",
                "CHECKLIST_ANSWER_CREATE",
                "WORK_ORDER_TIME_LOG_VIEW",
                "WORK_ORDER_TIME_LOG_CREATE",
                "WORK_ORDER_TIME_LOG_UPDATE",
                "WORK_ORDER_TIME_LOG_DELETE",
                "EMPLOYEE_BULK_UPLOAD",
                "EMPLOYEE_VIEW",
                "EMPLOYEE_CREATE",
                "EMPLOYEE_UPDATE",
                "EMPLOYEE_DELETE"
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

        SecurityRole adminRole = securityRoleRepo.findByCode("ADMIN")
                .orElseGet(() -> {
                    SecurityRole role = new SecurityRole();
                    role.setName("Administrator");
                    role.setCode("ADMIN");
                    role.setDescription("Full access to all organization modules");
                    role.setActive(true);
                    return securityRoleRepo.save(role);
                });

        adminRole.setPermissions(permissions);
        securityRoleRepo.save(adminRole);

        UserSecurityRole userSecurityRole = new UserSecurityRole();
        userSecurityRole.setUserId(creatorUserId);
        userSecurityRole.setRole(adminRole);
        userSecurityRoleRepo.save(userSecurityRole);
    }
}
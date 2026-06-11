package com.fatayriTech.avarLMS.service.security;

import com.fatayriTech.avarLMS.dto.security.SecurityGroupDetailsDto;
import com.fatayriTech.avarLMS.dto.security.SecurityGroupListDto;
import com.fatayriTech.avarLMS.dto.security.SecurityGroupPermissionDto;
import com.fatayriTech.avarLMS.dto.security.SecurityGroupUserDto;
import com.fatayriTech.avarLMS.model.User;
import com.fatayriTech.avarLMS.model.Permission;
import com.fatayriTech.avarLMS.model.SecurityRole;
import com.fatayriTech.avarLMS.model.UserSecurityRole;
import com.fatayriTech.avarLMS.repository.PermissionRepo;
import com.fatayriTech.avarLMS.repository.SecurityRoleRepo;
import com.fatayriTech.avarLMS.repository.UserSecurityRoleRepo;
import com.fatayriTech.avarLMS.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityGroupServiceImpl implements SecurityGroupService {

    private final SecurityRoleRepo securityRoleRepo;
    private final PermissionRepo permissionRepo;
    private final UserSecurityRoleRepo userSecurityRoleRepo;
    private final UserRepo userRepo;

    @Override
    public List<SecurityGroupListDto> getAllSecurityGroups() {
        return securityRoleRepo.findAll()
                .stream()
                .map(this::mapToListDto)
                .toList();
    }

    @Override
    public SecurityGroupDetailsDto getSecurityGroupById(Long id) {
        SecurityRole role = securityRoleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Security group not found with id: " + id));

        List<SecurityGroupPermissionDto> permissions = role.getPermissions()
                .stream()
                .map(this::mapPermissionToDto)
                .toList();

        List<SecurityGroupUserDto> users = userSecurityRoleRepo.findByRoleId(role.getId())
                .stream()
                .map(UserSecurityRole::getUserId)
                .map(userRepo::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(this::mapUserToDto)
                .toList();

        return new SecurityGroupDetailsDto(
                role.getId(),
                role.getName(),
                role.getCode(),
                role.getDescription(),
                role.isActive() ? "ACTIVE" : "INACTIVE",
                users.size(),
                permissions.size(),
                role.getCreationDate(),
                role.getModifiedDate(),
                permissions,
                users
        );
    }

    @Override
    public List<SecurityGroupUserDto> getAvailableUsers(Long groupId) {
        securityRoleRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Security group not found with id: " + groupId));

        List<Long> assignedUserIds = userSecurityRoleRepo.findByRoleId(groupId)
                .stream()
                .map(UserSecurityRole::getUserId)
                .toList();

        return userRepo.findAll()
                .stream()
                .filter(user -> !assignedUserIds.contains(user.getId()))
                .map(this::mapUserToDto)
                .toList();
    }

    @Override
    public void addUserToSecurityGroup(Long groupId, Long userId) {
        SecurityRole role = securityRoleRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Security group not found with id: " + groupId));

        userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        boolean exists = userSecurityRoleRepo.existsByUserIdAndRoleId(userId, groupId);

        if (!exists) {
            UserSecurityRole userSecurityRole = new UserSecurityRole();
            userSecurityRole.setUserId(userId);
            userSecurityRole.setRole(role);
            userSecurityRoleRepo.save(userSecurityRole);
        }
    }

    @Override
    public void removeUserFromSecurityGroup(Long groupId, Long userId) {
        userSecurityRoleRepo.deleteByUserIdAndRoleId(userId, groupId);
    }

    @Override
    public List<SecurityGroupPermissionDto> getAvailablePermissions(Long groupId) {
        securityRoleRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Security group not found with id: " + groupId));

        return permissionRepo.findPermissionsNotInSecurityGroup(groupId)
                .stream()
                .map(this::mapPermissionToDto)
                .toList();
    }

    @Override
    public void addPermissionToSecurityGroup(Long groupId, Long permissionId) {
        SecurityRole role = securityRoleRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Security group not found with id: " + groupId));

        Permission permission = permissionRepo.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        role.getPermissions().add(permission);
        securityRoleRepo.save(role);
    }

    @Override
    public void removePermissionFromSecurityGroup(Long groupId, Long permissionId) {
        SecurityRole role = securityRoleRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Security group not found with id: " + groupId));

        Permission permission = permissionRepo.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        role.getPermissions().remove(permission);
        securityRoleRepo.save(role);
    }

    private SecurityGroupListDto mapToListDto(SecurityRole role) {
        return new SecurityGroupListDto(
                role.getId(),
                role.getName(),
                role.getCode(),
                role.getDescription(),
                role.isActive() ? "ACTIVE" : "INACTIVE",
                userSecurityRoleRepo.countByRoleId(role.getId()),
                role.getPermissions().size()
        );
    }

    private SecurityGroupPermissionDto mapPermissionToDto(Permission permission) {
        return new SecurityGroupPermissionDto(
                permission.getId(),
                permission.getName(),
                permission.getDescription(),
                extractModule(permission.getName()),
                extractType(permission.getName()),
                "GRANTED"
        );
    }

    private SecurityGroupUserDto mapUserToDto(User user) {
        String fullName = String.join(" ",
                safe(user.getFirstName()),
                safe(user.getMiddleName()),
                safe(user.getLastName())
        ).trim();

        if (fullName.isBlank()) {
            fullName = user.getUsername();
        }

        return new SecurityGroupUserDto(
                user.getId(),
                user.getUsername(),
                fullName,
                user.getEmail(),
                user.isConfirmed() ? "ACTIVE" : "INACTIVE"
        );
    }

    private String extractModule(String permissionName) {
        if (permissionName == null || !permissionName.contains("_")) {
            return "GENERAL";
        }

        String[] parts = permissionName.split("_");

        if (parts.length >= 2 && parts[0].equals("WORK") && parts[1].equals("ORDER")) {
            return "WORK_ORDER";
        }

        if (parts.length >= 2 && parts[0].equals("CHECKLIST")) {
            return "CHECKLIST";
        }

        return parts[0];
    }

    private String extractType(String permissionName) {
        if (permissionName == null || !permissionName.contains("_")) {
            return "GENERAL";
        }

        String[] parts = permissionName.split("_");
        return parts[parts.length - 1];
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
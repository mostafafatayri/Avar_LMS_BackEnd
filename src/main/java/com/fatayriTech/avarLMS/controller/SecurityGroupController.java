package com.fatayriTech.avarLMS.controller;



import com.fatayriTech.avarLMS.dto.security.SecurityGroupDetailsDto;
import com.fatayriTech.avarLMS.dto.security.SecurityGroupListDto;
import com.fatayriTech.avarLMS.dto.security.SecurityGroupPermissionDto;
import com.fatayriTech.avarLMS.dto.security.SecurityGroupUserDto;
import com.fatayriTech.avarLMS.service.security.SecurityGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/security-groups")
@RequiredArgsConstructor
public class SecurityGroupController {

    private final SecurityGroupService securityGroupService;

    @GetMapping
    public List<SecurityGroupListDto> getAllSecurityGroups() {
        return securityGroupService.getAllSecurityGroups();
    }

    @GetMapping("/{id}")
    public SecurityGroupDetailsDto getSecurityGroupById(@PathVariable Long id) {
        return securityGroupService.getSecurityGroupById(id);
    }
    @GetMapping("/{groupId}/available-users")
    public List<SecurityGroupUserDto> getAvailableUsers(
            @PathVariable Long groupId
    ) {
        return securityGroupService.getAvailableUsers(groupId);
    }

    @PostMapping("/{groupId}/users/{userId}")
    public void addUserToSecurityGroup(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        securityGroupService.addUserToSecurityGroup(groupId, userId);
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    public void removeUserFromSecurityGroup(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        securityGroupService.removeUserFromSecurityGroup(groupId, userId);
    }

    @GetMapping("/{groupId}/available-permissions")
    public List<SecurityGroupPermissionDto> getAvailablePermissions(
            @PathVariable Long groupId
    ) {
        return securityGroupService.getAvailablePermissions(groupId);
    }



    @PostMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<String> addPermissionToSecurityGroup(
            @PathVariable Long groupId,
            @PathVariable Long permissionId
    ) {
        securityGroupService.addPermissionToSecurityGroup(groupId, permissionId);

        return ResponseEntity.ok("Permission assigned successfully");
    }

    @DeleteMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<String> removePermissionFromSecurityGroup(
            @PathVariable Long groupId,
            @PathVariable Long permissionId
    ) {
        securityGroupService.removePermissionFromSecurityGroup(groupId, permissionId);
        return ResponseEntity.ok("Permission removed  successfully");
    }
}
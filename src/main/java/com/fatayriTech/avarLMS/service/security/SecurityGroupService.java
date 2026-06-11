package com.fatayriTech.avarLMS.service.security;

import com.fatayriTech.avarLMS.dto.security.SecurityGroupDetailsDto;
import com.fatayriTech.avarLMS.dto.security.SecurityGroupListDto;
import com.fatayriTech.avarLMS.dto.security.SecurityGroupPermissionDto;
import com.fatayriTech.avarLMS.dto.security.SecurityGroupUserDto;

import java.util.List;

public interface SecurityGroupService {

    List<SecurityGroupListDto> getAllSecurityGroups();
    SecurityGroupDetailsDto getSecurityGroupById(Long id);

    List<SecurityGroupUserDto> getAvailableUsers(Long groupId);

    void addUserToSecurityGroup(Long groupId, Long userId);

    void removeUserFromSecurityGroup(Long groupId, Long userId);

    List<SecurityGroupPermissionDto> getAvailablePermissions(Long groupId);

    void addPermissionToSecurityGroup(Long groupId, Long permissionId);

    void removePermissionFromSecurityGroup(Long groupId, Long permissionId);
}
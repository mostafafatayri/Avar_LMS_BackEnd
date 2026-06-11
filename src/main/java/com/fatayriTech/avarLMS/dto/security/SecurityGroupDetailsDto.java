package com.fatayriTech.avarLMS.dto.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SecurityGroupDetailsDto {

    private Long id;
    private String name;
    private String code;
    private String description;
    private String status;
    private long usersCount;
    private long permissionsCount;
    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;

    private List<SecurityGroupPermissionDto> permissions;
    private List<SecurityGroupUserDto> users;
}
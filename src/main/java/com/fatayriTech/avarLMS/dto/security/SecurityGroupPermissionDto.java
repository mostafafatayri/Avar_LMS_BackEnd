package com.fatayriTech.avarLMS.dto.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecurityGroupPermissionDto {

    private Long id;
    private String name;
    private String description;
    private String module;
    private String type;
    private String status;
}
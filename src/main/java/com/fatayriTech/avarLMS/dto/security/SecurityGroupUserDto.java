package com.fatayriTech.avarLMS.dto.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecurityGroupUserDto {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String status;
}
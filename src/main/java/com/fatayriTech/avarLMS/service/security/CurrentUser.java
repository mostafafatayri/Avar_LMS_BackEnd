package com.fatayriTech.avarLMS.service.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrentUser {

    private Long userId;

    private Long employeeId;

    private Long organizationId;

    private String email;

    private String username;
}
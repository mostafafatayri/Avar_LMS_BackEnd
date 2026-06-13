package com.fatayriTech.avarLMS.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationDomainUserResponse {

    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String fullName;
}
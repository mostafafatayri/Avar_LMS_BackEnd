package com.fatayriTech.avarLMS.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrganizationDomainUserResponse {

    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String fullName;

    private Boolean hasOrganizationAccess;
}
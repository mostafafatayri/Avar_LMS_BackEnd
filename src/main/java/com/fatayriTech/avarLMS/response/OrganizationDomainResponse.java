package com.fatayriTech.avarLMS.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationDomainResponse {

    private Long id;

    private String domain;

    private boolean allowed;
}
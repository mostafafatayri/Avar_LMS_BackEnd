package com.fatayriTech.avarLMS.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationDomainRequest {

    private String domain;

    private Boolean allowed;
}
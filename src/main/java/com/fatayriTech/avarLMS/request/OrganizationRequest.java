package com.fatayriTech.avarLMS.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrganizationRequest {

    private String code;

    private String name;

    private String industry;

    private String contactEmail;

    private String contactPhone;

    private String logoUrl;

    private Boolean active;

    private LocalDate licenseStartDate;

    private LocalDate licenseEndDate;

    private Integer maxUsers;
}
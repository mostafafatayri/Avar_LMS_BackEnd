package com.fatayriTech.avarLMS.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OrganizationResponse {

    private Long id;

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

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;
}
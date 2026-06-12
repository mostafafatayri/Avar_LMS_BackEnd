package com.fatayriTech.avarLMS.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrganizationCardDto {

    private Long id;
    private String name;
    private String code;
    private String logoUrl;
    private String industry;
}
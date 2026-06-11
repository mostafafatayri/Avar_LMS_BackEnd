package com.fatayriTech.avarLMS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrganizationRequest {
    private String name;
    private String slug;
    private String color;
    private String databaseName;
}
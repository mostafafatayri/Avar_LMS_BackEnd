package com.fatayriTech.avarLMS.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCardDto {

    private Long id;
    private String name;
    private String slug;
    private String role;
    private Long usersCount;
    private Long buildingsCount;
    private String color;

    public String getSlug() {
        return slug;
    }

    public String getColor() {
        return color;
    }
}
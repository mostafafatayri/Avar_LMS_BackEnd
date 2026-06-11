package com.fatayriTech.avarLMS.dto.security;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecurityGroupListDto {

    private Long id;
    private String name;
    private String code;
    private String description;
    private String status;
    private long usersCount;
    private long permissionsCount;
}
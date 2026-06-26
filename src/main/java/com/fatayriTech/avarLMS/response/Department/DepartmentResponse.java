package com.fatayriTech.avarLMS.response.Department;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DepartmentResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private boolean active;

    private Long headEmployeeId;
    private String headName;
    private String headEmail;

    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;
}
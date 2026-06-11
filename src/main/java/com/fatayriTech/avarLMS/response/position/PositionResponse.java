package com.fatayriTech.avarLMS.response.position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
public class PositionResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private boolean active;

    private Long departmentId;

    private String departmentName;

    private LocalDateTime creationDate;

    private LocalDateTime modifiedDate;
}
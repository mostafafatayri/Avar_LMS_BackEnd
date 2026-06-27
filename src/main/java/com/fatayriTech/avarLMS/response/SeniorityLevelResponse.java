package com.fatayriTech.avarLMS.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeniorityLevelResponse {

    private Long id;

    private String name;

    private Integer displayOrder;

    private String description;

    private boolean active;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;
}
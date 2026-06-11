package com.fatayriTech.avarLMS.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NationalityResponse {
    private Long id;
    private String name;
    private String code;
    private boolean active;
}
package com.fatayriTech.avarLMS.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NationalityRequest {
    private String name;
    private String code;
    private boolean active = true;
}
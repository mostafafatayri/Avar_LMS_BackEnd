package com.fatayriTech.avarLMS.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignupRequest {
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
}

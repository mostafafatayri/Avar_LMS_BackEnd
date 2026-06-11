package com.fatayriTech.avarLMS.response.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InviteValidationResponse {
    private Long employeeId;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
}
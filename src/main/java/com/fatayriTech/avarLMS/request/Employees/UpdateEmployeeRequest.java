package com.fatayriTech.avarLMS.request.Employees;

import com.fatayriTech.avarLMS.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmployeeRequest {
    private String employeeId;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;

    private Long departmentId;
    private Long positionId;
    private Long managerId;

    private Long territoryId;
    private Long nationalityId;

    private Gender gender;
    private String address;

    private String phoneNumber;
    private boolean active;
}
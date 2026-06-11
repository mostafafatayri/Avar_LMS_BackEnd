package com.fatayriTech.avarLMS.response.employee;

import com.fatayriTech.avarLMS.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String employeeId;

    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;

    private Long departmentId;
    private String departmentName;

    private Long positionId;
    private String positionName;

    private Long managerId;
    private String managerName;

    private Long nationalityId;
    private String nationalityName;

    private Gender gender;

    private String phoneNumber;
    private boolean active;

    private Long appUserId;
    private String username;

    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;
}
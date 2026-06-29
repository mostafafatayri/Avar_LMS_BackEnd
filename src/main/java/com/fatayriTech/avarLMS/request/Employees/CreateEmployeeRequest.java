package com.fatayriTech.avarLMS.request.Employees;

import com.fatayriTech.avarLMS.enums.AcademyStatus;
import com.fatayriTech.avarLMS.enums.EmployeeType;
import com.fatayriTech.avarLMS.enums.EmploymentStatus;
import com.fatayriTech.avarLMS.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmployeeRequest {
    private String employeeId;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;

    private Long departmentId;
    private Long subTeamId;
    private Long positionId;
    private Long specializationId;
    private Long seniorityLevelId;
    private Long locationId;
    private Long managerId;

    private Long nationalityId;

    private Gender gender;
    private String phoneNumber;

    private EmploymentStatus employmentStatus;
    private AcademyStatus academyStatus;
    private EmployeeType employeeType;
}
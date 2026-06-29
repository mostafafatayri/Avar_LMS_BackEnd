package com.fatayriTech.avarLMS.response.employee;

import com.fatayriTech.avarLMS.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.fatayriTech.avarLMS.enums.AcademyStatus;
import com.fatayriTech.avarLMS.enums.EmployeeType;
import com.fatayriTech.avarLMS.enums.EmploymentStatus;
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

    private String primaryAddress;

    private Gender gender;

    private String phoneNumber;
    private boolean active;

    private Long appUserId;
    private String username;
    private Long subTeamId;
    private String subTeamName;

    private Long specializationId;
    private String specializationName;

    private Long seniorityLevelId;
    private String seniorityLevelName;

    private Long locationId;
    private String locationName;

    private EmploymentStatus employmentStatus;
    private AcademyStatus academyStatus;
    private EmployeeType employeeType;
    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;
}
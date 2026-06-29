package com.fatayriTech.avarLMS.response.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeBulkUploadFailedRow {
    private int rowNumber;
    private String employeeId;
    private String email;
    private String reason;
}
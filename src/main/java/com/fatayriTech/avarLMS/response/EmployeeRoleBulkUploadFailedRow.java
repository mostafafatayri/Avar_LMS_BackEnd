package com.fatayriTech.avarLMS.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeRoleBulkUploadFailedRow {

    private int rowNumber;
    private String name;
    private String departmentCode;
    private String reason;
}
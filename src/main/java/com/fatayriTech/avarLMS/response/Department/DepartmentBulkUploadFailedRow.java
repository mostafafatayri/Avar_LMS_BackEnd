package com.fatayriTech.avarLMS.response.Department;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepartmentBulkUploadFailedRow {
    private int rowNumber;
    private String name;
    private String code;
    private String reason;
}
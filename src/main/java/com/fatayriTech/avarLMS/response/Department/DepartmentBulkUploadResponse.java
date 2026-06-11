package com.fatayriTech.avarLMS.response.Department;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepartmentBulkUploadResponse {
    private int totalRows;
    private int insertedRows;
    private int failedRows;
}
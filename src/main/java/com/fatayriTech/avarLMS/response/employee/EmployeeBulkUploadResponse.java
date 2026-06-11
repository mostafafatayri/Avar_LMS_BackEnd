package com.fatayriTech.avarLMS.response.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EmployeeBulkUploadResponse {
    private int totalRows;
    private int inserted;
    private int skipped;
    private List<String> errors;
}
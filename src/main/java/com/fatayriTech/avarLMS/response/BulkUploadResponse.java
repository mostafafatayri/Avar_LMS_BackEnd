package com.fatayriTech.avarLMS.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BulkUploadResponse {
    private int totalRows;  //// 100
    private int insertedRows;
    private int failedRows;
}

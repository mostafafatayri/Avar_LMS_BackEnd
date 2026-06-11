package com.fatayriTech.avarLMS.response.position;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionBulkUploadResponse {

    private int totalRows;
    private int insertedRows;
    private int failedRows;
}
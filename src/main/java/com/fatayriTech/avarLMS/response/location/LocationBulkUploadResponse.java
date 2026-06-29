package com.fatayriTech.avarLMS.response.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LocationBulkUploadResponse {

    private int totalRows;
    private int insertedRows;
    private int failedRows;

    private List<LocationBulkUploadFailedRow> failedRecords;
}
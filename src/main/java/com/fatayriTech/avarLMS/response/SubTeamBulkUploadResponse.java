package com.fatayriTech.avarLMS.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SubTeamBulkUploadResponse {
    private int totalRows;
    private int insertedRows;
    private int failedRows;
    private List<SubTeamBulkUploadFailedRow> failedRecords;
}
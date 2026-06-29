package com.fatayriTech.avarLMS.response.training;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TrainingCatalogueBulkUploadResponse {

    private int totalRows;
    private int insertedRows;
    private int failedRows;

    private List<TrainingCatalogueBulkUploadFailedRow> failedRecords;
}
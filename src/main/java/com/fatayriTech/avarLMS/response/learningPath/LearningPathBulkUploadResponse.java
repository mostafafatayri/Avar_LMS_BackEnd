package com.fatayriTech.avarLMS.response.learningPath;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LearningPathBulkUploadResponse {
    private int totalRows;
    private int insertedRows;
    private int failedRows;
    private List<LearningPathBulkUploadFailedRow> failedRecords;
}
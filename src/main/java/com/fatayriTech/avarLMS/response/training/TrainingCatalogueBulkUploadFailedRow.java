package com.fatayriTech.avarLMS.response.training;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrainingCatalogueBulkUploadFailedRow {

    private int rowNumber;
    private String title;
    private String trainingType;
    private String reason;
}
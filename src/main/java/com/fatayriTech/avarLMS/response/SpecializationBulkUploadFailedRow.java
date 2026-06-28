package com.fatayriTech.avarLMS.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpecializationBulkUploadFailedRow {

    private int rowNumber;
    private String name;
    private String code;
    private String reason;
}
package com.fatayriTech.avarLMS.response.position;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionBulkUploadFailedRow {

    private int rowNumber;
    private String name;
    private String code;
    private String reason;
}
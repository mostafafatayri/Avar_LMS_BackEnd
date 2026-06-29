package com.fatayriTech.avarLMS.response.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationBulkUploadFailedRow {

    private int rowNumber;
    private String name;
    private String code;
    private String reason;
}
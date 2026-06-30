package com.fatayriTech.avarLMS.response.learningPath;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LearningPathBulkUploadFailedRow {
    private int rowNumber;
    private String name;
    private String module;
    private String reason;
}
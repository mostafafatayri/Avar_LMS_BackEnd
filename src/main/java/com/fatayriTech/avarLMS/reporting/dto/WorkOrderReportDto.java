package com.fatayriTech.avar.reporting.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkOrderReportDto {

    private Long id;
    private String code;
    private String title;
    private String description;

    private String status;
    private String priority;
    private String category;

    private String siteName;
    private String assetName;

    private String requestedBy;
    private String assignedTo;

    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
}
package com.fatayriTech.avarLMS.response.training;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingLectureAttachmentResponse {
    private Long id;
    private Long organizationId;
    private Long lectureId;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String fileUrl;
    private Boolean active;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
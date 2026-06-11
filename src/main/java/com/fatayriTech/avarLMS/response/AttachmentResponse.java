package com.fatayriTech.avarLMS.response;

import com.fatayriTech.avarLMS.enums.AttachmentType;
import com.fatayriTech.avarLMS.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor

public class AttachmentResponse {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private AttachmentType attachmentType;
    private EntityType entityType;
    private Long entityId;
    private LocalDateTime createdDate;
}
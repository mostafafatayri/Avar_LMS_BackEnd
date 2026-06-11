package com.fatayriTech.avarLMS.model;

import com.fatayriTech.avarLMS.enums.AttachmentType;
import com.fatayriTech.avarLMS.enums.EntityType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Getter
@Setter
public class Attachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;

    private String fileType;

    @Enumerated(EnumType.STRING)
    private AttachmentType attachmentType;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private Long entityId;

    private Long uploadedBy;

    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}
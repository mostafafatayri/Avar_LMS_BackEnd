package com.fatayriTech.avarLMS.service.AttachmentSerivce;

import com.fatayriTech.avarLMS.enums.EntityType;
import com.fatayriTech.avarLMS.model.Attachments;
import com.fatayriTech.avarLMS.repository.AttachmentRepos.AttachmentRepo;
import com.fatayriTech.avarLMS.response.AttachmentResponse;
import com.fatayriTech.avarLMS.service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepo attachmentRepo;

    public List<AttachmentResponse> getAttachmentsByEntity(
            EntityType entityType,
            Long entityId
    ) {
        return attachmentRepo
                .findByEntityTypeAndEntityIdOrderByCreatedDateDesc(entityType, entityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AttachmentResponse mapToResponse(Attachments attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getFileUrl(),
                attachment.getFileType(),
                attachment.getAttachmentType(),
                attachment.getEntityType(),
                attachment.getEntityId(),
                attachment.getCreatedDate()
        );
    }
    public void deleteAttachment(Long attachmentId) {

        CurrentUser currentUser =
                (CurrentUser) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        Long employeeId = currentUser.getEmployeeId();

        Attachments attachment = attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        if (attachment.getUploadedBy() == null ||
                !attachment.getUploadedBy().equals(employeeId)) {
            throw new RuntimeException("You are not allowed to delete this attachment");
        }

        attachmentRepo.delete(attachment);
    }
}

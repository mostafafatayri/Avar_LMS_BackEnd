package com.fatayriTech.avarLMS.controller;


import com.fatayriTech.avarLMS.enums.AttachmentType;
import com.fatayriTech.avarLMS.enums.EntityType;
import com.fatayriTech.avarLMS.response.AttachmentResponse;
import com.fatayriTech.avarLMS.service.AttachmentSerivce.AttachmentService;
import com.fatayriTech.avarLMS.service.AttachmentSerivce.SupabaseStorageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
// for future grabage collector a job that will ru and make each time an entity id is 1 we delete the record
@RestController
@RequestMapping("${api.prefix}/attachments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AttachmentController {

    private final SupabaseStorageService storageService;
    private final AttachmentService attachmentService ;

@PostMapping("/upload")
public AttachmentResponse uploadAttachment(
        @RequestParam("file") MultipartFile file,
        @RequestParam("attachmentType") AttachmentType attachmentType,
        @RequestParam("entityType") EntityType entityType,
        @RequestParam(required = false) Long entityId
) {
    System.out.println("the appi received");
    return storageService.uploadFile(file, attachmentType, entityType, entityId);
}

    @GetMapping("/entity/{entityType}/{entityId}")
    public List<AttachmentResponse> getAttachmentsByEntity(
            @PathVariable EntityType entityType,
            @PathVariable Long entityId
    ) {
        return attachmentService.getAttachmentsByEntity(entityType, entityId);
    }

    @PreAuthorize("hasAuthority('ATTACHMENT_DELETE')")
    @DeleteMapping("/{id}")
    public String deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return "Attachment deleted successfully";
    }

}
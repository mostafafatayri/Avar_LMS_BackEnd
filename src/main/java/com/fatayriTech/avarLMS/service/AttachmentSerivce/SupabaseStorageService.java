package com.fatayriTech.avarLMS.service.AttachmentSerivce;
import com.fatayriTech.avarLMS.enums.AttachmentType;
import com.fatayriTech.avarLMS.enums.EntityType;
import com.fatayriTech.avarLMS.model.Attachments;
import com.fatayriTech.avarLMS.repository.AttachmentRepos.AttachmentRepo;
import com.fatayriTech.avarLMS.response.AttachmentResponse;
import com.fatayriTech.avarLMS.service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {
    private final AttachmentRepo attachmentRepo;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Value("${supabase.bucket}")
    private String bucket;


   public AttachmentResponse uploadFile(
           MultipartFile file,
           AttachmentType attachmentType,
           EntityType entityType,
           Long entityId
   ) {
       try {
           String originalName = file.getOriginalFilename();
           String fileName = UUID.randomUUID() + "-" + originalName;

           String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

           HttpHeaders headers = new HttpHeaders();
           headers.setBearerAuth(serviceRoleKey);
           headers.setContentType(MediaType.parseMediaType(file.getContentType()));

           HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

           ResponseEntity<String> response = new RestTemplate().exchange(
                   uploadUrl,
                   HttpMethod.POST,
                   requestEntity,
                   String.class
           );

           if (!response.getStatusCode().is2xxSuccessful()) {
               throw new RuntimeException("Failed to upload file to Supabase");
           }

           String fileUrl = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;

           Attachments attachment = new Attachments();
           attachment.setFileName(fileName);
           attachment.setFileUrl(fileUrl);
           attachment.setFileType(file.getContentType());
           attachment.setAttachmentType(attachmentType);
           attachment.setEntityType(entityType);
           attachment.setEntityId(entityId);

           // Get Employee :
           CurrentUser currentUser =
                   (CurrentUser) SecurityContextHolder.getContext()
                           .getAuthentication()
                           .getPrincipal();

           attachment.setUploadedBy(currentUser.getEmployeeId());



           Attachments saved = attachmentRepo.save(attachment);

           return new AttachmentResponse(
                   saved.getId(),
                   saved.getFileName(),
                   saved.getFileUrl(),
                   saved.getFileType(),
                   saved.getAttachmentType(),
                   saved.getEntityType(),
                   saved.getEntityId(),
                   saved.getCreatedDate()
           );

       } catch (Exception e) {
           throw new RuntimeException("File upload failed: " + e.getMessage());
       }
   }

}
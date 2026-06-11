package com.fatayriTech.avarLMS.repository.AttachmentRepos;

import com.fatayriTech.avarLMS.enums.EntityType;
import com.fatayriTech.avarLMS.model.Attachments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepo extends JpaRepository<Attachments, Long> {
    List<Attachments> findByEntityTypeAndEntityIdOrderByCreatedDateDesc(
            EntityType entityType,
            Long entityId
    );
    boolean existsByIdAndUploadedBy(Long id, Long uploadedBy);
}
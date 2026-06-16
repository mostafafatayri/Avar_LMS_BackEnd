package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.model.TrainingLecture;
import com.fatayriTech.avarLMS.model.TrainingLectureAttachment;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.repository.TrainingLectureAttachmentRepo;
import com.fatayriTech.avarLMS.repository.TrainingLectureRepo;
import com.fatayriTech.avarLMS.request.training.TrainingLectureRequest;
import com.fatayriTech.avarLMS.response.AttachmentResponse;
import com.fatayriTech.avarLMS.response.training.TrainingLectureAttachmentResponse;
import com.fatayriTech.avarLMS.response.training.TrainingLectureResponse;
import com.fatayriTech.avarLMS.service.AttachmentSerivce.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fatayriTech.avarLMS.response.AttachmentResponse;
import com.fatayriTech.avarLMS.enums.AttachmentType;
import com.fatayriTech.avarLMS.enums.EntityType;
import com.fatayriTech.avarLMS.service.AttachmentSerivce.SupabaseStorageService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingLectureService {

    private final TrainingLectureRepo trainingLectureRepo;
    private final TrainingLectureAttachmentRepo attachmentRepo;
    private final TrainingCatalogueRepo trainingCatalogueRepo;
    private final SupabaseStorageService supabaseStorageService;
    public List<TrainingLectureResponse> getAll(Long organizationId, Long trainingId) {
        return trainingLectureRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
                        organizationId,
                        trainingId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TrainingLectureResponse create(
            Long organizationId,
            Long trainingId,
            TrainingLectureRequest request
    ) {
        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationId(trainingId, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        TrainingLecture lecture = TrainingLecture.builder()
                .organizationId(organizationId)
                .trainingCatalogue(training)
                .title(request.getTitle())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .active(true)
                .build();

        return mapToResponse(trainingLectureRepo.save(lecture));
    }

    public TrainingLectureResponse update(
            Long organizationId,
            Long trainingId,
            Long lectureId,
            TrainingLectureRequest request
    ) {
        TrainingLecture lecture = findLecture(organizationId, trainingId, lectureId);

        lecture.setTitle(request.getTitle());
        lecture.setDescription(request.getDescription());
        lecture.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        return mapToResponse(trainingLectureRepo.save(lecture));
    }

    public void delete(Long organizationId, Long trainingId, Long lectureId) {
        TrainingLecture lecture = findLecture(organizationId, trainingId, lectureId);
        lecture.setActive(false);
        trainingLectureRepo.save(lecture);
    }


    public TrainingLectureAttachmentResponse uploadAttachment(
            Long organizationId,
            Long trainingId,
            Long lectureId,
            MultipartFile file
    ) {
        TrainingLecture lecture = findLecture(organizationId, trainingId, lectureId);

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        AttachmentResponse uploadedFile = supabaseStorageService.uploadFile(
                file,
                AttachmentType.TRAINING_MATERIAL,
                EntityType.TRAINING_LECTURE,
                lectureId
        );

        String fileUrl = uploadedFile.getFileUrl();

        TrainingLectureAttachment attachment = TrainingLectureAttachment.builder()
                .organizationId(organizationId)
                .lecture(lecture)
                .fileName(fileName)
                .contentType(contentType)
                .fileSize(file.getSize())
                .fileUrl(fileUrl)
                .active(true)
                .build();

        return mapAttachmentToResponse(attachmentRepo.save(attachment));
    }

    public void deleteAttachment(
            Long organizationId,
            Long trainingId,
            Long lectureId,
            Long attachmentId
    ) {
        findLecture(organizationId, trainingId, lectureId);

        TrainingLectureAttachment attachment = attachmentRepo
                .findByIdAndOrganizationIdAndLectureId(
                        attachmentId,
                        organizationId,
                        lectureId
                )
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        attachment.setActive(false);
        attachmentRepo.save(attachment);
    }

    private TrainingLecture findLecture(Long organizationId, Long trainingId, Long lectureId) {
        return trainingLectureRepo
                .findByIdAndOrganizationIdAndTrainingCatalogueId(
                        lectureId,
                        organizationId,
                        trainingId
                )
                .orElseThrow(() -> new RuntimeException("Lecture not found"));
    }

    private TrainingLectureResponse mapToResponse(TrainingLecture lecture) {
        List<TrainingLectureAttachmentResponse> attachments = attachmentRepo
                .findByOrganizationIdAndLectureIdAndActiveTrue(
                        lecture.getOrganizationId(),
                        lecture.getId()
                )
                .stream()
                .map(this::mapAttachmentToResponse)
                .toList();

        return TrainingLectureResponse.builder()
                .id(lecture.getId())
                .organizationId(lecture.getOrganizationId())
                .trainingCatalogueId(lecture.getTrainingCatalogue().getId())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .displayOrder(lecture.getDisplayOrder())
                .active(lecture.getActive())
                .creationDate(lecture.getCreationDate())
                .modificationDate(lecture.getModificationDate())
                .attachments(attachments)
                .build();
    }

    private TrainingLectureAttachmentResponse mapAttachmentToResponse(
            TrainingLectureAttachment attachment
    ) {
        return TrainingLectureAttachmentResponse.builder()
                .id(attachment.getId())
                .organizationId(attachment.getOrganizationId())
                .lectureId(attachment.getLecture().getId())
                .fileName(attachment.getFileName())
                .contentType(attachment.getContentType())
                .fileSize(attachment.getFileSize())
                .fileUrl(attachment.getFileUrl())
                .active(attachment.getActive())
                .creationDate(attachment.getCreationDate())
                .modificationDate(attachment.getModificationDate())
                .build();
    }
}
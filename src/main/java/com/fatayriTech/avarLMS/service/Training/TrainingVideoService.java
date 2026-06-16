package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.enums.AttachmentType;
import com.fatayriTech.avarLMS.enums.EntityType;
import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.model.TrainingVideo;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.repository.TrainingVideoRepo;
import com.fatayriTech.avarLMS.request.training.TrainingVideoRequest;
import com.fatayriTech.avarLMS.response.AttachmentResponse;
import com.fatayriTech.avarLMS.response.training.TrainingVideoResponse;
import com.fatayriTech.avarLMS.service.AttachmentSerivce.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingVideoService {

    private final TrainingVideoRepo trainingVideoRepo;
    private final TrainingCatalogueRepo trainingCatalogueRepo;
    private final SupabaseStorageService supabaseStorageService;

    public List<TrainingVideoResponse> getAll(
            Long organizationId,
            Long trainingId
    ) {

        return trainingVideoRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
                        organizationId,
                        trainingId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TrainingVideoResponse create(
            Long organizationId,
            Long trainingId,
            TrainingVideoRequest request
    ) {

        TrainingCatalogue training =
                trainingCatalogueRepo
                        .findByIdAndOrganizationId(trainingId, organizationId)
                        .orElseThrow(() -> new RuntimeException("Training not found"));

        TrainingVideo video = TrainingVideo.builder()
                .organizationId(organizationId)
                .trainingCatalogue(training)
                .title(request.getTitle())
                .description(request.getDescription())
                .videoType(request.getVideoType())
                .videoUrl(request.getVideoUrl())
                .thumbnailUrl(request.getThumbnailUrl())
                .durationMinutes(request.getDurationMinutes())
                .displayOrder(request.getDisplayOrder())
                .active(true)
                .build();

        return mapToResponse(trainingVideoRepo.save(video));
    }

    public TrainingVideoResponse update(
            Long organizationId,
            Long trainingId,
            Long videoId,
            TrainingVideoRequest request
    ) {

        TrainingVideo video =
                findVideo(organizationId, trainingId, videoId);

        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setVideoType(request.getVideoType());
        video.setVideoUrl(request.getVideoUrl());
        video.setThumbnailUrl(request.getThumbnailUrl());
        video.setDurationMinutes(request.getDurationMinutes());
        video.setDisplayOrder(request.getDisplayOrder());

        return mapToResponse(trainingVideoRepo.save(video));
    }

    public void delete(
            Long organizationId,
            Long trainingId,
            Long videoId
    ) {

        TrainingVideo video =
                findVideo(organizationId, trainingId, videoId);

        video.setActive(false);

        trainingVideoRepo.save(video);
    }

    public TrainingVideoResponse uploadVideo(
            Long organizationId,
            Long trainingId,
            Long videoId,
            MultipartFile file
    ) {

        TrainingVideo video =
                findVideo(organizationId, trainingId, videoId);

        AttachmentResponse uploaded =
                supabaseStorageService.uploadFile(
                        file,
                        AttachmentType.TRAINING_MATERIAL,
                        EntityType.TRAINING_VIDEO,
                        videoId
                );

        video.setVideoUrl(uploaded.getFileUrl());
        video.setVideoType("UPLOADED");

        return mapToResponse(trainingVideoRepo.save(video));
    }

    private TrainingVideo findVideo(
            Long organizationId,
            Long trainingId,
            Long videoId
    ) {

        return trainingVideoRepo
                .findByIdAndOrganizationIdAndTrainingCatalogueId(
                        videoId,
                        organizationId,
                        trainingId
                )
                .orElseThrow(() -> new RuntimeException("Video not found"));
    }

    private TrainingVideoResponse mapToResponse(
            TrainingVideo video
    ) {

        return TrainingVideoResponse.builder()
                .id(video.getId())
                .organizationId(video.getOrganizationId())
                .trainingCatalogueId(video.getTrainingCatalogue().getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoType(video.getVideoType())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .durationMinutes(video.getDurationMinutes())
                .displayOrder(video.getDisplayOrder())
                .active(video.getActive())
                .creationDate(video.getCreationDate())
                .modificationDate(video.getModificationDate())
                .build();
    }
}
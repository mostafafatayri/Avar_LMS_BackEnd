package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.request.training.TrainingCatalogueRequest;
import com.fatayriTech.avarLMS.response.training.TrainingCatalogueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingCatalogueService {

    private final TrainingCatalogueRepo trainingCatalogueRepo;

    public TrainingCatalogueResponse create(Long organizationId, TrainingCatalogueRequest request) {
        TrainingCatalogue training = TrainingCatalogue.builder()
                .organizationId(organizationId)
                .title(request.getTitle())
                .description(request.getDescription())
                .trainingType(request.getTrainingType())
                .status(request.getStatus() != null ? request.getStatus() : "Draft")
                .durationHours(request.getDurationHours())
                .validityMonths(request.getValidityMonths())
                .passingScore(request.getPassingScore())
                .trainer(request.getTrainer())
                .trainerEmail(request.getTrainerEmail())
                .materialUrl(request.getMaterialUrl())
                .hasLiveSession(Boolean.TRUE.equals(request.getHasLiveSession()))
                .certificateEnabled(Boolean.TRUE.equals(request.getCertificateEnabled()))
                .mandatory(Boolean.TRUE.equals(request.getMandatory()))
                .active(true)
                .build();

        return mapToResponse(trainingCatalogueRepo.save(training));
    }

    public List<TrainingCatalogueResponse> getAll(Long organizationId) {
        return trainingCatalogueRepo.findByOrganizationIdAndActiveTrue(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TrainingCatalogueResponse getById(Long organizationId, Long id) {
        TrainingCatalogue training = trainingCatalogueRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        return mapToResponse(training);
    }

    public TrainingCatalogueResponse update(Long organizationId, Long id, TrainingCatalogueRequest request) {
        TrainingCatalogue training = trainingCatalogueRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        training.setTitle(request.getTitle());
        training.setDescription(request.getDescription());
        training.setTrainingType(request.getTrainingType());
        training.setStatus(request.getStatus());
        training.setDurationHours(request.getDurationHours());
        training.setValidityMonths(request.getValidityMonths());
        training.setPassingScore(request.getPassingScore());
        training.setTrainer(request.getTrainer());
        training.setTrainerEmail(request.getTrainerEmail());
        training.setMaterialUrl(request.getMaterialUrl());
        training.setHasLiveSession(Boolean.TRUE.equals(request.getHasLiveSession()));
        training.setCertificateEnabled(Boolean.TRUE.equals(request.getCertificateEnabled()));
        training.setMandatory(Boolean.TRUE.equals(request.getMandatory()));

        return mapToResponse(trainingCatalogueRepo.save(training));
    }

    public void delete(Long organizationId, Long id) {
        TrainingCatalogue training = trainingCatalogueRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        training.setActive(false);
        trainingCatalogueRepo.save(training);
    }

    private TrainingCatalogueResponse mapToResponse(TrainingCatalogue training) {
        return TrainingCatalogueResponse.builder()
                .id(training.getId())
                .organizationId(training.getOrganizationId())
                .title(training.getTitle())
                .description(training.getDescription())
                .trainingType(training.getTrainingType())
                .status(training.getStatus())
                .durationHours(training.getDurationHours())
                .validityMonths(training.getValidityMonths())
                .passingScore(training.getPassingScore())
                .trainer(training.getTrainer())
                .trainerEmail(training.getTrainerEmail())
                .materialUrl(training.getMaterialUrl())
                .hasLiveSession(training.getHasLiveSession())
                .certificateEnabled(training.getCertificateEnabled())
                .mandatory(training.getMandatory())
                .active(training.getActive())
                .creationDate(training.getCreationDate())
                .modificationDate(training.getModificationDate())
                .build();
    }

    public TrainingCatalogueResponse uploadMaterial(
            Long organizationId,
            Long trainingId,
            MultipartFile file
    ) {
        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationIdAndActiveTrue(trainingId, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        String contentType = file.getContentType();

        boolean validType =
                "application/pdf".equals(contentType) ||
                        "application/vnd.ms-powerpoint".equals(contentType) ||
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation".equals(contentType) ||
                        "video/mp4".equals(contentType);

        if (!validType) {
            throw new RuntimeException("Only PDF, PPT, PPTX, and MP4 files are allowed");
        }

        String fileName = file.getOriginalFilename();

        // Replace this later with Supabase upload result URL
        String materialUrl = "/uploads/training-materials/" + fileName;

        training.setMaterialUrl(materialUrl);
        training.setMaterialFileName(fileName);
        training.setMaterialContentType(contentType);
        training.setMaterialSize(file.getSize());

        return mapToResponse(trainingCatalogueRepo.save(training));
    }
}
package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.enums.TrainingCatalogueStatus;
import com.fatayriTech.avarLMS.enums.TrainingModule;
import com.fatayriTech.avarLMS.enums.TrainingType;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
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
    private final EmployeeRepo employeeRepo;

    public TrainingCatalogueResponse create(Long organizationId, TrainingCatalogueRequest request) {
        validateRequest(request);

        TrainingCatalogue training = TrainingCatalogue.builder()
                .organizationId(organizationId)
                .module(request.getModule() == null ? TrainingModule.L_AND_D : request.getModule())
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .trainingType(request.getTrainingType() == null ? TrainingType.ONLINE : request.getTrainingType())
                .status(request.getStatus() == null ? TrainingCatalogueStatus.DRAFT : request.getStatus())
                .durationHours(request.getDurationHours())
                .validityMonths(request.getValidityMonths())
                .passingScore(request.getPassingScore())
                .kpiWeightPercentage(request.getKpiWeightPercentage())
                .materialUrl(request.getMaterialUrl())
                .hasLiveSession(Boolean.TRUE.equals(request.getHasLiveSession()))
                .liveSessionDateTime(request.getLiveSessionDateTime())
                .meetingLink(request.getMeetingLink())
                .recordingUrl(request.getRecordingUrl())
                .recordingAccess(request.getRecordingAccess())
                .certificateEnabled(Boolean.TRUE.equals(request.getCertificateEnabled()))
                .refresher(Boolean.TRUE.equals(request.getRefresher()))
                .assessment(Boolean.TRUE.equals(request.getAssessment()))
                .approval(Boolean.TRUE.equals(request.getApproval()))
                .autoRenew(Boolean.TRUE.equals(request.getAutoRenew()))
                .renewalLeadTimeDays(request.getRenewalLeadTimeDays())
                .active(true)
                .build();

        applyTrainer(organizationId, training, request);
        normalizeConditionalFields(training);

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

    public TrainingCatalogueResponse update(
            Long organizationId,
            Long id,
            TrainingCatalogueRequest request
    ) {
        validateRequest(request);

        TrainingCatalogue training = trainingCatalogueRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        training.setModule(request.getModule() == null ? TrainingModule.L_AND_D : request.getModule());
        training.setTitle(request.getTitle().trim());
        training.setDescription(request.getDescription());
        training.setTrainingType(request.getTrainingType() == null ? TrainingType.ONLINE : request.getTrainingType());
        training.setStatus(request.getStatus() == null ? TrainingCatalogueStatus.DRAFT : request.getStatus());
        training.setDurationHours(request.getDurationHours());
        training.setValidityMonths(request.getValidityMonths());
        training.setPassingScore(request.getPassingScore());
        training.setKpiWeightPercentage(request.getKpiWeightPercentage());
        training.setMaterialUrl(request.getMaterialUrl());

        training.setHasLiveSession(Boolean.TRUE.equals(request.getHasLiveSession()));
        training.setLiveSessionDateTime(request.getLiveSessionDateTime());
        training.setMeetingLink(request.getMeetingLink());
        training.setRecordingUrl(request.getRecordingUrl());
        training.setRecordingAccess(request.getRecordingAccess());

        training.setCertificateEnabled(Boolean.TRUE.equals(request.getCertificateEnabled()));
        training.setRefresher(Boolean.TRUE.equals(request.getRefresher()));
        training.setAssessment(Boolean.TRUE.equals(request.getAssessment()));
        training.setApproval(Boolean.TRUE.equals(request.getApproval()));

        training.setAutoRenew(Boolean.TRUE.equals(request.getAutoRenew()));
        training.setRenewalLeadTimeDays(request.getRenewalLeadTimeDays());

        applyTrainer(organizationId, training, request);
        normalizeConditionalFields(training);

        return mapToResponse(trainingCatalogueRepo.save(training));
    }

    public void delete(Long organizationId, Long id) {
        TrainingCatalogue training = trainingCatalogueRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        training.setActive(false);
        trainingCatalogueRepo.save(training);
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
        String materialUrl = "/uploads/training-materials/" + fileName;

        training.setMaterialUrl(materialUrl);
        training.setMaterialFileName(fileName);
        training.setMaterialContentType(contentType);
        training.setMaterialSize(file.getSize());

        return mapToResponse(trainingCatalogueRepo.save(training));
    }

    private void applyTrainer(
            Long organizationId,
            TrainingCatalogue training,
            TrainingCatalogueRequest request
    ) {
        if (training.getTrainingType() == TrainingType.EXTERNAL) {
            training.setTrainerEmployeeId(null);
            training.setTrainer(request.getTrainer());
            training.setTrainerEmail(request.getTrainerEmail());
            return;
        }

        if (request.getTrainerEmployeeId() == null) {
            training.setTrainerEmployeeId(null);
            training.setTrainer(null);
            training.setTrainerEmail(null);
            return;
        }

        Employee trainerEmployee = employeeRepo
                .findByIdAndOrganizationId(request.getTrainerEmployeeId(), organizationId)
                .orElseThrow(() -> new RuntimeException("Trainer employee not found"));

        training.setTrainerEmployeeId(trainerEmployee.getId());
        training.setTrainer(buildFullName(trainerEmployee));
        training.setTrainerEmail(trainerEmployee.getEmail());
    }

    private void normalizeConditionalFields(TrainingCatalogue training) {
        if (!Boolean.TRUE.equals(training.getHasLiveSession())) {
            training.setLiveSessionDateTime(null);
            training.setMeetingLink(null);
            training.setRecordingUrl(null);
            training.setRecordingAccess(null);
        }

        if (!Boolean.TRUE.equals(training.getAutoRenew())) {
            training.setRenewalLeadTimeDays(null);
        }

        if (training.getKpiWeightPercentage() != null) {
            int safeWeight = Math.max(0, Math.min(training.getKpiWeightPercentage(), 100));
            training.setKpiWeightPercentage(safeWeight);
        }

        if (training.getPassingScore() != null) {
            int safeScore = Math.max(0, Math.min(training.getPassingScore(), 100));
            training.setPassingScore(safeScore);
        }
    }

    private void validateRequest(TrainingCatalogueRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RuntimeException("Training title is required");
        }

        if (Boolean.TRUE.equals(request.getAutoRenew())
                && request.getRenewalLeadTimeDays() == null) {
            throw new RuntimeException("Renewal lead-time is required when auto-renew is enabled");
        }

        if (Boolean.TRUE.equals(request.getHasLiveSession())
                && request.getLiveSessionDateTime() == null) {
            throw new RuntimeException("Live session date and time is required when live session is enabled");
        }
    }

    private TrainingCatalogueResponse mapToResponse(TrainingCatalogue training) {
        return TrainingCatalogueResponse.builder()
                .id(training.getId())
                .organizationId(training.getOrganizationId())
                .module(training.getModule())
                .title(training.getTitle())
                .description(training.getDescription())
                .trainingType(training.getTrainingType())
                .status(training.getStatus())
                .durationHours(training.getDurationHours())
                .validityMonths(training.getValidityMonths())
                .passingScore(training.getPassingScore())
                .kpiWeightPercentage(training.getKpiWeightPercentage())
                .trainerEmployeeId(training.getTrainerEmployeeId())
                .trainer(training.getTrainer())
                .trainerEmail(training.getTrainerEmail())
                .materialUrl(training.getMaterialUrl())
                .materialFileName(training.getMaterialFileName())
                .materialContentType(training.getMaterialContentType())
                .materialSize(training.getMaterialSize())
                .hasLiveSession(training.getHasLiveSession())
                .liveSessionDateTime(training.getLiveSessionDateTime())
                .meetingLink(training.getMeetingLink())
                .recordingUrl(training.getRecordingUrl())
                .recordingAccess(training.getRecordingAccess())
                .certificateEnabled(training.getCertificateEnabled())
                .refresher(training.getRefresher())
                .assessment(training.getAssessment())
                .approval(training.getApproval())
                .autoRenew(training.getAutoRenew())
                .renewalLeadTimeDays(training.getRenewalLeadTimeDays())
                .active(training.getActive())
                .joinToken(training.getJoinToken())
                .joinUrl(training.getJoinUrl())
                .joinUrlGeneratedAt(training.getJoinUrlGeneratedAt())
                .joinUrlGeneratedBy(training.getJoinUrlGeneratedBy())
                .creationDate(training.getCreationDate())
                .modificationDate(training.getModificationDate())
                .build();
    }

    private String buildFullName(Employee employee) {
        return String.join(" ",
                employee.getFirstName() != null ? employee.getFirstName() : "",
                employee.getMiddleName() != null ? employee.getMiddleName() : "",
                employee.getLastName() != null ? employee.getLastName() : ""
        ).trim().replaceAll(" +", " ");
    }

    public TrainingCatalogueResponse generateJoinUrl(
            Long organizationId,
            Long trainingId,
            Long generatedBy
    ) {
        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationIdAndActiveTrue(trainingId, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        if (!Boolean.TRUE.equals(training.getHasLiveSession())) {
            throw new RuntimeException("This training is not marked as a live session");
        }

        String token = java.util.UUID.randomUUID().toString();

        String joinUrl = "http://localhost:5173/live-sessions/join/" + token;

        training.setJoinToken(token);
        training.setJoinUrl(joinUrl);
        training.setJoinUrlGeneratedAt(java.time.LocalDateTime.now());
        training.setJoinUrlGeneratedBy(generatedBy);

        return mapToResponse(trainingCatalogueRepo.save(training));
    }
}
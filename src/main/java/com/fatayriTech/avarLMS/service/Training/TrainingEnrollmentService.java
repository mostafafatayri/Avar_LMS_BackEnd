package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.model.TrainingEnrollment;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.repository.TrainingEnrollmentRepo;
import com.fatayriTech.avarLMS.request.training.TrainingEnrollmentRequest;
import com.fatayriTech.avarLMS.response.training.TrainingEnrollmentResponse;
import com.fatayriTech.avarLMS.service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingEnrollmentService {

    private final TrainingEnrollmentRepo enrollmentRepo;
    private final TrainingCatalogueRepo trainingCatalogueRepo;

    public List<TrainingEnrollmentResponse> getAll(Long organizationId, Long trainingId) {
        return enrollmentRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndActiveTrue(
                        organizationId,
                        trainingId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TrainingEnrollmentResponse getMyEnrollment(
            Long organizationId,
            Long trainingId,
            CurrentUser currentUser
    ) {
        return enrollmentRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndUserIdAndActiveTrue(
                        organizationId,
                        trainingId,
                        currentUser.getUserId()
                )
                .map(this::mapToResponse)
                .orElse(null);
    }

    public TrainingEnrollmentResponse enroll(
            Long organizationId,
            Long trainingId,
            CurrentUser currentUser
    ) {
        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationId(trainingId, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        if (enrollmentRepo.existsByOrganizationIdAndTrainingCatalogueIdAndUserIdAndActiveTrue(
                organizationId,
                trainingId,
                currentUser.getUserId()
        )) {
            throw new RuntimeException("You are already enrolled in this training");
        }

        TrainingEnrollment enrollment = TrainingEnrollment.builder()
                .organizationId(organizationId)
                .trainingCatalogue(training)
                .userId(currentUser.getUserId())
                .employeeId(currentUser.getEmployeeId())
                .status("ENROLLED")
                .active(true)
                .enrolledDate(LocalDateTime.now())
                .build();

        return mapToResponse(enrollmentRepo.save(enrollment));
    }

    public TrainingEnrollmentResponse updateMyStatus(
            Long organizationId,
            Long trainingId,
            TrainingEnrollmentRequest request,
            CurrentUser currentUser
    ) {
        TrainingEnrollment enrollment = enrollmentRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndUserIdAndActiveTrue(
                        organizationId,
                        trainingId,
                        currentUser.getUserId()
                )
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        String status = request.getStatus();

        if (status == null || status.isBlank()) {
            throw new RuntimeException("Status is required");
        }

        enrollment.setStatus(status);

        if ("COMPLETED".equalsIgnoreCase(status)) {
            enrollment.setCompletedDate(LocalDateTime.now());
        }

        return mapToResponse(enrollmentRepo.save(enrollment));
    }

    public void cancelMyEnrollment(
            Long organizationId,
            Long trainingId,
            CurrentUser currentUser
    ) {
        TrainingEnrollment enrollment = enrollmentRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndUserIdAndActiveTrue(
                        organizationId,
                        trainingId,
                        currentUser.getUserId()
                )
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setStatus("CANCELLED");
        enrollment.setActive(false);

        enrollmentRepo.save(enrollment);
    }

    private TrainingEnrollmentResponse mapToResponse(TrainingEnrollment enrollment) {
        return TrainingEnrollmentResponse.builder()
                .id(enrollment.getId())
                .organizationId(enrollment.getOrganizationId())
                .trainingCatalogueId(enrollment.getTrainingCatalogue().getId())
                .userId(enrollment.getUserId())
                .employeeId(enrollment.getEmployeeId())
                .status(enrollment.getStatus())
                .active(enrollment.getActive())
                .enrolledDate(enrollment.getEnrolledDate())
                .completedDate(enrollment.getCompletedDate())
                .creationDate(enrollment.getCreationDate())
                .modificationDate(enrollment.getModificationDate())
                .build();
    }
}
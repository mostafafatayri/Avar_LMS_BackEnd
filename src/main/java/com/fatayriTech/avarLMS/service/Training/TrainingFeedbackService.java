package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.model.TrainingFeedback;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.repository.TrainingEnrollmentRepo;
import com.fatayriTech.avarLMS.repository.TrainingFeedbackRepo;
import com.fatayriTech.avarLMS.request.training.TrainingFeedbackRequest;
import com.fatayriTech.avarLMS.response.training.TrainingFeedbackResponse;
import com.fatayriTech.avarLMS.service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingFeedbackService {

    private final TrainingFeedbackRepo feedbackRepo;
    private final TrainingEnrollmentRepo enrollmentRepo;
    private final TrainingCatalogueRepo trainingCatalogueRepo;

    public List<TrainingFeedbackResponse> getAll(Long organizationId, Long trainingId) {
        return feedbackRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByCreationDateDesc(
                        organizationId,
                        trainingId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TrainingFeedbackResponse getMyFeedback(
            Long organizationId,
            Long trainingId,
            CurrentUser currentUser
    ) {
        return feedbackRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndUserIdAndActiveTrue(
                        organizationId,
                        trainingId,
                        currentUser.getUserId()
                )
                .map(this::mapToResponse)
                .orElse(null);
    }

    public TrainingFeedbackResponse create(
            Long organizationId,
            Long trainingId,
            TrainingFeedbackRequest request,
            CurrentUser currentUser
    ) {
        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationId(trainingId, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        validateRating(request.getRating());

        boolean eligible = enrollmentRepo
                .existsByOrganizationIdAndTrainingCatalogueIdAndUserIdAndStatusInAndActiveTrue(
                        organizationId,
                        trainingId,
                        currentUser.getUserId(),
                        List.of("ENROLLED", "IN_PROGRESS", "COMPLETED")
                );

        if (!eligible) {
            throw new RuntimeException("You can only submit feedback after enrolling in this training");
        }

        if (feedbackRepo.existsByOrganizationIdAndTrainingCatalogueIdAndUserIdAndActiveTrue(
                organizationId,
                trainingId,
                currentUser.getUserId()
        )) {
            throw new RuntimeException("You already submitted feedback for this training");
        }

        TrainingFeedback feedback = TrainingFeedback.builder()
                .organizationId(organizationId)
                .trainingCatalogue(training)
                .userId(currentUser.getUserId())
                .employeeId(currentUser.getEmployeeId())
                .rating(request.getRating())
                .comment(request.getComment())
                .anonymous(request.getAnonymous() != null ? request.getAnonymous() : false)
                .active(true)
                .build();

        return mapToResponse(feedbackRepo.save(feedback));
    }

    public TrainingFeedbackResponse update(
            Long organizationId,
            Long trainingId,
            Long feedbackId,
            TrainingFeedbackRequest request,
            CurrentUser currentUser
    ) {
        TrainingFeedback feedback = feedbackRepo
                .findByIdAndOrganizationIdAndTrainingCatalogueIdAndActiveTrue(
                        feedbackId,
                        organizationId,
                        trainingId
                )
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        if (!feedback.getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("You can only update your own feedback");
        }

        validateRating(request.getRating());

        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setAnonymous(request.getAnonymous() != null ? request.getAnonymous() : false);

        return mapToResponse(feedbackRepo.save(feedback));
    }

    public void delete(
            Long organizationId,
            Long trainingId,
            Long feedbackId,
            CurrentUser currentUser
    ) {
        TrainingFeedback feedback = feedbackRepo
                .findByIdAndOrganizationIdAndTrainingCatalogueIdAndActiveTrue(
                        feedbackId,
                        organizationId,
                        trainingId
                )
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        if (!feedback.getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("You can only delete your own feedback");
        }

        feedback.setActive(false);
        feedbackRepo.save(feedback);
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
    }

    private TrainingFeedbackResponse mapToResponse(TrainingFeedback feedback) {
        return TrainingFeedbackResponse.builder()
                .id(feedback.getId())
                .organizationId(feedback.getOrganizationId())
                .trainingCatalogueId(feedback.getTrainingCatalogue().getId())
                .userId(feedback.getUserId())
                .employeeId(feedback.getEmployeeId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .anonymous(feedback.getAnonymous())
                .active(feedback.getActive())
                .creationDate(feedback.getCreationDate())
                .modificationDate(feedback.getModificationDate())
                .build();
    }
}
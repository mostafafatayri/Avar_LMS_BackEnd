package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.training.TrainingFeedbackRequest;
import com.fatayriTech.avarLMS.response.training.TrainingFeedbackResponse;
import com.fatayriTech.avarLMS.service.Training.TrainingFeedbackService;
import com.fatayriTech.avarLMS.service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/training-catalogue/{trainingId}/feedback")
public class TrainingFeedbackController {

    private final TrainingFeedbackService feedbackService;

    @GetMapping
    public List<TrainingFeedbackResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId
    ) {
        return feedbackService.getAll(organizationId, trainingId);
    }

    @GetMapping("/my")
    public TrainingFeedbackResponse getMyFeedback(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return feedbackService.getMyFeedback(
                organizationId,
                trainingId,
                currentUser
        );
    }

    @PostMapping
    public TrainingFeedbackResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @RequestBody TrainingFeedbackRequest request,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return feedbackService.create(
                organizationId,
                trainingId,
                request,
                currentUser
        );
    }

    @PutMapping("/{feedbackId}")
    public TrainingFeedbackResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long feedbackId,
            @RequestBody TrainingFeedbackRequest request,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return feedbackService.update(
                organizationId,
                trainingId,
                feedbackId,
                request,
                currentUser
        );
    }

    @DeleteMapping("/{feedbackId}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long feedbackId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        feedbackService.delete(
                organizationId,
                trainingId,
                feedbackId,
                currentUser
        );
    }
}
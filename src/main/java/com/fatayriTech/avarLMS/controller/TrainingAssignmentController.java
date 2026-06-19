package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.training.TrainingAssignmentRequest;
import com.fatayriTech.avarLMS.response.training.TrainingAssignmentResponse;
import com.fatayriTech.avarLMS.service.Training.TrainingAssignmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/training-assignments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TrainingAssignmentController {

    private final TrainingAssignmentService trainingAssignmentService;

    @PreAuthorize("hasAuthority('TRAINING_ASSIGNMENT_VIEW')")
    @GetMapping
    public List<TrainingAssignmentResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return trainingAssignmentService.getAll(organizationId);
    }

    @PreAuthorize("hasAuthority('TRAINING_ASSIGNMENT_VIEW')")
    @GetMapping("/{id}")
    public TrainingAssignmentResponse getById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return trainingAssignmentService.getById(organizationId, id);
    }

    @PreAuthorize("hasAuthority('TRAINING_ASSIGNMENT_CREATE')")
    @PostMapping
    public TrainingAssignmentResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody TrainingAssignmentRequest request
    ) {
        return trainingAssignmentService.create(
                organizationId,
                userId,
                request
        );
    }

    @PreAuthorize("hasAuthority('TRAINING_ASSIGNMENT_UPDATE')")
    @PatchMapping("/{id}/progress")
    public TrainingAssignmentResponse updateProgress(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestParam Integer progress
    ) {
        return trainingAssignmentService.updateProgress(
                organizationId,
                id,
                progress
        );
    }

    @PreAuthorize("hasAuthority('TRAINING_ASSIGNMENT_UPDATE')")
    @PatchMapping("/{id}/complete")
    public TrainingAssignmentResponse markCompleted(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return trainingAssignmentService.markCompleted(
                organizationId,
                id
        );
    }

    @PreAuthorize("hasAuthority('TRAINING_ASSIGNMENT_DELETE')")
    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        trainingAssignmentService.delete(organizationId, id);
    }
}
package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.learningPath.LearningPathAssignmentRequest;
import com.fatayriTech.avarLMS.response.learningPath.LearningPathAssignmentResponse;
import com.fatayriTech.avarLMS.service.LearningPath.LearningPathAssignmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/learning-path-assignments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class LearningPathAssignmentController {

    private final LearningPathAssignmentService assignmentService;

    @PreAuthorize("hasAuthority('LEARNING_PATH_ASSIGNMENT_VIEW')")
    @GetMapping
    public List<LearningPathAssignmentResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return assignmentService.getAll(organizationId);
    }

    @PreAuthorize("hasAuthority('LEARNING_PATH_ASSIGNMENT_VIEW')")
    @GetMapping("/path/{learningPathId}")
    public List<LearningPathAssignmentResponse> getByLearningPath(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long learningPathId
    ) {
        return assignmentService.getByLearningPath(organizationId, learningPathId);
    }

    @PreAuthorize("hasAuthority('LEARNING_PATH_ASSIGNMENT_CREATE')")
    @PostMapping("/path/{learningPathId}")
    public LearningPathAssignmentResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long learningPathId,
            @RequestBody LearningPathAssignmentRequest request
    ) {
        return assignmentService.create(organizationId, learningPathId, userId, request);
    }

    @PreAuthorize("hasAuthority('LEARNING_PATH_ASSIGNMENT_UPDATE')")
    @PatchMapping("/{assignmentId}/progress")
    public LearningPathAssignmentResponse updateProgress(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long assignmentId,
            @RequestParam Integer progress
    ) {
        return assignmentService.updateProgress(organizationId, assignmentId, progress);
    }

    @PreAuthorize("hasAuthority('LEARNING_PATH_ASSIGNMENT_UPDATE')")
    @PatchMapping("/{assignmentId}/complete")
    public LearningPathAssignmentResponse markCompleted(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long assignmentId
    ) {
        return assignmentService.markCompleted(organizationId, assignmentId);
    }

    @PreAuthorize("hasAuthority('LEARNING_PATH_ASSIGNMENT_DELETE')")
    @DeleteMapping("/{assignmentId}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long assignmentId
    ) {
        assignmentService.delete(organizationId, assignmentId);
    }

    @PreAuthorize("hasAuthority('LEARNING_PATH_ASSIGNMENT_CREATE')")
    @PostMapping("/path/{learningPathId}/batch")
    public List<LearningPathAssignmentResponse> createBatch(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long learningPathId,
            @RequestBody LearningPathAssignmentRequest request
    ) {
        return assignmentService.createBatch(
                organizationId,
                learningPathId,
                userId,
                request
        );
    }
}
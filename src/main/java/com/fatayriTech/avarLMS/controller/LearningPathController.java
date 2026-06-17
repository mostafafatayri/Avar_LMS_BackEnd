package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.learningPath.LearningPathAssignmentRequest;
import com.fatayriTech.avarLMS.request.learningPath.LearningPathItemRequest;
import com.fatayriTech.avarLMS.request.learningPath.LearningPathRequest;
import com.fatayriTech.avarLMS.response.learningPath.LearningPathAssignmentResponse;
import com.fatayriTech.avarLMS.response.learningPath.LearningPathItemResponse;
import com.fatayriTech.avarLMS.response.learningPath.LearningPathResponse;
import com.fatayriTech.avarLMS.service.LearningPath.LearningPathService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/learning-paths")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class LearningPathController {

    private final LearningPathService learningPathService;

    @GetMapping
    public List<LearningPathResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return learningPathService.getAll(organizationId);
    }

    @GetMapping("/{pathId}")
    public LearningPathResponse getById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long pathId
    ) {
        return learningPathService.getById(organizationId, pathId);
    }

    @PostMapping
    public LearningPathResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody LearningPathRequest request
    ) {
        return learningPathService.create(organizationId, request);
    }

    @PutMapping("/{pathId}")
    public LearningPathResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long pathId,
            @RequestBody LearningPathRequest request
    ) {
        return learningPathService.update(organizationId, pathId, request);
    }

    @DeleteMapping("/{pathId}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long pathId
    ) {
        learningPathService.delete(organizationId, pathId);
    }

    @PostMapping("/{pathId}/items")
    public LearningPathItemResponse addItem(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long pathId,
            @RequestBody LearningPathItemRequest request
    ) {
        return learningPathService.addItem(organizationId, pathId, request);
    }

    @PutMapping("/{pathId}/items/{itemId}")
    public LearningPathItemResponse updateItem(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long pathId,
            @PathVariable Long itemId,
            @RequestBody LearningPathItemRequest request
    ) {
        return learningPathService.updateItem(
                organizationId,
                pathId,
                itemId,
                request
        );
    }

    @DeleteMapping("/{pathId}/items/{itemId}")
    public void deleteItem(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long pathId,
            @PathVariable Long itemId
    ) {
        learningPathService.deleteItem(
                organizationId,
                pathId,
                itemId
        );
    }

    @PostMapping("/{pathId}/assignments")
    public LearningPathAssignmentResponse assign(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long pathId,
            @RequestBody LearningPathAssignmentRequest request
    ) {
        return learningPathService.assign(organizationId, pathId, request);
    }

    @DeleteMapping("/{pathId}/assignments/{assignmentId}")
    public void removeAssignment(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long pathId,
            @PathVariable Long assignmentId
    ) {
        learningPathService.removeAssignment(
                organizationId,
                pathId,
                assignmentId
        );
    }

    @GetMapping("/{pathId}/sub-paths")
    public List<LearningPathResponse> getSubPaths(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long pathId
    ) {
        return learningPathService.getSubPaths(
                organizationId,
                pathId
        );
    }
}
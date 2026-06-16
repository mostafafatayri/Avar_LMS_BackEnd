package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.training.TrainingLearningObjectiveRequest;
import com.fatayriTech.avarLMS.response.training.TrainingLearningObjectiveResponse;
import com.fatayriTech.avarLMS.service.Training.TrainingLearningObjectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/training-catalogue/{trainingId}/objectives")
@RequiredArgsConstructor
public class TrainingLearningObjectiveController {

    private final TrainingLearningObjectiveService objectiveService;

    @GetMapping
    public List<TrainingLearningObjectiveResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId
    ) {
        return objectiveService.getAll(organizationId, trainingId);
    }

    @PostMapping
    public TrainingLearningObjectiveResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @RequestBody TrainingLearningObjectiveRequest request
    ) {
        return objectiveService.create(organizationId, trainingId, request);
    }

    @PutMapping("/{objectiveId}")
    public TrainingLearningObjectiveResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long objectiveId,
            @RequestBody TrainingLearningObjectiveRequest request
    ) {
        return objectiveService.update(organizationId, trainingId, objectiveId, request);
    }

    @DeleteMapping("/{objectiveId}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long objectiveId
    ) {
        objectiveService.delete(organizationId, trainingId, objectiveId);
    }
}
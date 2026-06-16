package com.fatayriTech.avarLMS.controller.Training;

import com.fatayriTech.avarLMS.request.training.TrainingRequirementRequest;
import com.fatayriTech.avarLMS.response.training.TrainingRequirementResponse;
import com.fatayriTech.avarLMS.service.Training.TrainingRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/training-catalogue/{trainingId}/requirements")
public class TrainingRequirementController {

    private final TrainingRequirementService requirementService;

    @GetMapping
    public List<TrainingRequirementResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId
    ) {
        return requirementService.getAll(organizationId, trainingId);
    }

    @PostMapping
    public TrainingRequirementResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @RequestBody TrainingRequirementRequest request
    ) {
        return requirementService.create(organizationId, trainingId, request);
    }

    @PutMapping("/{requirementId}")
    public TrainingRequirementResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long requirementId,
            @RequestBody TrainingRequirementRequest request
    ) {
        return requirementService.update(
                organizationId,
                trainingId,
                requirementId,
                request
        );
    }

    @DeleteMapping("/{requirementId}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long requirementId
    ) {
        requirementService.delete(
                organizationId,
                trainingId,
                requirementId
        );
    }
}
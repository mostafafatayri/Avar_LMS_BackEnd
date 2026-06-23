package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.training.TrainingDisplayModuleItemRequest;
import com.fatayriTech.avarLMS.request.training.TrainingDisplayModuleRequest;
import com.fatayriTech.avarLMS.response.training.TrainingDisplayModuleItemResponse;
import com.fatayriTech.avarLMS.response.training.TrainingDisplayModuleResponse;
import com.fatayriTech.avarLMS.service.Training.TrainingDisplayModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/training-catalogue/{trainingId}/display-modules")
@RequiredArgsConstructor
public class TrainingDisplayModuleController {

    private final TrainingDisplayModuleService service;

    @GetMapping
    public List<TrainingDisplayModuleResponse> getModules(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId
    ) {
        return service.getModules(organizationId, trainingId);
    }

    @PostMapping
    public TrainingDisplayModuleResponse createModule(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @RequestBody TrainingDisplayModuleRequest request
    ) {
        return service.createModule(organizationId, trainingId, request);
    }

    @PutMapping("/{moduleId}")
    public TrainingDisplayModuleResponse updateModule(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long moduleId,
            @RequestBody TrainingDisplayModuleRequest request
    ) {
        return service.updateModule(organizationId, trainingId, moduleId, request);
    }

    @DeleteMapping("/{moduleId}")
    public void deleteModule(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long moduleId
    ) {
        service.deleteModule(organizationId, trainingId, moduleId);
    }

    @PostMapping("/{moduleId}/items")
    public TrainingDisplayModuleItemResponse addItem(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long moduleId,
            @RequestBody TrainingDisplayModuleItemRequest request
    ) {
        return service.addItem(organizationId, trainingId, moduleId, request);
    }

    @PutMapping("/{moduleId}/items/{itemId}")
    public TrainingDisplayModuleItemResponse updateItem(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long moduleId,
            @PathVariable Long itemId,
            @RequestBody TrainingDisplayModuleItemRequest request
    ) {
        return service.updateItem(organizationId, trainingId, moduleId, itemId, request);
    }

    @DeleteMapping("/{moduleId}/items/{itemId}")
    public void deleteItem(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long moduleId,
            @PathVariable Long itemId
    ) {
        service.deleteItem(organizationId, trainingId, moduleId, itemId);
    }
}
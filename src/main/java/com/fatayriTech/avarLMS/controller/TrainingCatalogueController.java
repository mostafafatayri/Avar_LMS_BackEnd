package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.training.TrainingCatalogueRequest;
import com.fatayriTech.avarLMS.response.training.TrainingCatalogueResponse;
import com.fatayriTech.avarLMS.service.Training.TrainingCatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/training-catalogue")
@RequiredArgsConstructor
public class TrainingCatalogueController {

    private final TrainingCatalogueService trainingCatalogueService;

    @PostMapping
    public TrainingCatalogueResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody TrainingCatalogueRequest request
    ) {
        return trainingCatalogueService.create(organizationId, request);
    }

    @GetMapping
    public List<TrainingCatalogueResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return trainingCatalogueService.getAll(organizationId);
    }

    @GetMapping("/{id}")
    public TrainingCatalogueResponse getById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return trainingCatalogueService.getById(organizationId, id);
    }

    @PutMapping("/{id}")
    public TrainingCatalogueResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestBody TrainingCatalogueRequest request
    ) {
        return trainingCatalogueService.update(organizationId, id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        trainingCatalogueService.delete(organizationId, id);
    }


    @PostMapping("/{id}/material")
    public TrainingCatalogueResponse uploadMaterial(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return trainingCatalogueService.uploadMaterial(organizationId, id, file);
    }

}
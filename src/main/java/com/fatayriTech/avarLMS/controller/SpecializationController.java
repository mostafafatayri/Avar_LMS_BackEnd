package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.SpecializationRequest;
import com.fatayriTech.avarLMS.response.SpecializationResponse;
import com.fatayriTech.avarLMS.service.WorkStructure.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.fatayriTech.avarLMS.response.SpecializationBulkUploadResponse;
import com.fatayriTech.avarLMS.service.WorkStructure.SpecializationBulkUploadService;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/specializations")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;
    private final SpecializationBulkUploadService specializationBulkUploadService;
    @GetMapping
    public List<SpecializationResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return specializationService.getAll(organizationId);
    }

    @GetMapping("/{id}")
    public SpecializationResponse getById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return specializationService.getById(organizationId, id);
    }

    @PostMapping
    public SpecializationResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody SpecializationRequest request
    ) {
        return specializationService.create(organizationId, request);
    }

    @PutMapping("/{id}")
    public SpecializationResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestBody SpecializationRequest request
    ) {
        return specializationService.update(organizationId, id, request);
    }

    @PatchMapping("/{id}/active")
    public SpecializationResponse setActive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return specializationService.setActive(organizationId, id);
    }

    @PatchMapping("/{id}/inactive")
    public SpecializationResponse setInactive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return specializationService.setInactive(organizationId, id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        specializationService.delete(organizationId, id);
    }

    @PostMapping("/bulk-upload")
    public SpecializationBulkUploadResponse bulkUpload(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestParam("file") MultipartFile file
    ) {
        return specializationBulkUploadService.uploadSpecializations(organizationId, file);
    }
}
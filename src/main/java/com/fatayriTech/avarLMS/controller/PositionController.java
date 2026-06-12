package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.position.CreatePositionRequest;
import com.fatayriTech.avarLMS.request.position.UpdatePositionRequest;
import com.fatayriTech.avarLMS.response.position.PositionBulkUploadResponse;
import com.fatayriTech.avarLMS.response.position.PositionResponse;
import com.fatayriTech.avarLMS.service.WorkStructure.PositionBulkUploadService;
import com.fatayriTech.avarLMS.service.WorkStructure.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;
    private final PositionBulkUploadService positionBulkUploadService;

    @PreAuthorize("hasAuthority('POSITION_CREATE')")
    @PostMapping
    public PositionResponse createPosition(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody CreatePositionRequest request
    ) {
        return positionService.createPosition(organizationId, request);
    }

    @PreAuthorize("hasAuthority('POSITION_VIEW')")
    @GetMapping
    public List<PositionResponse> getAllPositions(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return positionService.getAllPositions(organizationId);
    }

    @PreAuthorize("hasAuthority('POSITION_VIEW')")
    @GetMapping("/{id}")
    public PositionResponse getPositionById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return positionService.getPositionById(organizationId, id);
    }

    @PreAuthorize("hasAuthority('POSITION_UPDATE')")
    @PutMapping("/{id}")
    public PositionResponse updatePosition(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestBody UpdatePositionRequest request
    ) {
        return positionService.updatePosition(organizationId, id, request);
    }

    @PreAuthorize("hasAuthority('POSITION_DELETE')")
    @DeleteMapping("/{id}")
    public String deletePosition(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        positionService.deletePosition(organizationId, id);
        return "Position deleted successfully";
    }

    @PreAuthorize("hasAuthority('POSITION_UPDATE')")
    @PatchMapping("/{id}/inactive")
    public PositionResponse setPositionInactive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return positionService.setPositionInactive(organizationId, id);
    }

    @PreAuthorize("hasAuthority('POSITION_UPDATE')")
    @PatchMapping("/{id}/active")
    public PositionResponse setPositionActive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return positionService.setPositionActive(organizationId, id);
    }

    @PreAuthorize("hasAuthority('POSITION_CREATE')")
    @PostMapping("/bulk-upload")
    public PositionBulkUploadResponse bulkUploadPositions(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestParam("file") MultipartFile file
    ) {
        return positionBulkUploadService.uploadPositions(organizationId, file);
    }
}
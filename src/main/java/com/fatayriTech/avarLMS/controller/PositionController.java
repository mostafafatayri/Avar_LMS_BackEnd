package com.fatayriTech.avarLMS.controller;


import com.fatayriTech.avarLMS.request.position.CreatePositionRequest;
import com.fatayriTech.avarLMS.request.position.UpdatePositionRequest;
import com.fatayriTech.avarLMS.response.position.PositionBulkUploadResponse;
import com.fatayriTech.avarLMS.response.position.PositionResponse;
import com.fatayriTech.avarLMS.service.WorkStructure.PositionBulkUploadService;
import com.fatayriTech.avarLMS.service.WorkStructure.PositionService;//  PositionService;
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
            @RequestBody CreatePositionRequest request
    ) {
        return positionService.createPosition(request);
    }

    @PreAuthorize("hasAuthority('POSITION_VIEW')")
    @GetMapping
    public List<PositionResponse> getAllPositions() {
        return positionService.getAllPositions();
    }

    @PreAuthorize("hasAuthority('POSITION_VIEW')")
    @GetMapping("/{id}")
    public PositionResponse getPositionById(
            @PathVariable Long id
    ) {
        return positionService.getPositionById(id);
    }

    @PreAuthorize("hasAuthority('POSITION_UPDATE')")
    @PutMapping("/{id}")
    public PositionResponse updatePosition(
            @PathVariable Long id,
            @RequestBody UpdatePositionRequest request
    ) {
        return positionService.updatePosition(id, request);
    }

    @PreAuthorize("hasAuthority('POSITION_DELETE')")
    @DeleteMapping("/{id}")
    public String deletePosition(
            @PathVariable Long id
    ) {
        positionService.deletePosition(id);
        return "Position deleted successfully";
    }

    @PreAuthorize("hasAuthority('POSITION_UPDATE')")
    @PatchMapping("/{id}/inactive")
    public PositionResponse setPositionInactive(@PathVariable Long id) {
        return positionService.setPositionInactive(id);
    }

    @PreAuthorize("hasAuthority('POSITION_UPDATE')")
    @PatchMapping("/{id}/active")
    public PositionResponse setPositionActive(@PathVariable Long id) {
        return positionService.setPositionActive(id);
    }

    @PreAuthorize("hasAuthority('POSITION_CREATE')")
    @PostMapping("/bulk-upload")
    public PositionBulkUploadResponse bulkUploadPositions(
            @RequestParam("file") MultipartFile file
    ) {
        return positionBulkUploadService.uploadPositions(file);
    }
}
package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.location.LocationRequest;
import com.fatayriTech.avarLMS.response.location.LocationBulkUploadResponse;
import com.fatayriTech.avarLMS.response.location.LocationResponse;
import com.fatayriTech.avarLMS.service.Location.LocationBulkUploadService;
import com.fatayriTech.avarLMS.service.Location.LocationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/locations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class LocationController {

    private final LocationService locationService;
    private final LocationBulkUploadService locationBulkUploadService;

    @GetMapping
    public List<LocationResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return locationService.getAll(organizationId);
    }

    @GetMapping("/{locationId}")
    public LocationResponse getById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long locationId
    ) {
        return locationService.getById(organizationId, locationId);
    }

    @PostMapping
    public LocationResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody LocationRequest request
    ) {
        return locationService.create(organizationId, request);
    }

    @PutMapping("/{locationId}")
    public LocationResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long locationId,
            @RequestBody LocationRequest request
    ) {
        return locationService.update(organizationId, locationId, request);
    }

    @DeleteMapping("/{locationId}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long locationId
    ) {
        locationService.delete(organizationId, locationId);
    }

    @PostMapping("/bulk-upload")
    public LocationBulkUploadResponse bulkUploadLocations(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestParam("file") MultipartFile file
    ) {
        return locationBulkUploadService.uploadLocations(organizationId, file);
    }
}
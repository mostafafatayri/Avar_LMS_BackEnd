package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.SubTeamRequest;
import com.fatayriTech.avarLMS.response.SubTeamBulkUploadResponse;
import com.fatayriTech.avarLMS.response.SubTeamResponse;
import com.fatayriTech.avarLMS.service.WorkStructure.SubTeamBulkUploadService;
import com.fatayriTech.avarLMS.service.WorkStructure.SubTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/sub-teams")
@RequiredArgsConstructor
public class SubTeamController {

    private final SubTeamService subTeamService;
    private final SubTeamBulkUploadService subTeamBulkUploadService;

    @GetMapping
    public List<SubTeamResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return subTeamService.getAll(organizationId);
    }

    @GetMapping("/{id}")
    public SubTeamResponse getById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return subTeamService.getById(organizationId, id);
    }

    @PostMapping
    public SubTeamResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody SubTeamRequest request
    ) {
        return subTeamService.create(organizationId, request);
    }

    @PutMapping("/{id}")
    public SubTeamResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestBody SubTeamRequest request
    ) {
        return subTeamService.update(organizationId, id, request);
    }

    @PatchMapping("/{id}/active")
    public SubTeamResponse setActive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return subTeamService.setActive(organizationId, id);
    }

    @PatchMapping("/{id}/inactive")
    public SubTeamResponse setInactive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return subTeamService.setInactive(organizationId, id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        subTeamService.delete(organizationId, id);
    }

    @PostMapping("/bulk-upload")
    public SubTeamBulkUploadResponse bulkUploadSubTeams(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestParam("file") MultipartFile file
    ) {
        return subTeamBulkUploadService.uploadSubTeams(organizationId, file);
    }
}
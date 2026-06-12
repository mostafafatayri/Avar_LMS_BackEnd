package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.dto.OrganizationCardDto;
import com.fatayriTech.avarLMS.request.OrganizationRequest;
import com.fatayriTech.avarLMS.response.OrganizationResponse;
import com.fatayriTech.avarLMS.service.OrganizationService.OrganizationService;
import com.fatayriTech.avarLMS.service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/organizations")
@RequiredArgsConstructor
public class OrganizationsController {

    private final OrganizationService organizationService;

    @GetMapping("/my")
    public List<OrganizationCardDto> getMyOrganizations(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return organizationService.getOrganizationsForUser(currentUser.getUserId());
    }

    @GetMapping
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @GetMapping("/{id}")
    public OrganizationResponse getOrganizationById(@PathVariable Long id) {
        return organizationService.getOrganizationById(id);
    }

    @PreAuthorize("hasAuthority('ORGANIZATION_CREATE')")
    @PostMapping
    public OrganizationResponse createOrganization(@RequestBody OrganizationRequest request) {
        return organizationService.createOrganization(request);
    }

    @PutMapping("/{id}")
    public OrganizationResponse updateOrganization(
            @PathVariable Long id,
            @RequestBody OrganizationRequest request
    ) {
        return organizationService.updateOrganization(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
    }
}
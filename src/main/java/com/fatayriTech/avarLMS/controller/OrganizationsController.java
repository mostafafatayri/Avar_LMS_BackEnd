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
import com.fatayriTech.avarLMS.request.OrganizationDomainRequest;
import com.fatayriTech.avarLMS.response.OrganizationDomainResponse;
import com.fatayriTech.avarLMS.response.OrganizationDomainUserResponse;
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

    ///  domain related :
    @GetMapping("/{id}/domain")
    public OrganizationDomainResponse getOrganizationDomain(@PathVariable Long id) {
        return organizationService.getOrganizationDomain(id);
    }

    @PutMapping("/{id}/domain")
    public OrganizationDomainResponse attachDomain(
            @PathVariable Long id,
            @RequestBody OrganizationDomainRequest request
    ) {
        return organizationService.attachDomainToOrganization(id, request);
    }

    @DeleteMapping("/{id}/domain")
    public void removeDomain(@PathVariable Long id) {
        organizationService.removeDomainFromOrganization(id);
    }

    @GetMapping("/{id}/domain-users")
    public List<OrganizationDomainUserResponse> getOrganizationDomainUsers(
            @PathVariable Long id
    ) {
        return organizationService.getUsersByOrganizationDomain(id);
    }

    @GetMapping("/{id}/admins")
    public List<OrganizationDomainUserResponse> getOrganizationAdmins(
            @PathVariable Long id
    ) {
        return organizationService.getOrganizationAdmins(id);
    }

    /// ////
    @PostMapping("/{organizationId}/users/{userId}/grant-view")
    public void grantOrganizationView(
            @PathVariable Long organizationId,
            @PathVariable Long userId
    ) {
        organizationService.grantOrganizationView(organizationId, userId);
    }

    @DeleteMapping("/{organizationId}/users/{userId}/remove-view")
    public void removeOrganizationView(
            @PathVariable Long organizationId,
            @PathVariable Long userId
    ) {
        organizationService.removeOrganizationView(organizationId, userId);
    }

    @PostMapping("/{organizationId}/users/{userId}/make-admin")
    public void makeOrganizationAdmin(
            @PathVariable Long organizationId,
            @PathVariable Long userId
    ) {
        organizationService.makeOrganizationAdmin(organizationId, userId);
    }

    @DeleteMapping("/{organizationId}/users/{userId}/remove-admin")
    public void removeOrganizationAdmin(
            @PathVariable Long organizationId,
            @PathVariable Long userId
    ) {
        organizationService.removeOrganizationAdmin(organizationId, userId);
    }
}
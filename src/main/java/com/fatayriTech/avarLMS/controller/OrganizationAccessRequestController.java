package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.response.organization.OrganizationAccessRequestResponse;
import com.fatayriTech.avarLMS.service.OrganizationService.OrganizationAccessRequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/organization-access-requests")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrganizationAccessRequestController {

    private final OrganizationAccessRequestService accessRequestService;

    @PostMapping("/contact-administrator")
    public OrganizationAccessRequestResponse contactAdministrator(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return accessRequestService.contactAdministrator(userId);
    }
}
package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.dto.CreateOrganizationRequest;
import com.fatayriTech.avarLMS.dto.OrganizationCardDto;
import com.fatayriTech.avarLMS.service.OrganizationService.OrganizationService;
import com.fatayriTech.avarLMS.service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/organizations")
@RequiredArgsConstructor
public class OrganizationsController {

    private final OrganizationService organizationService;
    private final CurrentUserService currentUserService;

    @GetMapping("/my-organizations")
    public ResponseEntity<List<OrganizationCardDto>> myOrganizations() {

        Long userId = currentUserService.getUserId();

        return ResponseEntity.ok(
                organizationService.getOrganizationsForUser(userId)
        );
    }

    @PostMapping
    public ResponseEntity<OrganizationCardDto> createOrganization(
            @RequestBody CreateOrganizationRequest request
    ) {
        Long userId = currentUserService.getUserId();

        return ResponseEntity.ok(
                organizationService.createOrganization(request, userId)
        );
    }
}
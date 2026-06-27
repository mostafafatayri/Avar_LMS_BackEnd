package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.SeniorityLevelRequest;
import com.fatayriTech.avarLMS.response.SeniorityLevelResponse;
import com.fatayriTech.avarLMS.service.WorkStructure.SeniorityLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/seniority-levels")
@RequiredArgsConstructor
public class SeniorityLevelController {

    private final SeniorityLevelService seniorityLevelService;

    @GetMapping
    public List<SeniorityLevelResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return seniorityLevelService.getAll(organizationId);
    }

    @GetMapping("/active")
    public List<SeniorityLevelResponse> getActive(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return seniorityLevelService.getActive(organizationId);
    }

    @GetMapping("/{id}")
    public SeniorityLevelResponse getById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return seniorityLevelService.getById(organizationId, id);
    }

    @PostMapping
    public SeniorityLevelResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody SeniorityLevelRequest request
    ) {
        return seniorityLevelService.create(organizationId, request);
    }

    @PutMapping("/{id}")
    public SeniorityLevelResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestBody SeniorityLevelRequest request
    ) {
        return seniorityLevelService.update(organizationId, id, request);
    }

    @PatchMapping("/{id}/active")
    public SeniorityLevelResponse setActive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return seniorityLevelService.setActive(organizationId, id);
    }

    @PatchMapping("/{id}/inactive")
    public SeniorityLevelResponse setInactive(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return seniorityLevelService.setInactive(organizationId, id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        seniorityLevelService.delete(organizationId, id);
    }
}
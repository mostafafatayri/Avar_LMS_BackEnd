package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.notification.NotificationRuleRequest;
import com.fatayriTech.avarLMS.response.notification.NotificationRuleResponse;
import com.fatayriTech.avarLMS.service.Notification.NotificationRuleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/notification-rules")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationRuleController {

    private final NotificationRuleService notificationRuleService;

    @PostMapping
    public NotificationRuleResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestBody NotificationRuleRequest request
    ) {
        return notificationRuleService.create(organizationId, request);
    }

    @GetMapping
    public List<NotificationRuleResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return notificationRuleService.getAll(organizationId);
    }

    @GetMapping("/active")
    public List<NotificationRuleResponse> getActive(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return notificationRuleService.getActive(organizationId);
    }

    @GetMapping("/{id}")
    public NotificationRuleResponse getById(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return notificationRuleService.getById(organizationId, id);
    }

    @PutMapping("/{id}")
    public NotificationRuleResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id,
            @RequestBody NotificationRuleRequest request
    ) {
        return notificationRuleService.update(organizationId, id, request);
    }

    @PatchMapping("/{id}/toggle")
    public NotificationRuleResponse toggleStatus(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        return notificationRuleService.toggleStatus(organizationId, id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long id
    ) {
        notificationRuleService.delete(organizationId, id);
    }
}
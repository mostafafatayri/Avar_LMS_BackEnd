package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.response.notification.InAppNotificationResponse;
import com.fatayriTech.avarLMS.service.Notification.InAppNotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InAppNotificationController {

    private final InAppNotificationService service;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<InAppNotificationResponse> getMyNotifications(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return service.getMyNotifications(organizationId, userId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unread-count")
    public Long getUnreadCount(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return service.getUnreadCount(organizationId, userId);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{notificationId}/read")
    public InAppNotificationResponse markAsRead(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long notificationId
    ) {
        return service.markAsRead(organizationId, notificationId);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/mark-all-read")
    public void markAllAsRead(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        service.markAllAsRead(organizationId, userId);
    }
}
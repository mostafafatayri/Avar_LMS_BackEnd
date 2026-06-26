package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.enums.NotificationStatus;
import com.fatayriTech.avarLMS.response.notification.NotificationEngineStatsResponse;
import com.fatayriTech.avarLMS.response.notification.NotificationEventResponse;
import com.fatayriTech.avarLMS.service.Notification.NotificationEventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/notification-events")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationEventController {

    private final NotificationEventService notificationEventService;

    @GetMapping
    public List<NotificationEventResponse> getEvents(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return notificationEventService.getEvents(organizationId);
    }

    @GetMapping("/status/{status}")
    public List<NotificationEventResponse> getEventsByStatus(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable NotificationStatus status
    ) {
        return notificationEventService.getEventsByStatus(
                organizationId,
                status
        );
    }

    @GetMapping("/stats")
    public NotificationEngineStatsResponse getStats(
            @RequestHeader("X-Organization-Id") Long organizationId
    ) {
        return notificationEventService.getStats(organizationId);
    }
}
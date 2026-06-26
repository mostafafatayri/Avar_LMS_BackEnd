package com.fatayriTech.avarLMS.response.notification;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationEngineStatsResponse {

    private Long totalRules;
    private Long activeRules;
    private Long inactiveRules;

    private Long pendingEvents;
    private Long sentEvents;
    private Long failedEvents;

    private Long emailEvents;
    private Long inAppEvents;
}
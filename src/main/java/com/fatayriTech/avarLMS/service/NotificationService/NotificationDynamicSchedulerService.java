package com.fatayriTech.avarLMS.service.NotificationService;

import com.fatayriTech.avarLMS.enums.NotificationEventType;
import com.fatayriTech.avarLMS.enums.NotificationModule;
import com.fatayriTech.avarLMS.model.NotificationRule;
import com.fatayriTech.avarLMS.repository.NotificationRepos.NotificationRuleRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class NotificationDynamicSchedulerService {

    private final NotificationRuleRepo notificationRuleRepo;
    private final NotificationScannerService notificationScannerService;
    private final TaskScheduler taskScheduler;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks =
            new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        refreshSchedules();
    }

    public void refreshSchedules() {
        cancelAllSchedules();

        List<NotificationRule> rules =
                notificationRuleRepo.findByModuleAndEventTypeAndActiveTrue(
                        NotificationModule.ASSIGNMENT,
                        NotificationEventType.EXPIRY_REMINDER
                );

        for (NotificationRule rule : rules) {
            scheduleRule(rule);
        }
    }

    public void scheduleRule(NotificationRule rule) {
        if (rule.getCronExpression() == null || rule.getCronExpression().isBlank()) {
            return;
        }

        ScheduledFuture<?> task = taskScheduler.schedule(
                () -> notificationScannerService.scanRule(rule),
                new CronTrigger(rule.getCronExpression())
        );

        scheduledTasks.put(rule.getId(), task);
    }

    public void cancelSchedule(Long ruleId) {
        ScheduledFuture<?> task = scheduledTasks.remove(ruleId);

        if (task != null) {
            task.cancel(false);
        }
    }

    public void cancelAllSchedules() {
        scheduledTasks.forEach((ruleId, task) -> task.cancel(false));
        scheduledTasks.clear();
    }
}
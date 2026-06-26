package com.fatayriTech.avarLMS.service.Notification;

import com.fatayriTech.avarLMS.model.NotificationRule;
import com.fatayriTech.avarLMS.repository.NotificationRepos.NotificationRuleRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
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

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final Map<Long, String> scheduledCronExpressions = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        refreshSchedules();
    }

    @Scheduled(fixedDelay = 60000)
    public void refreshSchedules() {
        List<NotificationRule> activeRules = notificationRuleRepo.findAll()
                .stream()
                .filter(rule -> Boolean.TRUE.equals(rule.getActive()))
                .filter(rule -> rule.getCronExpression() != null)
                .filter(rule -> !rule.getCronExpression().isBlank())
                .toList();

        for (NotificationRule rule : activeRules) {
            scheduleOrRefresh(rule);
        }

        scheduledTasks.keySet().removeIf(ruleId -> {
            boolean stillActive = activeRules.stream()
                    .anyMatch(rule -> rule.getId().equals(ruleId));

            if (!stillActive) {
                ScheduledFuture<?> task = scheduledTasks.remove(ruleId);

                if (task != null) {
                    task.cancel(false);
                }

                scheduledCronExpressions.remove(ruleId);
                return true;
            }

            return false;
        });
    }

    public void scheduleOrRefresh(NotificationRule rule) {
        String cron = rule.getCronExpression();

        if (cron == null || cron.isBlank()) {
            cancelSchedule(rule.getId());
            return;
        }

        String currentCron = scheduledCronExpressions.get(rule.getId());

        if (cron.equals(currentCron)) {
            return;
        }

        cancelSchedule(rule.getId());

        ScheduledFuture<?> task = taskScheduler.schedule(
                () -> notificationScannerService.scanRule(rule),
                new CronTrigger(cron)
        );

        scheduledTasks.put(rule.getId(), task);
        scheduledCronExpressions.put(rule.getId(), cron);
    }

    public void cancelSchedule(Long ruleId) {
        ScheduledFuture<?> task = scheduledTasks.remove(ruleId);

        if (task != null) {
            task.cancel(false);
        }

        scheduledCronExpressions.remove(ruleId);
    }

    public void cancelAllSchedules() {
        scheduledTasks.forEach((ruleId, task) -> task.cancel(false));
        scheduledTasks.clear();
        scheduledCronExpressions.clear();
    }
}
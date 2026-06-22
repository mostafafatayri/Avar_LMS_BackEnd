package com.fatayriTech.avarLMS.service.NotificationService;

import com.fatayriTech.avarLMS.enums.LearningPathAssignmentStatus;
import com.fatayriTech.avarLMS.enums.NotificationEventType;
import com.fatayriTech.avarLMS.enums.NotificationModule;
import jakarta.transaction.Transactional;
import com.fatayriTech.avarLMS.enums.TrainingAssignmentStatus;
import com.fatayriTech.avarLMS.model.LearningPathAssignment;
import com.fatayriTech.avarLMS.model.NotificationRule;
import com.fatayriTech.avarLMS.model.TrainingAssignment;
import com.fatayriTech.avarLMS.repository.LearningPathAssignmentRepo;
import com.fatayriTech.avarLMS.repository.NotificationRepos.NotificationRuleRepo;
import com.fatayriTech.avarLMS.repository.TrainingAssignmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScannerService {

    private final NotificationRuleRepo notificationRuleRepo;
    private final TrainingAssignmentRepo trainingAssignmentRepo;
    private final LearningPathAssignmentRepo learningPathAssignmentRepo;
    private final NotificationDispatchService notificationDispatchService;

    /*@Scheduled(cron = "0 0 8 * * *")
    public void scanAssignmentExpiryReminders() {
        List<NotificationRule> rules =
                notificationRuleRepo.findByModuleAndEventTypeAndActiveTrue(
                        NotificationModule.ASSIGNMENT,
                        NotificationEventType.EXPIRY_REMINDER
                );

        for (NotificationRule rule : rules) {
            scanRule(rule);
        }
    }*/
    @Transactional
    public void scanRule(NotificationRule rule) {
        if (rule.getDaysBefore() == null && rule.getDaysAfter() == null) {
            return;
        }

        LocalDate targetDate = LocalDate.now();

        if (rule.getDaysBefore() != null) {
            targetDate = targetDate.plusDays(rule.getDaysBefore());
        }

        if (rule.getDaysAfter() != null) {
            targetDate = targetDate.minusDays(rule.getDaysAfter());
        }

        scanTrainingAssignments(rule, targetDate);
        scanLearningPathAssignments(rule, targetDate);
    }

    private void scanTrainingAssignments(
            NotificationRule rule,
            LocalDate targetDate
    ) {
        List<TrainingAssignment> assignments =
                trainingAssignmentRepo.findExpiryReminderAssignments(
                        rule.getOrganizationId(),
                        targetDate,
                        List.of(
                                TrainingAssignmentStatus.ASSIGNED,
                                TrainingAssignmentStatus.IN_PROGRESS
                        )
                );

        for (TrainingAssignment assignment : assignments) {
            String email = assignment.getEmployee().getEmail();

            if (email == null || email.isBlank()) {
                continue;
            }

            String eventKey =
                    "ASSIGNMENT_EXPIRY:TRAINING:" +
                            assignment.getId() +
                            ":" +
                            rule.getCode();

            String subject = render(
                    rule.getSubjectTemplate(),
                    "Training Expiry Reminder",
                    assignment.getTrainingCatalogue().getTitle(),
                    assignment.getExpiryDate()
            );

            String body = render(
                    rule.getBodyTemplate(),
                    "Your assigned training is close to expiry.",
                    assignment.getTrainingCatalogue().getTitle(),
                    assignment.getExpiryDate()
            );

            if (Boolean.TRUE.equals(rule.getChannelEmail())) {
                notificationDispatchService.createEmailEventIfNotExists(
                        rule,
                        eventKey,
                        "TRAINING_ASSIGNMENT",
                        assignment.getId(),
                        assignment.getEmployee().getMasterUserId(),
                        assignment.getEmployee().getId(),
                        email,
                        subject,
                        body
                );
            }

            if (Boolean.TRUE.equals(rule.getChannelInApp())) {
                notificationDispatchService.createInAppEventIfNotExists(
                        rule,
                        eventKey,
                        "TRAINING_ASSIGNMENT",
                        assignment.getId(),
                        assignment.getEmployee().getMasterUserId(),
                        assignment.getEmployee().getId(),
                        subject,
                        body,
                        "/my-trainings"
                );
            }
        }
    }

    private void scanLearningPathAssignments(
            NotificationRule rule,
            LocalDate targetDate
    ) {
        List<LearningPathAssignment> assignments =
                learningPathAssignmentRepo.findByOrganizationIdAndExpiryDateAndStatusInAndActiveTrue(
                        rule.getOrganizationId(),
                        targetDate,
                        List.of(
                                LearningPathAssignmentStatus.ASSIGNED,
                                LearningPathAssignmentStatus.IN_PROGRESS
                        )
                );

        for (LearningPathAssignment assignment : assignments) {
            if (assignment.getTargetType() == null ||
                    !"EMPLOYEE".equals(assignment.getTargetType().name())) {
                continue;
            }

            String eventKey =
                    "ASSIGNMENT_EXPIRY:LEARNING_PATH:" +
                            assignment.getId() +
                            ":" +
                            rule.getCode();

            String subject = render(
                    rule.getSubjectTemplate(),
                    "Learning Path Expiry Reminder",
                    assignment.getLearningPath().getName(),
                    assignment.getExpiryDate()
            );

            String body = render(
                    rule.getBodyTemplate(),
                    "Your assigned learning path is close to expiry.",
                    assignment.getLearningPath().getName(),
                    assignment.getExpiryDate()
            );

            notificationDispatchService.createInAppEventIfNotExists(
                    rule,
                    eventKey,
                    "LEARNING_PATH_ASSIGNMENT",
                    assignment.getId(),
                    null,
                    assignment.getTargetId(),
                    subject,
                    body,
                    "/my-trainings"
            );
        }
    }

    private String render(
            String template,
            String fallback,
            String itemName,
            LocalDate expiryDate
    ) {
        String text = template == null || template.isBlank()
                ? fallback
                : template;

        return text
                .replace("{itemName}", itemName == null ? "-" : itemName)
                .replace("{expiryDate}", expiryDate == null ? "-" : expiryDate.toString());
    }
}
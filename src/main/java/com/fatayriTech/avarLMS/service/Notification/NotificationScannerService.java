package com.fatayriTech.avarLMS.service.Notification;

import com.fatayriTech.avarLMS.enums.*;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.LearningPathAssignment;
import com.fatayriTech.avarLMS.model.NotificationRule;
import com.fatayriTech.avarLMS.model.TrainingAssignment;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.LearningPathAssignmentRepo;
import com.fatayriTech.avarLMS.repository.TrainingAssignmentRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScannerService {

    private final TrainingAssignmentRepo trainingAssignmentRepo;
    private final LearningPathAssignmentRepo learningPathAssignmentRepo;
    private final EmployeeRepo employeeRepo;
    private final NotificationDispatchService notificationDispatchService;

    @Transactional
    public void scanRule(NotificationRule rule) {
        if (!Boolean.TRUE.equals(rule.getActive())) return;

        if (rule.getEventType() == NotificationEventType.ASSIGNED) {
            return;
        }

        LocalDate targetDate = resolveTargetDate(rule);

        switch (rule.getEventType()) {
            case EXPIRY_REMINDER -> scanExpiryReminder(rule, targetDate);
            case EXPIRED -> scanExpired(rule, targetDate);
            default -> {
            }
        }
    }

    private LocalDate resolveTargetDate(NotificationRule rule) {
        LocalDate today = LocalDate.now();

        if (rule.getDaysBefore() != null) {
            return today.plusDays(rule.getDaysBefore());
        }

        if (rule.getDaysAfter() != null) {
            return today.minusDays(rule.getDaysAfter());
        }

        return today;
    }

    private void scanExpiryReminder(NotificationRule rule, LocalDate targetDate) {
        if (rule.getModule() == NotificationModule.ASSIGNMENT ||
                rule.getModule() == NotificationModule.TRAINING) {
            scanTrainingExpiryReminder(rule, targetDate);
        }

        if (rule.getModule() == NotificationModule.ASSIGNMENT ||
                rule.getModule() == NotificationModule.LEARNING_PATH) {
            scanLearningPathExpiryReminder(rule, targetDate);
        }
    }

    private void scanExpired(NotificationRule rule, LocalDate targetDate) {
        if (rule.getModule() == NotificationModule.ASSIGNMENT ||
                rule.getModule() == NotificationModule.TRAINING) {
            scanExpiredTrainingAssignments(rule, targetDate);
        }

        if (rule.getModule() == NotificationModule.ASSIGNMENT ||
                rule.getModule() == NotificationModule.LEARNING_PATH) {
            scanExpiredLearningPathAssignments(rule, targetDate);
        }
    }

    private void scanTrainingExpiryReminder(NotificationRule rule, LocalDate targetDate) {
        List<TrainingAssignment> assignments =
                trainingAssignmentRepo.findExpiryReminderAssignments(
                        rule.getOrganizationId(),
                        targetDate,
                        List.of(
                                TrainingAssignmentStatus.NOT_STARTED,
                                TrainingAssignmentStatus.IN_PROGRESS,
                                TrainingAssignmentStatus.PENDING_APPROVAL
                        )
                );

        for (TrainingAssignment assignment : assignments) {
            sendTrainingNotification(
                    rule,
                    assignment,
                    "ASSIGNMENT_EXPIRY:TRAINING:",
                    "Training Expiry Reminder",
                    "Your assigned training is close to expiry.",
                    "/my-trainings"
            );
        }
    }

    private void scanExpiredTrainingAssignments(NotificationRule rule, LocalDate targetDate) {
        List<TrainingAssignment> assignments =
                trainingAssignmentRepo.findExpiryReminderAssignments(
                        rule.getOrganizationId(),
                        targetDate,
                        List.of(
                                TrainingAssignmentStatus.NOT_STARTED,
                                TrainingAssignmentStatus.IN_PROGRESS,
                                TrainingAssignmentStatus.PENDING_APPROVAL,
                                TrainingAssignmentStatus.OVERDUE
                        )
                );

        for (TrainingAssignment assignment : assignments) {
            sendTrainingNotification(
                    rule,
                    assignment,
                    "ASSIGNMENT_EXPIRED:TRAINING:",
                    "Training Expired",
                    "Your assigned training has expired.",
                    "/my-trainings"
            );
        }
    }

    private void scanLearningPathExpiryReminder(NotificationRule rule, LocalDate targetDate) {
        List<LearningPathAssignment> assignments =
                learningPathAssignmentRepo.findByOrganizationIdAndExpiryDateAndStatusInAndActiveTrue(
                        rule.getOrganizationId(),
                        targetDate,
                        List.of(
                                LearningPathAssignmentStatus.NOT_STARTED,
                                LearningPathAssignmentStatus.IN_PROGRESS,
                                LearningPathAssignmentStatus.PENDING_APPROVAL
                        )
                );

        for (LearningPathAssignment assignment : assignments) {
            sendLearningPathNotification(
                    rule,
                    assignment,
                    "ASSIGNMENT_EXPIRY:LEARNING_PATH:",
                    "Learning Path Expiry Reminder",
                    "Your assigned learning path is close to expiry.",
                    "/my-trainings"
            );
        }
    }

    private void scanExpiredLearningPathAssignments(NotificationRule rule, LocalDate targetDate) {
        List<LearningPathAssignment> assignments =
                learningPathAssignmentRepo.findByOrganizationIdAndExpiryDateAndStatusInAndActiveTrue(
                        rule.getOrganizationId(),
                        targetDate,
                        List.of(
                                LearningPathAssignmentStatus.NOT_STARTED,
                                LearningPathAssignmentStatus.IN_PROGRESS,
                                LearningPathAssignmentStatus.PENDING_APPROVAL,
                                LearningPathAssignmentStatus.OVERDUE
                        )
                );

        for (LearningPathAssignment assignment : assignments) {
            sendLearningPathNotification(
                    rule,
                    assignment,
                    "ASSIGNMENT_EXPIRED:LEARNING_PATH:",
                    "Learning Path Expired",
                    "Your assigned learning path has expired.",
                    "/my-trainings"
            );
        }
    }

    private void sendTrainingNotification(
            NotificationRule rule,
            TrainingAssignment assignment,
            String eventKeyPrefix,
            String fallbackSubject,
            String fallbackBody,
            String actionUrl
    ) {
        if (assignment.getEmployee() == null || assignment.getTrainingCatalogue() == null) {
            return;
        }

        String subject = render(
                rule.getSubjectTemplate(),
                fallbackSubject,
                assignment.getTrainingCatalogue().getTitle(),
                assignment.getExpiryDate()
        );

        String body = render(
                rule.getBodyTemplate(),
                fallbackBody,
                assignment.getTrainingCatalogue().getTitle(),
                assignment.getExpiryDate()
        );

        if (!doesAssignmentMatchRuleRecipient(rule, assignment.getEmployee())) {
            return;
        }
        List<Employee> recipients = resolveRecipients(rule, assignment.getEmployee());

        for (Employee recipient : recipients) {
            sendToEmployee(
                    rule,
                    recipient,
                    eventKeyPrefix + assignment.getId() + ":" + recipient.getId() + ":" + rule.getCode(),
                    "TRAINING_ASSIGNMENT",
                    assignment.getId(),
                    subject,
                    body,
                    actionUrl
            );
        }
    }

    private boolean doesAssignmentMatchRuleRecipient(NotificationRule rule, Employee assignedEmployee) {
        if (assignedEmployee == null) return false;

        NotificationRecipientType recipientType =
                rule.getRecipientType() == null
                        ? NotificationRecipientType.EMPLOYEE
                        : rule.getRecipientType();

        return switch (recipientType) {
            case EMPLOYEE -> true;

            case DEPARTMENT ->
                    assignedEmployee.getDepartment() != null
                            && rule.getRecipientTargetId() != null
                            && assignedEmployee.getDepartment().getId().equals(rule.getRecipientTargetId());

            case EMPLOYEE_TYPE ->
                    assignedEmployee.getEmployeeType() != null
                            && rule.getRecipientTargetCode() != null
                            && assignedEmployee.getEmployeeType().name().equals(rule.getRecipientTargetCode());

            case ACADEMY ->
                    assignedEmployee.getAcademyStatus() != null
                            && rule.getRecipientTargetCode() != null
                            && assignedEmployee.getAcademyStatus().name().equals(rule.getRecipientTargetCode());
        };
    }

    private void sendLearningPathNotification(
            NotificationRule rule,
            LearningPathAssignment assignment,
            String eventKeyPrefix,
            String fallbackSubject,
            String fallbackBody,
            String actionUrl
    ) {
        if (assignment.getLearningPath() == null) {
            return;
        }

        String subject = render(
                rule.getSubjectTemplate(),
                fallbackSubject,
                assignment.getLearningPath().getName(),
                assignment.getExpiryDate()
        );

        String body = render(
                rule.getBodyTemplate(),
                fallbackBody,
                assignment.getLearningPath().getName(),
                assignment.getExpiryDate()
        );

        List<Employee> recipients = resolveRecipients(rule, null);

        for (Employee recipient : recipients) {
            sendToEmployee(
                    rule,
                    recipient,
                    eventKeyPrefix + assignment.getId() + ":" + recipient.getId() + ":" + rule.getCode(),
                    "LEARNING_PATH_ASSIGNMENT",
                    assignment.getId(),
                    subject,
                    body,
                    actionUrl
            );
        }
    }

    private List<Employee> resolveRecipients(NotificationRule rule, Employee defaultEmployee) {
        NotificationRecipientType recipientType =
                rule.getRecipientType() == null
                        ? NotificationRecipientType.EMPLOYEE
                        : rule.getRecipientType();

        return switch (recipientType) {
            case EMPLOYEE -> defaultEmployee == null ? List.of() : List.of(defaultEmployee);

            case DEPARTMENT -> {
                if (rule.getRecipientTargetId() == null) {
                    yield List.of();
                }

                yield employeeRepo.findByDepartmentIdAndOrganizationIdAndActiveTrue(
                        rule.getRecipientTargetId(),
                        rule.getOrganizationId()
                );
            }

            case EMPLOYEE_TYPE -> {
                if (rule.getRecipientTargetCode() == null || rule.getRecipientTargetCode().isBlank()) {
                    yield List.of();
                }

                EmployeeType employeeType = EmployeeType.valueOf(rule.getRecipientTargetCode());

                yield employeeRepo.findByOrganizationIdAndEmployeeTypeAndActiveTrue(
                        rule.getOrganizationId(),
                        employeeType
                );
            }

            case ACADEMY -> {
                if (rule.getRecipientTargetCode() == null || rule.getRecipientTargetCode().isBlank()) {
                    yield List.of();
                }

                AcademyStatus academyStatus = AcademyStatus.valueOf(rule.getRecipientTargetCode());

                yield employeeRepo.findByOrganizationIdAndAcademyStatusAndActiveTrue(
                        rule.getOrganizationId(),
                        academyStatus
                );
            }
        };
    }

    private void sendToEmployee(
            NotificationRule rule,
            Employee recipient,
            String eventKey,
            String targetType,
            Long targetId,
            String subject,
            String body,
            String actionUrl
    ) {
        if (recipient == null) return;

        if (Boolean.TRUE.equals(rule.getChannelEmail())) {
            notificationDispatchService.createEmailEventIfNotExists(
                    rule,
                    eventKey,
                    targetType,
                    targetId,
                    recipient.getMasterUserId(),
                    recipient.getId(),
                    recipient.getEmail(),
                    subject,
                    body
            );
        }

        if (Boolean.TRUE.equals(rule.getChannelInApp())) {
            notificationDispatchService.createInAppEventIfNotExists(
                    rule,
                    eventKey,
                    targetType,
                    targetId,
                    recipient.getMasterUserId(),
                    recipient.getId(),
                    subject,
                    body,
                    actionUrl
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
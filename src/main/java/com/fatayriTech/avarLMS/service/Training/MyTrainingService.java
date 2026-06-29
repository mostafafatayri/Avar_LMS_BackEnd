package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.enums.LearningPathAssignmentTargetType;
import com.fatayriTech.avarLMS.enums.TrainingDisplayItemType;
import com.fatayriTech.avarLMS.enums.TrainingProgressStatus;
import com.fatayriTech.avarLMS.model.*;
import com.fatayriTech.avarLMS.repository.*;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.response.myTraining.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MyTrainingService {

    private final EmployeeRepo employeeRepo;
    private final TrainingAssignmentRepo trainingAssignmentRepo;
    private final LearningPathAssignmentRepo learningPathAssignmentRepo;

    private final TrainingCatalogueRepo trainingCatalogueRepo;
    private final TrainingDisplayModuleRepo moduleRepo;
    private final TrainingDisplayModuleItemRepo itemRepo;
    private final TrainingProgressRepo trainingProgressRepo;

    private final TrainingLectureRepo lectureRepo;
    private final TrainingVideoRepo videoRepo;
    private final TrainingQuizRepo quizRepo;

    public List<MyTrainingResponse> getMyTrainings(Long organizationId, Long userId) {
        Employee employee = employeeRepo
                .findByMasterUserIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new RuntimeException("Employee profile not found for this user"));

        List<MyTrainingResponse> result = new ArrayList<>();

        List<TrainingAssignment> trainingAssignments =
                trainingAssignmentRepo
                        .findByOrganizationIdAndEmployeeIdAndActiveTrueOrderByCreationDateDesc(
                                organizationId,
                                employee.getId()
                        );

        for (TrainingAssignment assignment : trainingAssignments) {
            result.add(mapTraining(assignment));
        }

        List<LearningPathAssignment> pathAssignments =
                learningPathAssignmentRepo
                        .findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(
                                organizationId
                        );

        for (LearningPathAssignment assignment : pathAssignments) {
            if (isAssignedToEmployee(assignment, employee)) {
                result.add(mapLearningPath(assignment));
            }
        }

        return result;
    }

    public MyTrainingDetailsResponse getMyTrainingDetails(
            Long organizationId,
            Long userId,
            Long trainingId
    ) {
        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationIdAndActiveTrue(trainingId, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        List<TrainingDisplayModule> modules =
                moduleRepo.findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
                        organizationId,
                        trainingId
                );

        List<TrainingProgress> progressList =
                trainingProgressRepo.findByOrganizationIdAndUserIdAndTrainingCatalogueId(
                        organizationId,
                        userId,
                        trainingId
                );

        Map<Long, TrainingProgress> progressByItemId = new HashMap<>();
        for (TrainingProgress progress : progressList) {
            progressByItemId.put(progress.getModuleItemId(), progress);
        }

        Integer firstIncompleteRequiredSequence = findFirstIncompleteRequiredSequence(
                organizationId,
                modules,
                progressByItemId
        );

        List<MyTrainingModuleResponse> moduleResponses = new ArrayList<>();

        int totalItems = 0;
        int completedItems = 0;
        int sequence = 0;

        for (TrainingDisplayModule module : modules) {
            List<TrainingDisplayModuleItem> items =
                    itemRepo.findByOrganizationIdAndModuleIdAndActiveTrueOrderByDisplayOrderAsc(
                            organizationId,
                            module.getId()
                    );

            List<MyTrainingModuleItemResponse> itemResponses = new ArrayList<>();

            for (TrainingDisplayModuleItem item : items) {
                sequence++;
                totalItems++;

                TrainingProgress progress = progressByItemId.get(item.getId());

                boolean completed =
                        progress != null &&
                                progress.getStatus() == TrainingProgressStatus.COMPLETED;

                if (completed) {
                    completedItems++;
                }

                boolean locked =
                        firstIncompleteRequiredSequence != null &&
                                sequence > firstIncompleteRequiredSequence;

                itemResponses.add(
                        MyTrainingModuleItemResponse.builder()
                                .id(item.getId())
                                .moduleId(module.getId())
                                .itemType(item.getItemType())
                                .itemRefId(item.getItemRefId())
                                .itemTitle(resolveItemTitle(item))
                                .itemDescription(resolveItemDescription(item))
                                .displayOrder(item.getDisplayOrder())
                                .required(item.getRequired())
                                .progressStatus(
                                        progress != null && progress.getStatus() != null
                                                ? progress.getStatus().name()
                                                : TrainingProgressStatus.NOT_STARTED.name()
                                )
                                .progressPercentage(
                                        progress != null && progress.getProgressPercentage() != null
                                                ? progress.getProgressPercentage()
                                                : 0
                                )
                                .completed(completed)
                                .locked(locked)
                                .build()
                );
            }

            moduleResponses.add(
                    MyTrainingModuleResponse.builder()
                            .id(module.getId())
                            .title(module.getTitle())
                            .description(module.getDescription())
                            .displayOrder(module.getDisplayOrder())
                            .items(itemResponses)
                            .build()
            );
        }

        int progressPercentage = totalItems == 0
                ? 0
                : Math.round((completedItems * 100f) / totalItems);

        return MyTrainingDetailsResponse.builder()
                .trainingCatalogueId(training.getId())
                .title(training.getTitle())
                .description(training.getDescription())
                //.trainingType(training.getTrainingType())
                .trainer(training.getTrainer())
                .trainerEmail(training.getTrainerEmail())
                .durationHours(training.getDurationHours())
                .validityMonths(training.getValidityMonths())
                .certificateEnabled(training.getCertificateEnabled())
                .totalItems(totalItems)
                .completedItems(completedItems)
                .progressPercentage(progressPercentage)
                .modules(moduleResponses)
                .build();
    }

    private Integer findFirstIncompleteRequiredSequence(
            Long organizationId,
            List<TrainingDisplayModule> modules,
            Map<Long, TrainingProgress> progressByItemId
    ) {
        int sequence = 0;

        for (TrainingDisplayModule module : modules) {
            List<TrainingDisplayModuleItem> items =
                    itemRepo.findByOrganizationIdAndModuleIdAndActiveTrueOrderByDisplayOrderAsc(
                            organizationId,
                            module.getId()
                    );

            for (TrainingDisplayModuleItem item : items) {
                sequence++;

                TrainingProgress progress = progressByItemId.get(item.getId());

                boolean completed =
                        progress != null &&
                                progress.getStatus() == TrainingProgressStatus.COMPLETED;

                if (Boolean.TRUE.equals(item.getRequired()) && !completed) {
                    return sequence;
                }
            }
        }

        return null;
    }

    private boolean isAssignedToEmployee(LearningPathAssignment assignment, Employee employee) {
        if (assignment.getTargetType() == null || assignment.getTargetId() == null) {
            return false;
        }

        LearningPathAssignmentTargetType type = assignment.getTargetType();
        Long targetId = assignment.getTargetId();

        return switch (type) {
            case EMPLOYEE -> targetId.equals(employee.getId());

            case DEPARTMENT ->
                    employee.getDepartment() != null &&
                            targetId.equals(employee.getDepartment().getId());

            case ROLE, JOB_TITLE ->
                    employee.getPosition() != null &&
                            targetId.equals(employee.getPosition().getId());

            case LOCATION ->
                    employee.getLocation() != null &&
                            targetId.equals(employee.getLocation().getId());

            case SUB_TEAM, SPECIALIZATION -> false;
        };
    }

    private MyTrainingResponse mapTraining(TrainingAssignment assignment) {
        return MyTrainingResponse.builder()
                .id("TRAINING-" + assignment.getId())
                .rawId(assignment.getId())
                .type("TRAINING")
                .trainingCatalogueId(assignment.getTrainingCatalogue().getId())
                .title(assignment.getTrainingCatalogue().getTitle())
                .subtitle(
                        assignment.getTrainingCatalogue().getTitle() != null
                                ? assignment.getTrainingCatalogue().getTitle()
                                : "Training"
                )
                .assignedVia("Direct Employee Assignment")
                .validityDays(assignment.getValidityDays())
                .expiryDate(assignment.getExpiryDate())
                .status(assignment.getStatus().name())
                .progressPercentage(assignment.getProgressPercentage())
                .build();
    }

    private MyTrainingResponse mapLearningPath(LearningPathAssignment assignment) {
        return MyTrainingResponse.builder()
                .id("PATH-" + assignment.getId())
                .rawId(assignment.getId())
                .type("LEARNING_PATH")
                .learningPathId(assignment.getLearningPath().getId())
                .title(assignment.getLearningPath().getName())
                .subtitle("Learning Path")
                .assignedVia(formatAssignedVia(assignment.getTargetType()))
                .validityDays(assignment.getValidityDays())
                .expiryDate(assignment.getExpiryDate())
                .status(assignment.getStatus().name())
                .progressPercentage(assignment.getProgressPercentage())
                .build();
    }

    private String formatAssignedVia(LearningPathAssignmentTargetType type) {
        if (type == null) {
            return "Assignment";
        }

        return switch (type) {
            case EMPLOYEE -> "Direct Employee Assignment";
            case DEPARTMENT -> "Department Assignment";
            case ROLE -> "Role Assignment";
            case JOB_TITLE -> "Job Title Assignment";
            case LOCATION -> "Location Assignment";
            case SUB_TEAM -> "Sub-Team Assignment";
            case SPECIALIZATION -> "Specialization Assignment";
        };
    }

    private String resolveItemTitle(TrainingDisplayModuleItem item) {
        if (item.getItemType() == TrainingDisplayItemType.LECTURE) {
            return lectureRepo.findById(item.getItemRefId())
                    .map(TrainingLecture::getTitle)
                    .orElse("-");
        }

        if (item.getItemType() == TrainingDisplayItemType.VIDEO) {
            return videoRepo.findById(item.getItemRefId())
                    .map(TrainingVideo::getTitle)
                    .orElse("-");
        }

        if (item.getItemType() == TrainingDisplayItemType.QUIZ) {
            return quizRepo.findById(item.getItemRefId())
                    .map(TrainingQuiz::getTitle)
                    .orElse("-");
        }

        return "-";
    }

    private String resolveItemDescription(TrainingDisplayModuleItem item) {
        if (item.getItemType() == TrainingDisplayItemType.LECTURE) {
            return lectureRepo.findById(item.getItemRefId())
                    .map(TrainingLecture::getDescription)
                    .orElse("-");
        }

        if (item.getItemType() == TrainingDisplayItemType.VIDEO) {
            return videoRepo.findById(item.getItemRefId())
                    .map(TrainingVideo::getDescription)
                    .orElse("-");
        }

        if (item.getItemType() == TrainingDisplayItemType.QUIZ) {
            return quizRepo.findById(item.getItemRefId())
                    .map(TrainingQuiz::getDescription)
                    .orElse("-");
        }

        return "-";
    }
}
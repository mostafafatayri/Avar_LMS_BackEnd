package com.fatayriTech.avarLMS.service.LearningPath;
import com.fatayriTech.avarLMS.enums.TrainingAssignmentStatus;
import com.fatayriTech.avarLMS.response.learningPath.MyLearningPathDetailsResponse;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.fatayriTech.avarLMS.enums.LearningPathAssignmentStatus;
import com.fatayriTech.avarLMS.enums.LearningPathAssignmentTargetType;
import com.fatayriTech.avarLMS.model.*;
import com.fatayriTech.avarLMS.repository.*;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.PositionRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.request.learningPath.LearningPathAssignmentRequest;
import com.fatayriTech.avarLMS.response.learningPath.LearningPathAssignmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class LearningPathAssignmentService {

    private final LearningPathAssignmentRepo assignmentRepo;
    private final LearningPathRepo learningPathRepo;
    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;
    private final PositionRepo positionRepo;
    private final SpecializationRepo specializationRepo;
    private final LocationRepo locationRepo;
    private final SubTeamRepo subTeamRepo;
    private final LearningPathItemRepo learningPathItemRepo;
    private final TrainingAssignmentRepo trainingAssignmentRepo;
    public List<LearningPathAssignmentResponse> getAll(Long organizationId) {
        refreshOverdueAssignments(organizationId);

        return assignmentRepo
                .findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<LearningPathAssignmentResponse> getByLearningPath(
            Long organizationId,
            Long learningPathId
    ) {
        refreshOverdueAssignments(organizationId);

        return assignmentRepo
                .findByOrganizationIdAndLearningPathIdAndActiveTrueOrderByCreationDateDesc(
                        organizationId,
                        learningPathId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public LearningPathAssignmentResponse create(
            Long organizationId,
            Long learningPathId,
            Long assignedBy,
            LearningPathAssignmentRequest request
    ) {
        if (request.getTargetType() == null) {
            throw new RuntimeException("Target type is required");
        }

        if (request.getTargetId() == null) {
            throw new RuntimeException("Target is required");
        }

        LearningPath learningPath = learningPathRepo
                .findByIdAndOrganizationIdAndActiveTrue(learningPathId, organizationId)
                .orElseThrow(() -> new RuntimeException("Learning path not found"));

        validateTargetExists(
                organizationId,
                request.getTargetType(),
                request.getTargetId()
        );

        boolean alreadyAssigned =
                assignmentRepo.existsByOrganizationIdAndLearningPathIdAndTargetTypeAndTargetIdAndActiveTrue(
                        organizationId,
                        learningPathId,
                        request.getTargetType(),
                        request.getTargetId()
                );

        if (alreadyAssigned) {
            throw new RuntimeException("This learning path is already assigned to this target");
        }

        LearningPathAssignment assignment = LearningPathAssignment.builder()
                .organizationId(organizationId)
                .learningPath(learningPath)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .assignedBy(assignedBy)
                .validityDays(request.getValidityDays())
                .status(LearningPathAssignmentStatus.NOT_STARTED)
                .progressPercentage(0)
                .active(true)
                .assignmentRequired(
                        request.getAssignmentRequired() == null
                                ? true
                                : request.getAssignmentRequired()
                )
                .build();

        return mapToResponse(assignmentRepo.save(assignment));
    }

    public List<LearningPathAssignmentResponse> createBatch(
            Long organizationId,
            Long learningPathId,
            Long assignedBy,
            LearningPathAssignmentRequest request
    ) {
        LearningPath learningPath = learningPathRepo
                .findByIdAndOrganizationIdAndActiveTrue(learningPathId, organizationId)
                .orElseThrow(() -> new RuntimeException("Learning path not found"));

        List<Employee> employees = resolveEmployeesForBatchAssignment(
                organizationId,
                request
        );

        if (employees.isEmpty()) {
            throw new RuntimeException("No employees found for the selected assignment target");
        }

        List<LearningPathAssignment> assignments = employees.stream()
                .filter(employee -> !assignmentRepo
                        .existsByOrganizationIdAndLearningPathIdAndTargetTypeAndTargetIdAndActiveTrue(
                                organizationId,
                                learningPathId,
                                LearningPathAssignmentTargetType.EMPLOYEE,
                                employee.getId()
                        )
                )
                .map(employee -> LearningPathAssignment.builder()
                        .organizationId(organizationId)
                        .learningPath(learningPath)
                        .targetType(LearningPathAssignmentTargetType.EMPLOYEE)
                        .targetId(employee.getId())
                        .assignedBy(assignedBy)
                        .validityDays(request.getValidityDays())
                        .status(LearningPathAssignmentStatus.NOT_STARTED)
                        .progressPercentage(0)
                        .active(true)
                        .assignmentRequired(
                                request.getAssignmentRequired() == null
                                        ? true
                                        : request.getAssignmentRequired()
                        )
                        .build()
                )
                .toList();

        if (assignments.isEmpty()) {
            throw new RuntimeException("All selected employees already have this learning path assigned");
        }

        return assignmentRepo.saveAll(assignments)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public LearningPathAssignmentResponse updateProgress(
            Long organizationId,
            Long assignmentId,
            Integer progress
    ) {
        LearningPathAssignment assignment = findAssignment(organizationId, assignmentId);

        int safeProgress = progress == null ? 0 : Math.max(0, Math.min(progress, 100));

        assignment.setProgressPercentage(safeProgress);

        if (safeProgress == 0) {
            assignment.setStatus(LearningPathAssignmentStatus.NOT_STARTED);
            assignment.setCompletionDate(null);
        } else if (safeProgress < 100) {
            assignment.setStatus(LearningPathAssignmentStatus.IN_PROGRESS);
            assignment.setCompletionDate(null);
        } else {
            assignment.setStatus(LearningPathAssignmentStatus.COMPLETED);
            assignment.setCompletionDate(LocalDateTime.now());
        }

        return mapToResponse(assignmentRepo.save(assignment));
    }

    public LearningPathAssignmentResponse markCompleted(
            Long organizationId,
            Long assignmentId
    ) {
        LearningPathAssignment assignment = findAssignment(organizationId, assignmentId);

        assignment.setStatus(LearningPathAssignmentStatus.COMPLETED);
        assignment.setProgressPercentage(100);
        assignment.setCompletionDate(LocalDateTime.now());

        return mapToResponse(assignmentRepo.save(assignment));
    }

    public void delete(Long organizationId, Long id) {
        LearningPathAssignment assignment = findAssignment(organizationId, id);
        assignment.setActive(false);
        assignmentRepo.save(assignment);
    }

    private LearningPathAssignment findAssignment(Long organizationId, Long id) {
        return assignmentRepo
                .findByIdAndOrganizationIdAndActiveTrue(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Learning path assignment not found"));
    }

    private void refreshOverdueAssignments(Long organizationId) {
        List<LearningPathAssignment> overdue =
                assignmentRepo.findByOrganizationIdAndExpiryDateBeforeAndStatusInAndActiveTrue(
                        organizationId,
                        LocalDate.now(),
                        List.of(
                                LearningPathAssignmentStatus.NOT_STARTED,
                                LearningPathAssignmentStatus.IN_PROGRESS,
                                LearningPathAssignmentStatus.PENDING_APPROVAL
                        )
                );

        if (overdue.isEmpty()) {
            return;
        }

        overdue.forEach(item -> item.setStatus(LearningPathAssignmentStatus.OVERDUE));
        assignmentRepo.saveAll(overdue);
    }

    private List<Employee> resolveEmployeesForBatchAssignment(
            Long organizationId,
            LearningPathAssignmentRequest request
    ) {
        if (request.getBatchTargetType() == null) {
            return List.of();
        }

        return switch (request.getBatchTargetType()) {
            case MULTIPLE_EMPLOYEES -> {
                if (request.getEmployeeIds() == null || request.getEmployeeIds().isEmpty()) {
                    throw new RuntimeException("Please select at least one employee");
                }

                yield employeeRepo.findByIdInAndOrganizationIdAndActiveTrue(
                        request.getEmployeeIds(),
                        organizationId
                );
            }

            case EMPLOYEE_TYPE -> {
                if (request.getEmployeeType() == null) {
                    throw new RuntimeException("Employee type is required");
                }

                yield employeeRepo.findByOrganizationIdAndEmployeeTypeAndActiveTrue(
                        organizationId,
                        request.getEmployeeType()
                );
            }

            case ACADEMY -> {
                if (request.getAcademyStatus() == null) {
                    throw new RuntimeException("Academy status is required");
                }

                yield employeeRepo.findByOrganizationIdAndAcademyStatusAndActiveTrue(
                        organizationId,
                        request.getAcademyStatus()
                );
            }
        };
    }

    private void validateTargetExists(
            Long organizationId,
            LearningPathAssignmentTargetType targetType,
            Long targetId
    ) {
        switch (targetType) {
            case EMPLOYEE -> employeeRepo
                    .findByIdAndOrganizationId(targetId, organizationId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            case DEPARTMENT -> departmentRepo
                    .findByIdAndOrganizationId(targetId, organizationId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            case ROLE, JOB_TITLE -> positionRepo
                    .findByIdAndOrganizationId(targetId, organizationId)
                    .orElseThrow(() -> new RuntimeException("Role / Job title not found"));

            case SPECIALIZATION -> specializationRepo
                    .findByIdAndOrganizationId(targetId, organizationId)
                    .orElseThrow(() -> new RuntimeException("Specialization not found"));

            case LOCATION -> locationRepo
                    .findByIdAndOrganizationId(targetId, organizationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));

            case SUB_TEAM -> subTeamRepo
                    .findByIdAndOrganizationId(targetId, organizationId)
                    .orElseThrow(() -> new RuntimeException("Sub-team not found"));
        }
    }

    private String resolveTargetName(
            LearningPathAssignmentTargetType targetType,
            Long targetId,
            Long organizationId
    ) {
        try {
            return switch (targetType) {
                case EMPLOYEE -> employeeRepo
                        .findByIdAndOrganizationId(targetId, organizationId)
                        .map(this::buildEmployeeName)
                        .orElse("-");

                case DEPARTMENT -> departmentRepo
                        .findByIdAndOrganizationId(targetId, organizationId)
                        .map(Department::getName)
                        .orElse("-");

                case ROLE, JOB_TITLE -> positionRepo
                        .findByIdAndOrganizationId(targetId, organizationId)
                        .map(Position::getName)
                        .orElse("-");

                case SPECIALIZATION -> specializationRepo
                        .findByIdAndOrganizationId(targetId, organizationId)
                        .map(Specialization::getName)
                        .orElse("-");

                case LOCATION -> locationRepo
                        .findByIdAndOrganizationId(targetId, organizationId)
                        .map(Location::getName)
                        .orElse("-");

                case SUB_TEAM -> subTeamRepo
                        .findByIdAndOrganizationId(targetId, organizationId)
                        .map(SubTeam::getName)
                        .orElse("-");
            };
        } catch (Exception exception) {
            return "-";
        }
    }

    private LearningPathAssignmentResponse mapToResponse(
            LearningPathAssignment assignment
    ) {
        LearningPath learningPath = assignment.getLearningPath();

        return LearningPathAssignmentResponse.builder()
                .id(assignment.getId())
                .organizationId(assignment.getOrganizationId())
                .learningPathId(learningPath.getId())
                .learningPathName(learningPath.getName())
                .targetType(assignment.getTargetType())
                .targetId(assignment.getTargetId())
                .targetName(
                        resolveTargetName(
                                assignment.getTargetType(),
                                assignment.getTargetId(),
                                assignment.getOrganizationId()
                        )
                )
                .assignedBy(assignment.getAssignedBy())
                .validityDays(assignment.getValidityDays())
                .expiryDate(assignment.getExpiryDate())
                .status(assignment.getStatus())
                .progressPercentage(assignment.getProgressPercentage())
                .assignedDate(assignment.getAssignedDate())
                .completionDate(assignment.getCompletionDate())
                .active(assignment.getActive())
                .creationDate(assignment.getCreationDate())
                .modificationDate(assignment.getModificationDate())
                .assignmentRequired(assignment.getAssignmentRequired())
                .build();
    }

    private String buildEmployeeName(Employee employee) {
        String fullName = String.join(
                " ",
                List.of(
                        employee.getFirstName() == null ? "" : employee.getFirstName(),
                        employee.getMiddleName() == null ? "" : employee.getMiddleName(),
                        employee.getLastName() == null ? "" : employee.getLastName()
                )
        ).trim();

        if (!fullName.isBlank()) {
            return fullName;
        }

        if (employee.getUsername() != null && !employee.getUsername().isBlank()) {
            return employee.getUsername();
        }

        return employee.getEmail() != null ? employee.getEmail() : "-";
    }
    public MyLearningPathDetailsResponse getMyLearningPathDetails(
            Long organizationId,
            Long assignmentId
    ) {
        LearningPathAssignment assignment = findAssignment(organizationId, assignmentId);

        if (assignment.getTargetType() != LearningPathAssignmentTargetType.EMPLOYEE) {
            throw new RuntimeException("Learning path details are only available for employee assignments");
        }

        LearningPath learningPath = assignment.getLearningPath();

        List<LearningPath> pathSequence = new java.util.ArrayList<>();
        pathSequence.add(learningPath);

        pathSequence.addAll(
                learningPathRepo.findByOrganizationIdAndParentLearningPathIdAndActiveTrueOrderByDisplayOrderAscCreationDateAsc(
                        organizationId,
                        learningPath.getId()
                )
        );

        List<TrainingAssignment> employeeTrainingAssignments =
                trainingAssignmentRepo.findByOrganizationIdAndEmployeeIdAndActiveTrueOrderByCreationDateDesc(
                        organizationId,
                        assignment.getTargetId()
                );

        Map<Long, TrainingAssignment> trainingAssignmentByCatalogueId =
                employeeTrainingAssignments
                        .stream()
                        .filter(item -> item.getTrainingCatalogue() != null)
                        .collect(Collectors.toMap(
                                item -> item.getTrainingCatalogue().getId(),
                                item -> item,
                                (first, second) -> first
                        ));

        BuildResult buildResult = buildSectionsAndFlatTrainings(
                organizationId,
                pathSequence,
                trainingAssignmentByCatalogueId
        );

        List<MyLearningPathDetailsResponse.TrainingItem> trainingItems =
                buildResult.flatTrainings();

        int totalTrainings = trainingItems.size();

        int completedTrainings = (int) trainingItems.stream()
                .filter(item -> item.getStatus() == TrainingAssignmentStatus.COMPLETED)
                .count();

        int inProgressTrainings = (int) trainingItems.stream()
                .filter(item -> item.getStatus() == TrainingAssignmentStatus.IN_PROGRESS)
                .count();

        int remainingTrainings = Math.max(totalTrainings - completedTrainings, 0);

        int progress = totalTrainings == 0
                ? 0
                : (int) Math.round((completedTrainings * 100.0) / totalTrainings);

        MyLearningPathDetailsResponse.TrainingItem nextTraining =
                trainingItems.stream()
                        .filter(item -> !Boolean.TRUE.equals(item.getLocked()))
                        .filter(item -> item.getStatus() != TrainingAssignmentStatus.COMPLETED)
                        .findFirst()
                        .orElse(null);

        return MyLearningPathDetailsResponse.builder()
                .assignmentId(assignment.getId())
                .learningPathId(learningPath.getId())
                .title(learningPath.getName())
                .description(learningPath.getDescription())
                .type("Learning Path")
                .assignedVia("Direct Employee Assignment")
                .expiryDate(assignment.getExpiryDate())
                .validityDays(assignment.getValidityDays())
                .status(assignment.getStatus())
                .progress(progress)
                .completedTrainings(completedTrainings)
                .inProgressTrainings(inProgressTrainings)
                .remainingTrainings(remainingTrainings)
                .totalTrainings(totalTrainings)
                .totalDuration(formatTotalDuration(buildResult.allPathItems()))
                .assignmentRequired(assignment.getAssignmentRequired())
                .certificateEnabled(false)
                .nextTraining(nextTraining)
                .trainings(trainingItems)
                .sections(buildResult.sections())
                .build();
    }

    private BuildResult buildSectionsAndFlatTrainings(
            Long organizationId,
            List<LearningPath> pathSequence,
            Map<Long, TrainingAssignment> trainingAssignmentByCatalogueId
    ) {
        boolean previousCompleted = true;
        int globalStep = 1;

        List<MyLearningPathDetailsResponse.PathSection> sections =
                new java.util.ArrayList<>();

        List<MyLearningPathDetailsResponse.TrainingItem> flatTrainings =
                new java.util.ArrayList<>();

        List<LearningPathItem> allPathItems =
                new java.util.ArrayList<>();

        for (int sectionIndex = 0; sectionIndex < pathSequence.size(); sectionIndex++) {
            LearningPath path = pathSequence.get(sectionIndex);

            List<LearningPathItem> pathItems =
                    learningPathItemRepo.findPathItemsWithTraining(
                            organizationId,
                            path.getId()
                    );

            allPathItems.addAll(pathItems);

            List<MyLearningPathDetailsResponse.TrainingItem> sectionTrainings =
                    new java.util.ArrayList<>();

            for (LearningPathItem pathItem : pathItems) {
                TrainingCatalogue training = pathItem.getTrainingCatalogue();

                TrainingAssignment trainingAssignment =
                        trainingAssignmentByCatalogueId.get(training.getId());

                TrainingAssignmentStatus status = trainingAssignment != null
                        ? trainingAssignment.getStatus()
                        : TrainingAssignmentStatus.NOT_STARTED;

                Integer progress = trainingAssignment != null &&
                        trainingAssignment.getProgressPercentage() != null
                        ? trainingAssignment.getProgressPercentage()
                        : 0;

                boolean locked =
                        Boolean.TRUE.equals(path.getLockingEnabled()) &&
                                Boolean.TRUE.equals(pathItem.getLockUntilPreviousCompleted()) &&
                                !previousCompleted;

                MyLearningPathDetailsResponse.TrainingItem trainingItem =
                        MyLearningPathDetailsResponse.TrainingItem.builder()
                                .learningPathItemId(pathItem.getId())
                                .trainingId(training.getId())
                                .trainingAssignmentId(
                                        trainingAssignment == null ? null : trainingAssignment.getId()
                                )
                                .sectionLearningPathId(path.getId())
                                .sectionTitle(path.getName())
                                .step(globalStep)
                                .title(training.getTitle())
                                .type("Training")
                                .duration(formatDuration(training.getDurationHours()))
                                .status(status)
                                .progress(progress)
                                .mandatory(pathItem.getMandatory())
                                .unlocked(!locked)
                                .locked(locked)
                                .lockReason(
                                        locked
                                                ? "Complete previous training to unlock"
                                                : null
                                )
                                .build();

                sectionTrainings.add(trainingItem);
                flatTrainings.add(trainingItem);

                previousCompleted = status == TrainingAssignmentStatus.COMPLETED;
                globalStep++;
            }

            sections.add(
                    MyLearningPathDetailsResponse.PathSection.builder()
                            .learningPathId(path.getId())
                            .title(sectionIndex == 0 ? "Main Path" : path.getName())
                            .description(path.getDescription())
                            .order(sectionIndex + 1)
                            .trainings(sectionTrainings)
                            .build()
            );
        }

        return new BuildResult(sections, flatTrainings, allPathItems);
    }

    private record BuildResult(
            List<MyLearningPathDetailsResponse.PathSection> sections,
            List<MyLearningPathDetailsResponse.TrainingItem> flatTrainings,
            List<LearningPathItem> allPathItems
    ) {
    }

    private String formatTotalDuration(List<LearningPathItem> pathItems) {
        int totalHours = pathItems.stream()
                .map(LearningPathItem::getTrainingCatalogue)
                .filter(Objects::nonNull)
                .map(TrainingCatalogue::getDurationHours)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return formatDuration(totalHours);
    }

    private String formatDuration(Integer hours) {
        if (hours == null || hours <= 0) {
            return "-";
        }

        return hours + "h";
    }






    /// ////
    private List<MyLearningPathDetailsResponse.TrainingItem> buildTrainingItems(
            List<LearningPathItem> pathItems,
            Map<Long, TrainingAssignment> trainingAssignmentByCatalogueId
    ) {
        boolean previousCompleted = true;

        List<MyLearningPathDetailsResponse.TrainingItem> result = new java.util.ArrayList<>();

        for (int index = 0; index < pathItems.size(); index++) {
            LearningPathItem pathItem = pathItems.get(index);
            TrainingCatalogue training = pathItem.getTrainingCatalogue();

            TrainingAssignment trainingAssignment =
                    trainingAssignmentByCatalogueId.get(training.getId());

            TrainingAssignmentStatus status = trainingAssignment != null
                    ? trainingAssignment.getStatus()
                    : TrainingAssignmentStatus.NOT_STARTED;

            Integer progress = trainingAssignment != null &&
                    trainingAssignment.getProgressPercentage() != null
                    ? trainingAssignment.getProgressPercentage()
                    : 0;

            boolean locked =
                    Boolean.TRUE.equals(pathItem.getLockUntilPreviousCompleted()) &&
                            !previousCompleted;

            result.add(
                    MyLearningPathDetailsResponse.TrainingItem.builder()
                            .learningPathItemId(pathItem.getId())
                            .trainingId(training.getId())
                            .trainingAssignmentId(
                                    trainingAssignment == null ? null : trainingAssignment.getId()
                            )
                            .step(index + 1)
                            .title(training.getTitle())
                            .type("Training")
                            .duration(formatDuration(training.getDurationHours()))
                            .status(status)
                            .progress(progress)
                            .mandatory(pathItem.getMandatory())
                            .unlocked(!locked)
                            .locked(locked)
                            .lockReason(
                                    locked
                                            ? "Complete previous training to unlock"
                                            : null
                            )
                            .build()
            );

            previousCompleted = status == TrainingAssignmentStatus.COMPLETED;
        }

        return result;
    }



}
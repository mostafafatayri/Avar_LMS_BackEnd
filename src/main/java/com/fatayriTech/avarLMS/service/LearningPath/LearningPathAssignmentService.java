package com.fatayriTech.avarLMS.service.LearningPath;

import com.fatayriTech.avarLMS.enums.LearningPathAssignmentStatus;
import com.fatayriTech.avarLMS.enums.LearningPathAssignmentTargetType;
import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.LearningPath;
import com.fatayriTech.avarLMS.model.LearningPathAssignment;
import com.fatayriTech.avarLMS.model.Position;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.PositionRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.LearningPathAssignmentRepo;
import com.fatayriTech.avarLMS.repository.LearningPathRepo;
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

        if (assignmentRepo.existsByOrganizationIdAndLearningPathIdAndTargetTypeAndTargetIdAndActiveTrue(
                organizationId,
                learningPathId,
                request.getTargetType(),
                request.getTargetId()
        )) {
            throw new RuntimeException("This learning path is already assigned to this target");
        }

        LearningPathAssignment assignment = LearningPathAssignment.builder()
                .organizationId(organizationId)
                .learningPath(learningPath)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .assignedBy(assignedBy)
                .dueDate(request.getDueDate())
                .status(LearningPathAssignmentStatus.ASSIGNED)
                .progressPercentage(0)
                .active(true)
                .build();

        return mapToResponse(assignmentRepo.save(assignment));
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
            assignment.setStatus(LearningPathAssignmentStatus.ASSIGNED);
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
                assignmentRepo.findByOrganizationIdAndDueDateBeforeAndStatusInAndActiveTrue(
                        organizationId,
                        LocalDate.now(),
                        List.of(
                                LearningPathAssignmentStatus.ASSIGNED,
                                LearningPathAssignmentStatus.IN_PROGRESS
                        )
                );

        if (overdue.isEmpty()) {
            return;
        }

        overdue.forEach(item -> item.setStatus(LearningPathAssignmentStatus.OVERDUE));
        assignmentRepo.saveAll(overdue);
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

            case ROLE -> positionRepo
                    .findByIdAndOrganizationId(targetId, organizationId)
                    .orElseThrow(() -> new RuntimeException("Role / Position not found"));
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
                        .map(Employee::getUsername)
                        .orElse("-");

                case DEPARTMENT -> departmentRepo
                        .findByIdAndOrganizationId(targetId, organizationId)
                        .map(Department::getName)
                        .orElse("-");

                case ROLE -> positionRepo
                        .findByIdAndOrganizationId(targetId, organizationId)
                        .map(Position::getName)
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
                .dueDate(assignment.getDueDate())
                .status(assignment.getStatus())
                .progressPercentage(assignment.getProgressPercentage())
                .assignedDate(assignment.getAssignedDate())
                .completionDate(assignment.getCompletionDate())
                .active(assignment.getActive())
                .creationDate(assignment.getCreationDate())
                .modificationDate(assignment.getModificationDate())
                .build();
    }
}
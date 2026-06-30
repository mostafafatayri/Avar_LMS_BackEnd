package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.enums.TrainingAssignmentStatus;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.TrainingAssignment;
import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.TrainingAssignmentRepo;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.request.training.TrainingAssignmentRequest;
import com.fatayriTech.avarLMS.response.training.TrainingAssignmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingAssignmentService {

    private final TrainingAssignmentRepo trainingAssignmentRepo;
    private final EmployeeRepo employeeRepo;
    private final TrainingCatalogueRepo trainingCatalogueRepo;

    public List<TrainingAssignmentResponse> getAll(Long organizationId) {
        refreshOverdueAssignments(organizationId);

        return trainingAssignmentRepo
                .findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TrainingAssignmentResponse getById(Long organizationId, Long id) {
        TrainingAssignment assignment = findAssignment(organizationId, id);
        return mapToResponse(assignment);
    }

    public TrainingAssignmentResponse create(
            Long organizationId,
            Long assignedBy,
            TrainingAssignmentRequest request
    ) {
        if (request.getEmployeeId() == null) {
            throw new RuntimeException("Employee is required");
        }

        if (request.getTrainingCatalogueId() == null) {
            throw new RuntimeException("Training is required");
        }

        Employee employee = employeeRepo
                .findByIdAndOrganizationId(request.getEmployeeId(), organizationId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationId(request.getTrainingCatalogueId(), organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        boolean alreadyAssigned =
                trainingAssignmentRepo.existsByOrganizationIdAndEmployeeIdAndTrainingCatalogueIdAndActiveTrue(
                        organizationId,
                        employee.getId(),
                        training.getId()
                );

        if (alreadyAssigned) {
            throw new RuntimeException("This training is already assigned to this employee");
        }

        TrainingAssignment assignment = TrainingAssignment.builder()
                .organizationId(organizationId)
                .employee(employee)
                .trainingCatalogue(training)
                .assignedBy(assignedBy)
                .validityDays(request.getValidityDays())
                .status(TrainingAssignmentStatus.NOT_STARTED)
                .progressPercentage(0)
                .active(true)
                .build();

        return mapToResponse(trainingAssignmentRepo.save(assignment));
    }

    public List<TrainingAssignmentResponse> createBatch(
            Long organizationId,
            Long assignedBy,
            TrainingAssignmentRequest request
    ) {
        if (request.getTrainingCatalogueId() == null) {
            throw new RuntimeException("Training is required");
        }

        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationId(request.getTrainingCatalogueId(), organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        List<Employee> employees = resolveEmployeesForBatchAssignment(
                organizationId,
                request
        );

        if (employees.isEmpty()) {
            throw new RuntimeException("No employees found for the selected assignment target");
        }

        List<TrainingAssignment> assignments = employees.stream()
                .filter(employee -> !trainingAssignmentRepo
                        .existsByOrganizationIdAndEmployeeIdAndTrainingCatalogueIdAndActiveTrue(
                                organizationId,
                                employee.getId(),
                                training.getId()
                        )
                )
                .map(employee -> TrainingAssignment.builder()
                        .organizationId(organizationId)
                        .employee(employee)
                        .trainingCatalogue(training)
                        .assignedBy(assignedBy)
                        .validityDays(request.getValidityDays())
                        .status(TrainingAssignmentStatus.NOT_STARTED)
                        .progressPercentage(0)
                        .active(true)
                        .build()
                )
                .toList();

        if (assignments.isEmpty()) {
            throw new RuntimeException("All selected employees already have this training assigned");
        }

        return trainingAssignmentRepo.saveAll(assignments)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TrainingAssignmentResponse updateProgress(
            Long organizationId,
            Long assignmentId,
            Integer progress
    ) {
        TrainingAssignment assignment = findAssignment(organizationId, assignmentId);

        int safeProgress = progress == null ? 0 : Math.max(0, Math.min(progress, 100));

        assignment.setProgressPercentage(safeProgress);

        if (safeProgress == 0) {
            assignment.setStatus(TrainingAssignmentStatus.NOT_STARTED);
            assignment.setCompletionDate(null);
        } else if (safeProgress < 100) {
            assignment.setStatus(TrainingAssignmentStatus.IN_PROGRESS);
            assignment.setCompletionDate(null);
        } else {
            assignment.setStatus(TrainingAssignmentStatus.COMPLETED);
            assignment.setCompletionDate(LocalDateTime.now());
        }

        return mapToResponse(trainingAssignmentRepo.save(assignment));
    }

    public TrainingAssignmentResponse markCompleted(Long organizationId, Long assignmentId) {
        TrainingAssignment assignment = findAssignment(organizationId, assignmentId);

        assignment.setProgressPercentage(100);
        assignment.setStatus(TrainingAssignmentStatus.COMPLETED);
        assignment.setCompletionDate(LocalDateTime.now());

        return mapToResponse(trainingAssignmentRepo.save(assignment));
    }

    public void delete(Long organizationId, Long id) {
        TrainingAssignment assignment = findAssignment(organizationId, id);
        assignment.setActive(false);
        trainingAssignmentRepo.save(assignment);
    }

    private TrainingAssignment findAssignment(Long organizationId, Long id) {
        return trainingAssignmentRepo
                .findByIdAndOrganizationIdAndActiveTrue(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
    }

    private List<Employee> resolveEmployeesForBatchAssignment(
            Long organizationId,
            TrainingAssignmentRequest request
    ) {
        if (request.getBatchTargetType() == null) {
            if (request.getEmployeeId() == null) {
                throw new RuntimeException("Employee is required");
            }

            Employee employee = employeeRepo
                    .findByIdAndOrganizationId(request.getEmployeeId(), organizationId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            return List.of(employee);
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

    private void refreshOverdueAssignments(Long organizationId) {
        List<TrainingAssignment> overdue =
                trainingAssignmentRepo.findByOrganizationIdAndExpiryDateBeforeAndStatusInAndActiveTrue(
                        organizationId,
                        LocalDate.now(),
                        List.of(
                                TrainingAssignmentStatus.NOT_STARTED,
                                TrainingAssignmentStatus.IN_PROGRESS,
                                TrainingAssignmentStatus.PENDING_APPROVAL
                        )
                );

        if (overdue.isEmpty()) {
            return;
        }

        overdue.forEach(item -> item.setStatus(TrainingAssignmentStatus.OVERDUE));
        trainingAssignmentRepo.saveAll(overdue);
    }

    private TrainingAssignmentResponse mapToResponse(TrainingAssignment assignment) {
        Employee employee = assignment.getEmployee();
        TrainingCatalogue training = assignment.getTrainingCatalogue();

        return TrainingAssignmentResponse.builder()
                .id(assignment.getId())
                .organizationId(assignment.getOrganizationId())
                .employeeId(employee.getId())
                .employeeName(buildEmployeeName(employee))
                .employeeEmail(employee.getEmail())
                .trainingCatalogueId(training.getId())
                .trainingTitle(training.getTitle())
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
}
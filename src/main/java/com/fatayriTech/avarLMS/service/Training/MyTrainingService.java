package com.fatayriTech.avarLMS.service.Training;



import com.fatayriTech.avarLMS.enums.LearningPathAssignmentTargetType;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.LearningPathAssignment;
import com.fatayriTech.avarLMS.model.TrainingAssignment;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.LearningPathAssignmentRepo;
import com.fatayriTech.avarLMS.repository.TrainingAssignmentRepo;
import com.fatayriTech.avarLMS.response.myTraining.MyTrainingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyTrainingService {

    private final EmployeeRepo employeeRepo;
    private final TrainingAssignmentRepo trainingAssignmentRepo;
    private final LearningPathAssignmentRepo learningPathAssignmentRepo;

    public List<MyTrainingResponse> getMyTrainings(
            Long organizationId,
            Long userId
    ) {
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

    private boolean isAssignedToEmployee(
            LearningPathAssignment assignment,
            Employee employee
    ) {
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
}
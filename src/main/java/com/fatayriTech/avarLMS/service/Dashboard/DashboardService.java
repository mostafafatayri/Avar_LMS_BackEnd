package com.fatayriTech.avarLMS.service.Dashboard;

import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.TrainingAssignment;
import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.TrainingAssignmentRepo;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.request.dashboard.DashboardFilterRequest;
import com.fatayriTech.avarLMS.response.dashboard.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmployeeRepo employeeRepo;
    private final TrainingCatalogueRepo trainingCatalogueRepo;
    private final TrainingAssignmentRepo trainingAssignmentRepo;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(
            Long organizationId,
            DashboardFilterRequest filter
    ) {
        List<Employee> employees = employeeRepo.findAll()
                .stream()
                .filter(e -> e.getOrganization() != null)
                .filter(e -> organizationId.equals(e.getOrganization().getId()))
                .toList();

        List<TrainingCatalogue> trainings = trainingCatalogueRepo.findAll()
                .stream()
                .filter(t -> organizationId.equals(t.getOrganizationId()))
                .toList();

        List<TrainingAssignment> assignments =
                trainingAssignmentRepo.findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(
                        organizationId
                );

        assignments = applyFilters(assignments, filter);

        long totalAssignments = assignments.size();

        long completed = assignments.stream()
                .filter(this::isCompleted)
                .count();

        long overdue = assignments.stream()
                .filter(this::isOverdue)
                .count();

        long compliant = assignments.stream()
                .filter(this::isCompliant)
                .count();

        int completionRate = percent(completed, totalAssignments);
        int complianceRate = percent(compliant, totalAssignments);

        return DashboardResponse.builder()
                .kpis(
                        DashboardResponse.Kpis.builder()
                                .totalEmployees(employees.stream().filter(Employee::isActive).count())
                                .totalTrainings(trainings.size())
                                .activeTrainings(trainings.stream().filter(t -> Boolean.TRUE.equals(t.getActive())).count())
                                .completionRate(completionRate)
                                .overdueTrainings(overdue)
                                .complianceRate(complianceRate)
                                .trainingAssignments(totalAssignments)
                                .build()
                )
                .completionTrend(buildCompletionTrend(assignments))
                .departmentCompletion(buildDepartmentCompletion(assignments))
                .departmentCompliance(buildDepartmentCompliance(assignments))
                .assignmentStatus(buildStatusOverview(assignments))
                .overdueTrainings(buildOverdueTrainings(assignments))
                .topTrainings(buildTopTrainings(assignments))
                .build();
    }

    private List<TrainingAssignment> applyFilters(
            List<TrainingAssignment> assignments,
            DashboardFilterRequest filter
    ) {
        if (filter == null) {
            return assignments;
        }

        return assignments.stream()
                .filter(a -> filter.getDepartmentId() == null ||
                        (
                                a.getEmployee() != null &&
                                        a.getEmployee().getDepartment() != null &&
                                        filter.getDepartmentId().equals(a.getEmployee().getDepartment().getId())
                        )
                )
                .filter(a -> filter.getRoleId() == null ||
                        (
                                a.getEmployee() != null &&
                                        a.getEmployee().getPosition() != null &&
                                        filter.getRoleId().equals(a.getEmployee().getPosition().getId())
                        )
                )
                .filter(a -> filter.getTrainingId() == null ||
                        (
                                a.getTrainingCatalogue() != null &&
                                        filter.getTrainingId().equals(a.getTrainingCatalogue().getId())
                        )
                )
                .filter(a -> filter.getStatus() == null ||
                        filter.getStatus().isBlank() ||
                        (
                                a.getStatus() != null &&
                                        a.getStatus().name().equalsIgnoreCase(filter.getStatus())
                        )
                )
                .filter(a -> filter.getDueDateFrom() == null ||
                        (
                                a.getExpiryDate() != null &&
                                        !a.getExpiryDate().isBefore(filter.getDueDateFrom())
                        )
                )
                .filter(a -> filter.getDueDateTo() == null ||
                        (
                                a.getExpiryDate() != null &&
                                        !a.getExpiryDate().isAfter(filter.getDueDateTo())
                        )
                )
                .filter(a -> filter.getCompletionDateFrom() == null ||
                        (
                                a.getCompletionDate() != null &&
                                        !a.getCompletionDate().toLocalDate().isBefore(filter.getCompletionDateFrom())
                        )
                )
                .filter(a -> filter.getCompletionDateTo() == null ||
                        (
                                a.getCompletionDate() != null &&
                                        !a.getCompletionDate().toLocalDate().isAfter(filter.getCompletionDateTo())
                        )
                )
                .toList();
    }

    private List<DashboardResponse.ChartPoint> buildCompletionTrend(
            List<TrainingAssignment> assignments
    ) {
        LocalDate today = LocalDate.now();

        List<DashboardResponse.ChartPoint> result = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate month = today.minusMonths(i);

            List<TrainingAssignment> monthAssignments = assignments.stream()
                    .filter(a -> a.getAssignedDate() != null)
                    .filter(a -> a.getAssignedDate().getMonth() == month.getMonth())
                    .filter(a -> a.getAssignedDate().getYear() == month.getYear())
                    .toList();

            long completed = monthAssignments.stream()
                    .filter(this::isCompleted)
                    .count();

            result.add(
                    DashboardResponse.ChartPoint.builder()
                            .label(month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                            .value(percent(completed, monthAssignments.size()))
                            .build()
            );
        }

        return result;
    }

    private List<DashboardResponse.DepartmentMetric> buildDepartmentCompletion(
            List<TrainingAssignment> assignments
    ) {
        Map<String, List<TrainingAssignment>> grouped = assignments.stream()
                .collect(Collectors.groupingBy(this::getDepartmentName));

        return grouped.entrySet()
                .stream()
                .map(entry -> DashboardResponse.DepartmentMetric.builder()
                        .name(entry.getKey())
                        .value(percent(
                                entry.getValue().stream().filter(this::isCompleted).count(),
                                entry.getValue().size()
                        ))
                        .build()
                )
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .toList();
    }

    private List<DashboardResponse.DepartmentMetric> buildDepartmentCompliance(
            List<TrainingAssignment> assignments
    ) {
        Map<String, List<TrainingAssignment>> grouped = assignments.stream()
                .collect(Collectors.groupingBy(this::getDepartmentName));

        return grouped.entrySet()
                .stream()
                .map(entry -> DashboardResponse.DepartmentMetric.builder()
                        .name(entry.getKey())
                        .value(percent(
                                entry.getValue().stream().filter(this::isCompliant).count(),
                                entry.getValue().size()
                        ))
                        .build()
                )
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .toList();
    }

    private List<DashboardResponse.AssignmentStatusMetric> buildStatusOverview(
            List<TrainingAssignment> assignments
    ) {
        long total = assignments.size();

        Map<String, Long> grouped = assignments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStatus() != null ? a.getStatus().name() : "UNKNOWN",
                        Collectors.counting()
                ));

        return grouped.entrySet()
                .stream()
                .map(entry -> DashboardResponse.AssignmentStatusMetric.builder()
                        .name(formatStatus(entry.getKey()))
                        .value(entry.getValue())
                        .percent(percent(entry.getValue(), total))
                        .build()
                )
                .toList();
    }

    private List<DashboardResponse.OverdueTraining> buildOverdueTrainings(
            List<TrainingAssignment> assignments
    ) {
        return assignments.stream()
                .filter(this::isOverdue)
                .collect(Collectors.groupingBy(
                        a -> getTrainingName(a) + "||" + getDepartmentName(a),
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    TrainingAssignment first = entry.getValue().get(0);

                    return DashboardResponse.OverdueTraining.builder()
                            .training(getTrainingName(first))
                            .department(getDepartmentName(first))
                            .overdueBy(getOverdueBy(first))
                            .employees(entry.getValue().size())
                            .build();
                })
                .limit(6)
                .toList();
    }

    private List<DashboardResponse.TopTraining> buildTopTrainings(
            List<TrainingAssignment> assignments
    ) {
        Map<String, List<TrainingAssignment>> grouped = assignments.stream()
                .collect(Collectors.groupingBy(this::getTrainingName));

        return grouped.entrySet()
                .stream()
                .map(entry -> DashboardResponse.TopTraining.builder()
                        .name(entry.getKey())
                        .rate(percent(
                                entry.getValue().stream().filter(this::isCompleted).count(),
                                entry.getValue().size()
                        ))
                        .build()
                )
                .sorted((a, b) -> Integer.compare(b.getRate(), a.getRate()))
                .limit(5)
                .toList();
    }

    private boolean isCompleted(TrainingAssignment assignment) {
        return assignment.getStatus() != null &&
                assignment.getStatus().name().equalsIgnoreCase("COMPLETED");
    }

    private boolean isOverdue(TrainingAssignment assignment) {
        return assignment.getExpiryDate() != null &&
                assignment.getExpiryDate().isBefore(LocalDate.now()) &&
                !isCompleted(assignment);
    }

    private boolean isCompliant(TrainingAssignment assignment) {
        if (!isCompleted(assignment)) {
            return false;
        }

        if (assignment.getExpiryDate() == null || assignment.getCompletionDate() == null) {
            return true;
        }

        return !assignment.getCompletionDate().toLocalDate().isAfter(assignment.getExpiryDate());
    }

    private String getDepartmentName(TrainingAssignment assignment) {
        if (assignment.getEmployee() == null ||
                assignment.getEmployee().getDepartment() == null) {
            return "No Department";
        }

        return assignment.getEmployee().getDepartment().getName();
    }

    private String getTrainingName(TrainingAssignment assignment) {
        if (assignment.getTrainingCatalogue() == null) {
            return "Unknown Training";
        }

        return assignment.getTrainingCatalogue().getTitle();
    }

    private String getOverdueBy(TrainingAssignment assignment) {
        if (assignment.getExpiryDate() == null) {
            return "-";
        }

        long days = LocalDate.now().toEpochDay() - assignment.getExpiryDate().toEpochDay();

        return days <= 0 ? "-" : days + " day(s)";
    }

    private int percent(long value, long total) {
        if (total <= 0) {
            return 0;
        }

        return (int) Math.round((value * 100.0) / total);
    }

    private String formatStatus(String value) {
        return value.replace("_", " ");
    }
}
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
    public DashboardResponse getDashboard(Long organizationId, DashboardFilterRequest filter) {
        List<Employee> employees = employeeRepo.findByOrganizationId(organizationId)
                .stream()
                .filter(Employee::isActive)
                .toList();

        List<TrainingCatalogue> trainings = trainingCatalogueRepo.findAll()
                .stream()
                .filter(training -> organizationId.equals(training.getOrganizationId()))
                .toList();

        List<TrainingAssignment> assignments =
                trainingAssignmentRepo.findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(
                        organizationId
                );

        List<TrainingAssignment> filteredAssignments = applyFilters(assignments, filter);

        long totalAssignments = filteredAssignments.size();
        long completedAssignments = filteredAssignments.stream().filter(this::isCompleted).count();
        long overdueAssignments = filteredAssignments.stream().filter(this::isOverdue).count();
        long compliantAssignments = filteredAssignments.stream().filter(this::isCompliant).count();

        return DashboardResponse.builder()
                .kpis(
                        DashboardResponse.Kpis.builder()
                                .totalEmployees(countFilteredEmployees(employees, filter))
                                .totalTrainings(countFilteredTrainings(trainings, filter))
                                .activeTrainings(countActiveFilteredTrainings(trainings, filter))
                                .completionRate(percent(completedAssignments, totalAssignments))
                                .overdueTrainings(overdueAssignments)
                                .complianceRate(percent(compliantAssignments, totalAssignments))
                                .trainingAssignments(totalAssignments)
                                .build()
                )
                .completionTrend(buildCompletionTrend(filteredAssignments))
                .departmentCompletion(buildDepartmentCompletion(filteredAssignments))
                .departmentCompliance(buildDepartmentCompliance(filteredAssignments))
                .mandatoryCompletionByDepartment(
                        buildMandatoryCompletionByDepartment(filteredAssignments)
                )
                .assignmentStatus(buildStatusOverview(filteredAssignments))
                .overdueTrainings(buildOverdueTrainings(filteredAssignments))
                .topTrainings(buildTopTrainings(filteredAssignments))
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
                .filter(assignment -> matchesDepartment(assignment, filter.getDepartmentId()))
                .filter(assignment -> matchesLocation(assignment, filter.getLocationId()))
                .filter(assignment -> matchesRole(assignment, filter.getRoleId()))
                .filter(assignment -> matchesTraining(assignment, filter.getTrainingId()))
                .filter(assignment -> matchesStatus(assignment, filter.getStatus()))
                .filter(assignment -> matchesAcademyStatus(assignment, filter.getAcademyStatus()))
                .filter(assignment -> matchesModule(assignment, filter.getModule()))
                .filter(assignment -> matchesDueDateFrom(assignment, filter.getDueDateFrom()))
                .filter(assignment -> matchesDueDateTo(assignment, filter.getDueDateTo()))
                .filter(assignment -> matchesCompletionDateFrom(assignment, filter.getCompletionDateFrom()))
                .filter(assignment -> matchesCompletionDateTo(assignment, filter.getCompletionDateTo()))
                .toList();
    }

    private long countFilteredEmployees(List<Employee> employees, DashboardFilterRequest filter) {
        if (filter == null) {
            return employees.size();
        }

        return employees.stream()
                .filter(employee -> filter.getDepartmentId() == null ||
                        (
                                employee.getDepartment() != null &&
                                        filter.getDepartmentId().equals(employee.getDepartment().getId())
                        )
                )
                .filter(employee -> filter.getLocationId() == null ||
                        (
                                employee.getLocation() != null &&
                                        filter.getLocationId().equals(employee.getLocation().getId())
                        )
                )
                .filter(employee -> filter.getRoleId() == null ||
                        (
                                employee.getPosition() != null &&
                                        filter.getRoleId().equals(employee.getPosition().getId())
                        )
                )
                .filter(employee -> filter.getAcademyStatus() == null ||
                        filter.getAcademyStatus().isBlank() ||
                        (
                                employee.getAcademyStatus() != null &&
                                        employee.getAcademyStatus().name()
                                                .equalsIgnoreCase(filter.getAcademyStatus())
                        )
                )
                .count();
    }

    private long countFilteredTrainings(
            List<TrainingCatalogue> trainings,
            DashboardFilterRequest filter
    ) {
        return trainings.stream()
                .filter(training -> matchesTrainingCatalogueFilters(training, filter))
                .count();
    }

    private long countActiveFilteredTrainings(
            List<TrainingCatalogue> trainings,
            DashboardFilterRequest filter
    ) {
        return trainings.stream()
                .filter(training -> matchesTrainingCatalogueFilters(training, filter))
                .filter(training -> Boolean.TRUE.equals(training.getActive()))
                .count();
    }

    private boolean matchesTrainingCatalogueFilters(
            TrainingCatalogue training,
            DashboardFilterRequest filter
    ) {
        if (filter == null) {
            return true;
        }

        if (filter.getTrainingId() != null &&
                !filter.getTrainingId().equals(training.getId())) {
            return false;
        }

        if (filter.getModule() != null &&
                !filter.getModule().isBlank() &&
                (
                        training.getModule() == null ||
                                !training.getModule().name().equalsIgnoreCase(filter.getModule())
                )) {
            return false;
        }

        return true;
    }

    private boolean matchesDepartment(TrainingAssignment assignment, Long departmentId) {
        if (departmentId == null) return true;

        return assignment.getEmployee() != null &&
                assignment.getEmployee().getDepartment() != null &&
                departmentId.equals(assignment.getEmployee().getDepartment().getId());
    }

    private boolean matchesLocation(TrainingAssignment assignment, Long locationId) {
        if (locationId == null) return true;

        return assignment.getEmployee() != null &&
                assignment.getEmployee().getLocation() != null &&
                locationId.equals(assignment.getEmployee().getLocation().getId());
    }

    private boolean matchesRole(TrainingAssignment assignment, Long roleId) {
        if (roleId == null) return true;

        return assignment.getEmployee() != null &&
                assignment.getEmployee().getPosition() != null &&
                roleId.equals(assignment.getEmployee().getPosition().getId());
    }

    private boolean matchesTraining(TrainingAssignment assignment, Long trainingId) {
        if (trainingId == null) return true;

        return assignment.getTrainingCatalogue() != null &&
                trainingId.equals(assignment.getTrainingCatalogue().getId());
    }

    private boolean matchesStatus(TrainingAssignment assignment, String status) {
        if (status == null || status.isBlank()) return true;

        return assignment.getStatus() != null &&
                assignment.getStatus().name().equalsIgnoreCase(status);
    }

    private boolean matchesAcademyStatus(TrainingAssignment assignment, String academyStatus) {
        if (academyStatus == null || academyStatus.isBlank()) return true;

        return assignment.getEmployee() != null &&
                assignment.getEmployee().getAcademyStatus() != null &&
                assignment.getEmployee().getAcademyStatus().name().equalsIgnoreCase(academyStatus);
    }

    private boolean matchesModule(TrainingAssignment assignment, String module) {
        if (module == null || module.isBlank()) return true;

        return assignment.getTrainingCatalogue() != null &&
                assignment.getTrainingCatalogue().getModule() != null &&
                assignment.getTrainingCatalogue().getModule().name().equalsIgnoreCase(module);
    }

    private boolean matchesDueDateFrom(TrainingAssignment assignment, LocalDate from) {
        if (from == null) return true;

        return assignment.getExpiryDate() != null &&
                !assignment.getExpiryDate().isBefore(from);
    }

    private boolean matchesDueDateTo(TrainingAssignment assignment, LocalDate to) {
        if (to == null) return true;

        return assignment.getExpiryDate() != null &&
                !assignment.getExpiryDate().isAfter(to);
    }

    private boolean matchesCompletionDateFrom(TrainingAssignment assignment, LocalDate from) {
        if (from == null) return true;

        return assignment.getCompletionDate() != null &&
                !assignment.getCompletionDate().toLocalDate().isBefore(from);
    }

    private boolean matchesCompletionDateTo(TrainingAssignment assignment, LocalDate to) {
        if (to == null) return true;

        return assignment.getCompletionDate() != null &&
                !assignment.getCompletionDate().toLocalDate().isAfter(to);
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
        return buildDepartmentPercentageMetric(assignments, false);
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
                        .build())
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(8)
                .toList();
    }

    private List<DashboardResponse.DepartmentMetric> buildMandatoryCompletionByDepartment(
            List<TrainingAssignment> assignments
    ) {
        List<TrainingAssignment> mandatoryAssignments = assignments.stream()
                .filter(assignment -> Boolean.TRUE.equals(assignment.getAssignmentRequired()))
                .toList();

        return buildDepartmentPercentageMetric(mandatoryAssignments, false);
    }

    private List<DashboardResponse.DepartmentMetric> buildDepartmentPercentageMetric(
            List<TrainingAssignment> assignments,
            boolean limitResults
    ) {
        Map<String, List<TrainingAssignment>> grouped = assignments.stream()
                .collect(Collectors.groupingBy(this::getDepartmentName));

        var stream = grouped.entrySet()
                .stream()
                .map(entry -> DashboardResponse.DepartmentMetric.builder()
                        .name(entry.getKey())
                        .value(percent(
                                entry.getValue().stream().filter(this::isCompleted).count(),
                                entry.getValue().size()
                        ))
                        .build())
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        if (limitResults) {
            return stream.limit(5).toList();
        }

        return stream.toList();
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
                        .build())
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
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
                .limit(8)
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
                        .build())
                .sorted((a, b) -> Integer.compare(b.getRate(), a.getRate()))
                .limit(8)
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
        if (!Boolean.TRUE.equals(assignment.getAssignmentRequired())) {
            return true;
        }

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
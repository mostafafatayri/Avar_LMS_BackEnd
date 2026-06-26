package com.fatayriTech.avarLMS.response.dashboard;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {

    private Kpis kpis;

    private List<ChartPoint> completionTrend;
    private List<DepartmentMetric> departmentCompletion;
    private List<DepartmentMetric> departmentCompliance;
    private List<AssignmentStatusMetric> assignmentStatus;

    private List<OverdueTraining> overdueTrainings;
    private List<TopTraining> topTrainings;

    @Getter
    @Builder
    public static class Kpis {
        private long totalEmployees;
        private long totalTrainings;
        private long activeTrainings;
        private int completionRate;
        private long overdueTrainings;
        private int complianceRate;
        private long trainingAssignments;
    }

    @Getter
    @Builder
    public static class ChartPoint {
        private String label;
        private int value;
    }

    @Getter
    @Builder
    public static class DepartmentMetric {
        private String name;
        private int value;
    }

    @Getter
    @Builder
    public static class AssignmentStatusMetric {
        private String name;
        private long value;
        private int percent;
    }

    @Getter
    @Builder
    public static class OverdueTraining {
        private String training;
        private String department;
        private String overdueBy;
        private long employees;
    }

    @Getter
    @Builder
    public static class TopTraining {
        private String name;
        private int rate;
    }
}
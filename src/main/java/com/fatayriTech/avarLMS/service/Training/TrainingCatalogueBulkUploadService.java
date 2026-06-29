package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.enums.TrainingCatalogueStatus;
import com.fatayriTech.avarLMS.enums.TrainingModule;
import com.fatayriTech.avarLMS.enums.TrainingType;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.response.training.TrainingCatalogueBulkUploadFailedRow;
import com.fatayriTech.avarLMS.response.training.TrainingCatalogueBulkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingCatalogueBulkUploadService {

    private final TrainingCatalogueRepo trainingCatalogueRepo;
    private final EmployeeRepo employeeRepo;

    public TrainingCatalogueBulkUploadResponse uploadTrainings(
            Long organizationId,
            MultipartFile file
    ) {
        int totalRows = 0;
        int insertedRows = 0;
        int failedRows = 0;

        List<TrainingCatalogueBulkUploadFailedRow> failedRecords = new ArrayList<>();

        try (
                InputStream inputStream = file.getInputStream();
                Workbook workbook = WorkbookFactory.create(inputStream)
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (isEmptyRow(row)) {
                    continue;
                }

                totalRows++;

                String title = getCellValue(row.getCell(0));
                String description = getCellValue(row.getCell(1));
                String moduleValue = getCellValue(row.getCell(2));
                String trainingTypeValue = getCellValue(row.getCell(3));
                String statusValue = getCellValue(row.getCell(4));
                String durationHoursValue = getCellValue(row.getCell(5));
                String validityMonthsValue = getCellValue(row.getCell(6));
                String passingScoreValue = getCellValue(row.getCell(7));
                String kpiWeightValue = getCellValue(row.getCell(8));
                String trainerEmployeeIdValue = getCellValue(row.getCell(9));
                String externalTrainerName = getCellValue(row.getCell(10));
                String externalTrainerEmail = getCellValue(row.getCell(11));
                String hasLiveSessionValue = getCellValue(row.getCell(12));
                String liveSessionDateTimeValue = getCellValue(row.getCell(13));
                String meetingLink = getCellValue(row.getCell(14));
                String recordingUrl = getCellValue(row.getCell(15));
                String recordingAccess = getCellValue(row.getCell(16));
                String autoRenewValue = getCellValue(row.getCell(17));
                String renewalLeadTimeValue = getCellValue(row.getCell(18));
                String certificateValue = getCellValue(row.getCell(19));
                String refresherValue = getCellValue(row.getCell(20));
                String assessmentValue = getCellValue(row.getCell(21));
                String approvalValue = getCellValue(row.getCell(22));

                try {
                    if (title == null || title.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, title, trainingTypeValue, "Training title is required."));
                        continue;
                    }

                    TrainingModule module = parseModule(moduleValue);
                    TrainingType trainingType = parseTrainingType(trainingTypeValue);
                    TrainingCatalogueStatus status = parseStatus(statusValue);

                    boolean hasLiveSession = parseBoolean(hasLiveSessionValue);
                    boolean autoRenew = parseBoolean(autoRenewValue);

                    LocalDateTime liveSessionDateTime = parseDateTime(liveSessionDateTimeValue);

                    if (hasLiveSession && liveSessionDateTime == null) {
                        failedRows++;
                        failedRecords.add(failed(i, title, trainingTypeValue, "Live session date and time is required when Has Live Session is Yes."));
                        continue;
                    }

                    Integer renewalLeadTimeDays = parseInteger(renewalLeadTimeValue);

                    if (autoRenew && renewalLeadTimeDays == null) {
                        failedRows++;
                        failedRecords.add(failed(i, title, trainingTypeValue, "Renewal lead-time is required when Auto Renew is Yes."));
                        continue;
                    }

                    Long trainerEmployeeId = null;
                    String trainerName = null;
                    String trainerEmail = null;

                    if (trainingType == TrainingType.EXTERNAL) {
                        if (externalTrainerName == null || externalTrainerName.isBlank()) {
                            failedRows++;
                            failedRecords.add(failed(i, title, trainingTypeValue, "External trainer name is required for External trainings."));
                            continue;
                        }

                        trainerName = externalTrainerName.trim();
                        trainerEmail = emptyToNull(externalTrainerEmail);
                    } else {
                        if (trainerEmployeeIdValue != null && !trainerEmployeeIdValue.isBlank()) {
                            trainerEmployeeId = parseLong(trainerEmployeeIdValue);

                            Employee trainerEmployee = employeeRepo
                                    .findByIdAndOrganizationId(trainerEmployeeId, organizationId)
                                    .orElse(null);

                            if (trainerEmployee == null) {
                                failedRows++;
                                failedRecords.add(failed(i, title, trainingTypeValue, "Trainer employee ID was not found."));
                                continue;
                            }

                            trainerName = buildFullName(trainerEmployee);
                            trainerEmail = trainerEmployee.getEmail();
                        }
                    }

                    TrainingCatalogue training = new TrainingCatalogue();
                    training.setOrganizationId(organizationId);
                    training.setTitle(title.trim());
                    training.setDescription(emptyToNull(description));
                    training.setModule(module);
                    training.setTrainingType(trainingType);
                    training.setStatus(status);
                    training.setDurationHours(parseInteger(durationHoursValue));
                    training.setValidityMonths(parseInteger(validityMonthsValue));
                    training.setPassingScore(clampPercentage(parseInteger(passingScoreValue)));
                    training.setKpiWeightPercentage(clampPercentage(parseInteger(kpiWeightValue)));

                    training.setTrainerEmployeeId(trainerEmployeeId);
                    training.setTrainer(trainerName);
                    training.setTrainerEmail(trainerEmail);

                    training.setHasLiveSession(hasLiveSession);
                    training.setLiveSessionDateTime(hasLiveSession ? liveSessionDateTime : null);
                    training.setMeetingLink(hasLiveSession ? emptyToNull(meetingLink) : null);
                    training.setRecordingUrl(hasLiveSession ? emptyToNull(recordingUrl) : null);
                    training.setRecordingAccess(hasLiveSession ? emptyToNull(recordingAccess) : null);

                    training.setAutoRenew(autoRenew);
                    training.setRenewalLeadTimeDays(autoRenew ? renewalLeadTimeDays : null);

                    training.setCertificateEnabled(parseBoolean(certificateValue));
                    training.setRefresher(parseBoolean(refresherValue));
                    training.setAssessment(parseBoolean(assessmentValue));
                    training.setApproval(parseBoolean(approvalValue));
                    training.setActive(true);

                    trainingCatalogueRepo.save(training);
                    insertedRows++;

                } catch (Exception e) {
                    failedRows++;
                    failedRecords.add(failed(
                            i,
                            title,
                            trainingTypeValue,
                            e.getMessage() != null ? e.getMessage() : "Unexpected error while processing this row."
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload training catalogue file", e);
        }

        return new TrainingCatalogueBulkUploadResponse(
                totalRows,
                insertedRows,
                failedRows,
                failedRecords
        );
    }

    private TrainingCatalogueBulkUploadFailedRow failed(
            int rowIndex,
            String title,
            String trainingType,
            String reason
    ) {
        return new TrainingCatalogueBulkUploadFailedRow(
                rowIndex + 1,
                title,
                trainingType,
                reason
        );
    }

    private TrainingModule parseModule(String value) {
        if (value == null || value.isBlank()) {
            return TrainingModule.L_AND_D;
        }

        return switch (normalize(value)) {
            case "L_AND_D", "L_D", "LD", "L&D" -> TrainingModule.L_AND_D;
            case "ACADEMY" -> TrainingModule.ACADEMY;
            default -> throw new RuntimeException("Invalid module: " + value);
        };
    }

    private TrainingType parseTrainingType(String value) {
        if (value == null || value.isBlank()) {
            return TrainingType.ONLINE;
        }

        return switch (normalize(value)) {
            case "ONLINE" -> TrainingType.ONLINE;
            case "CLASSROOM" -> TrainingType.CLASSROOM;
            case "LIVE_SESSION" -> TrainingType.LIVE_SESSION;
            case "PRACTICAL" -> TrainingType.PRACTICAL;
            case "ON_THE_JOB" -> TrainingType.ON_THE_JOB;
            case "EXTERNAL" -> TrainingType.EXTERNAL;
            case "VENDOR" -> TrainingType.VENDOR;
            case "REFRESHER" -> TrainingType.REFRESHER;
            case "ASSESSMENT_ONLY" -> TrainingType.ASSESSMENT_ONLY;
            default -> throw new RuntimeException("Invalid training type: " + value);
        };
    }

    private TrainingCatalogueStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            return TrainingCatalogueStatus.DRAFT;
        }

        return switch (normalize(value)) {
            case "DRAFT" -> TrainingCatalogueStatus.DRAFT;
            case "ACTIVE" -> TrainingCatalogueStatus.ACTIVE;
            case "INACTIVE" -> TrainingCatalogueStatus.INACTIVE;
            case "ARCHIVED" -> TrainingCatalogueStatus.ARCHIVED;
            default -> throw new RuntimeException("Invalid status: " + value);
        };
    }

    private boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String normalized = value.trim().toLowerCase();

        return normalized.equals("true")
                || normalized.equals("yes")
                || normalized.equals("y")
                || normalized.equals("1")
                || normalized.equals("active");
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            throw new RuntimeException("Invalid number: " + value);
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Long.parseLong(value.trim());
        } catch (Exception e) {
            throw new RuntimeException("Invalid ID: " + value);
        }
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(value.trim());
        } catch (Exception e) {
            throw new RuntimeException("Invalid date time. Use format: yyyy-MM-ddTHH:mm, example: 2026-06-29T12:30");
        }
    }

    private Integer clampPercentage(Integer value) {
        if (value == null) {
            return null;
        }

        return Math.max(0, Math.min(value, 100));
    }

    private String normalize(String value) {
        return value.trim()
                .toUpperCase()
                .replace("&", "AND")
                .replace("/", "_")
                .replace("-", "_")
                .replace(" ", "_");
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = 0; i <= 22; i++) {
            String value = getCellValue(row.getCell(i));
            if (value != null && !value.isBlank()) {
                return false;
            }
        }

        return true;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);

        return value == null ? null : value.trim();
    }

    private String buildFullName(Employee employee) {
        return String.join(" ",
                employee.getFirstName() != null ? employee.getFirstName() : "",
                employee.getMiddleName() != null ? employee.getMiddleName() : "",
                employee.getLastName() != null ? employee.getLastName() : ""
        ).trim().replaceAll(" +", " ");
    }
}
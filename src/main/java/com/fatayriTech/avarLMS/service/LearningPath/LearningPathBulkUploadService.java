package com.fatayriTech.avarLMS.service.LearningPath;

import com.fatayriTech.avarLMS.enums.LearningPathCompletionType;
import com.fatayriTech.avarLMS.enums.LearningPathModule;
import com.fatayriTech.avarLMS.enums.LearningPathStatus;
import com.fatayriTech.avarLMS.model.LearningPath;
import com.fatayriTech.avarLMS.repository.LearningPathRepo;
import com.fatayriTech.avarLMS.response.learningPath.LearningPathBulkUploadFailedRow;
import com.fatayriTech.avarLMS.response.learningPath.LearningPathBulkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningPathBulkUploadService {

    private final LearningPathRepo learningPathRepo;

    public LearningPathBulkUploadResponse uploadLearningPaths(
            Long organizationId,
            MultipartFile file
    ) {
        int totalRows = 0;
        int insertedRows = 0;
        int failedRows = 0;

        List<LearningPathBulkUploadFailedRow> failedRecords = new ArrayList<>();

        try (
                InputStream inputStream = file.getInputStream();
                Workbook workbook = WorkbookFactory.create(inputStream)
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (isEmptyRow(row)) continue;

                totalRows++;

                String name = getCellValue(row.getCell(0));
                String description = getCellValue(row.getCell(1));
                String moduleValue = getCellValue(row.getCell(2));
                String durationDaysValue = getCellValue(row.getCell(3));
                String completionTypeValue = getCellValue(row.getCell(4));
                String completionPercentageValue = getCellValue(row.getCell(5));
                String completionCountValue = getCellValue(row.getCell(6));
                String lockingEnabledValue = getCellValue(row.getCell(7));
                String statusValue = getCellValue(row.getCell(8));
                String approvalRequiredValue = getCellValue(row.getCell(9));
                String parentLearningPathName = getCellValue(row.getCell(10));

                try {
                    if (name == null || name.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, moduleValue, "Learning path name is required."));
                        continue;
                    }

                    if (learningPathRepo.existsByOrganizationIdAndNameIgnoreCaseAndActiveTrue(
                            organizationId,
                            name.trim()
                    )) {
                        failedRows++;
                        failedRecords.add(failed(i, name, moduleValue, "Learning path already exists."));
                        continue;
                    }

                    LearningPathModule module = parseModule(moduleValue);
                    LearningPathCompletionType completionType = parseCompletionType(completionTypeValue);
                    LearningPathStatus status = parseStatus(statusValue);

                    Integer completionPercentage = parseInteger(completionPercentageValue);
                    Integer completionCount = parseInteger(completionCountValue);

                    if (completionType == LearningPathCompletionType.PERCENTAGE) {
                        if (completionPercentage == null || completionPercentage < 1 || completionPercentage > 100) {
                            failedRows++;
                            failedRecords.add(failed(i, name, moduleValue, "Completion percentage must be between 1 and 100."));
                            continue;
                        }
                        completionCount = null;
                    }

                    if (completionType == LearningPathCompletionType.SPECIFIC_COUNT) {
                        if (completionCount == null || completionCount < 1) {
                            failedRows++;
                            failedRecords.add(failed(i, name, moduleValue, "Completion count is required."));
                            continue;
                        }
                        completionPercentage = null;
                    }

                    LearningPath parent = null;

                    if (parentLearningPathName != null && !parentLearningPathName.isBlank()) {
                        parent = learningPathRepo
                                .findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(organizationId)
                                .stream()
                                .filter(path -> path.getName().equalsIgnoreCase(parentLearningPathName.trim()))
                                .findFirst()
                                .orElse(null);

                        if (parent == null) {
                            failedRows++;
                            failedRecords.add(failed(i, name, moduleValue, "Parent learning path was not found."));
                            continue;
                        }
                    }

                    LearningPath path = LearningPath.builder()
                            .organizationId(organizationId)
                            .name(name.trim())
                            .description(emptyToNull(description))
                            .module(module)
                            .durationDays(parseInteger(durationDaysValue) == null ? 0 : parseInteger(durationDaysValue))
                            .completionType(completionType)
                            .completionPercentage(completionPercentage)
                            .completionCount(completionCount)
                            .lockingEnabled(completionType == LearningPathCompletionType.PERCENTAGE && parseBoolean(lockingEnabledValue))
                            .status(status)
                            .approvalRequired(parseBoolean(approvalRequiredValue))
                            .parentLearningPath(parent)
                            .active(true)
                            .build();

                    learningPathRepo.save(path);
                    insertedRows++;

                } catch (Exception e) {
                    failedRows++;
                    failedRecords.add(failed(
                            i,
                            name,
                            moduleValue,
                            e.getMessage() != null ? e.getMessage() : "Unexpected error while processing row."
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload learning paths file", e);
        }

        return new LearningPathBulkUploadResponse(
                totalRows,
                insertedRows,
                failedRows,
                failedRecords
        );
    }

    private LearningPathBulkUploadFailedRow failed(
            int rowIndex,
            String name,
            String module,
            String reason
    ) {
        return new LearningPathBulkUploadFailedRow(
                rowIndex + 1,
                name,
                module,
                reason
        );
    }

    private LearningPathModule parseModule(String value) {
        if (value == null || value.isBlank()) return LearningPathModule.L_AND_D;

        return switch (normalize(value)) {
            case "L_AND_D", "L_D", "LD", "L&D" -> LearningPathModule.L_AND_D;
            case "AVAR_ACADEMY", "ACADEMY" -> LearningPathModule.AVAR_ACADEMY;
            default -> throw new RuntimeException("Invalid module: " + value);
        };
    }

    private LearningPathCompletionType parseCompletionType(String value) {
        if (value == null || value.isBlank()) return LearningPathCompletionType.PERCENTAGE;

        return switch (normalize(value)) {
            case "PERCENTAGE" -> LearningPathCompletionType.PERCENTAGE;
            case "SPECIFIC_COUNT", "COUNT" -> LearningPathCompletionType.SPECIFIC_COUNT;
            default -> throw new RuntimeException("Invalid completion type: " + value);
        };
    }

    private LearningPathStatus parseStatus(String value) {
        if (value == null || value.isBlank()) return LearningPathStatus.DRAFT;

        return switch (normalize(value)) {
            case "DRAFT" -> LearningPathStatus.DRAFT;
            case "ACTIVE" -> LearningPathStatus.ACTIVE;
            case "INACTIVE" -> LearningPathStatus.INACTIVE;
            case "ARCHIVED" -> LearningPathStatus.ARCHIVED;
            default -> throw new RuntimeException("Invalid status: " + value);
        };
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) return null;

        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            throw new RuntimeException("Invalid number: " + value);
        }
    }

    private boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) return false;

        String normalized = value.trim().toLowerCase();

        return normalized.equals("true")
                || normalized.equals("yes")
                || normalized.equals("y")
                || normalized.equals("1");
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
        if (row == null) return true;

        for (int i = 0; i <= 10; i++) {
            String value = getCellValue(row.getCell(i));
            if (value != null && !value.isBlank()) return false;
        }

        return true;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);

        return value == null ? null : value.trim();
    }
}
package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.model.Specialization;
import com.fatayriTech.avarLMS.model.SubTeam;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.repository.SpecializationRepo;
import com.fatayriTech.avarLMS.repository.SubTeamRepo;
import com.fatayriTech.avarLMS.response.SpecializationBulkUploadFailedRow;
import com.fatayriTech.avarLMS.response.SpecializationBulkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationBulkUploadService {

    private final SpecializationRepo specializationRepo;
    private final DepartmentRepo departmentRepo;
    private final SubTeamRepo subTeamRepo;
    private final OrganizationRepo organizationRepo;

    public SpecializationBulkUploadResponse uploadSpecializations(
            Long organizationId,
            MultipartFile file
    ) {
        int totalRows = 0;
        int insertedRows = 0;
        int failedRows = 0;

        List<SpecializationBulkUploadFailedRow> failedRecords = new ArrayList<>();

        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

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

                String name = getCellValue(row.getCell(0));
                String departmentCode = getCellValue(row.getCell(1));
                String subTeamName = getCellValue(row.getCell(2));
                String description = getCellValue(row.getCell(3));
                String activeValue = getCellValue(row.getCell(4));

                try {
                    if (name == null || name.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, departmentCode, "Specialization name is required."));
                        continue;
                    }

                    if (departmentCode == null || departmentCode.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, departmentCode, "Department code is required."));
                        continue;
                    }

                    Department department = departmentRepo
                            .findByCodeAndOrganizationId(departmentCode.trim(), organizationId)
                            .orElse(null);

                    if (department == null) {
                        failedRows++;
                        failedRecords.add(failed(
                                i,
                                name,
                                departmentCode,
                                "Department code '" + departmentCode + "' was not found."
                        ));
                        continue;
                    }

                    if (specializationRepo.existsByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
                            name.trim(),
                            department.getId(),
                            organizationId
                    )) {
                        failedRows++;
                        failedRecords.add(failed(
                                i,
                                name,
                                departmentCode,
                                "Specialization already exists in this department."
                        ));
                        continue;
                    }

                    SubTeam subTeam = null;

                    if (subTeamName != null && !subTeamName.isBlank()) {
                        subTeam = subTeamRepo
                                .findByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
                                        subTeamName.trim(),
                                        department.getId(),
                                        organizationId
                                )
                                .orElse(null);

                        if (subTeam == null) {
                            failedRows++;
                            failedRecords.add(failed(
                                    i,
                                    name,
                                    departmentCode,
                                    "Sub-Team '" + subTeamName + "' was not found in this department."
                            ));
                            continue;
                        }
                    }

                    Specialization specialization = new Specialization();
                    specialization.setOrganization(organization);
                    specialization.setName(name.trim());
                    specialization.setDepartment(department);
                    specialization.setSubTeam(subTeam);
                    specialization.setDescription(description);
                    specialization.setActive(parseActive(activeValue));

                    specializationRepo.save(specialization);
                    insertedRows++;

                } catch (Exception e) {
                    failedRows++;
                    failedRecords.add(failed(
                            i,
                            name,
                            departmentCode,
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Unexpected error while processing this row."
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload specializations file", e);
        }

        return new SpecializationBulkUploadResponse(
                totalRows,
                insertedRows,
                failedRows,
                failedRecords
        );
    }

    private SpecializationBulkUploadFailedRow failed(
            int rowIndex,
            String name,
            String departmentCode,
            String reason
    ) {
        return new SpecializationBulkUploadFailedRow(
                rowIndex + 1,
                name,
                departmentCode,
                reason
        );
    }

    private boolean parseActive(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String normalized = value.trim().toLowerCase();

        return normalized.equals("true")
                || normalized.equals("yes")
                || normalized.equals("active")
                || normalized.equals("1");
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;

        for (int i = 0; i <= 4; i++) {
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
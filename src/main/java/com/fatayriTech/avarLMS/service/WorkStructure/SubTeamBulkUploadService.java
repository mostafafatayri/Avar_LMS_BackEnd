package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.model.SubTeam;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.repository.SubTeamRepo;
import com.fatayriTech.avarLMS.response.SubTeamBulkUploadFailedRow;
import com.fatayriTech.avarLMS.response.SubTeamBulkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubTeamBulkUploadService {

    private final SubTeamRepo subTeamRepo;
    private final DepartmentRepo departmentRepo;
    private final EmployeeRepo employeeRepo;
    private final OrganizationRepo organizationRepo;

    public SubTeamBulkUploadResponse uploadSubTeams(Long organizationId, MultipartFile file) {
        int totalRows = 0;
        int insertedRows = 0;
        int failedRows = 0;

        List<SubTeamBulkUploadFailedRow> failedRecords = new ArrayList<>();

        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

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
                String departmentCode = getCellValue(row.getCell(1));
                String description = getCellValue(row.getCell(2));
                String leadEmployeeCode = getCellValue(row.getCell(3));
                String activeValue = getCellValue(row.getCell(4));

                try {
                    if (name == null || name.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, departmentCode, "Sub-Team name is required."));
                        continue;
                    }

                    if (departmentCode == null || departmentCode.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, departmentCode, "Department code is required."));
                        continue;
                    }

                    if (subTeamRepo.existsByNameIgnoreCaseAndOrganizationId(name, organizationId)) {
                        failedRows++;
                        failedRecords.add(failed(i, name, departmentCode, "Sub-Team name already exists in this organization."));
                        continue;
                    }

                    Department department = departmentRepo
                            .findByCodeAndOrganizationId(departmentCode, organizationId)
                            .orElse(null);

                    if (department == null) {
                        failedRows++;
                        failedRecords.add(failed(i, name, departmentCode, "Department code '" + departmentCode + "' was not found."));
                        continue;
                    }

                    Employee leadEmployee = null;

                    if (leadEmployeeCode != null && !leadEmployeeCode.isBlank()) {
                        leadEmployee = employeeRepo
                                .findByEmployeeIdAndOrganizationId(leadEmployeeCode, organizationId)
                                .orElse(null);

                        if (leadEmployee == null) {
                            failedRows++;
                            failedRecords.add(failed(i, name, departmentCode, "Lead Employee ID '" + leadEmployeeCode + "' was not found."));
                            continue;
                        }
                    }

                    SubTeam subTeam = SubTeam.builder()
                            .organization(organization)
                            .name(name.trim())
                            .department(department)
                            .leadEmployee(leadEmployee)
                            .description(description)
                            .active(parseActive(activeValue))
                            .build();

                    subTeamRepo.save(subTeam);
                    insertedRows++;

                } catch (Exception e) {
                    failedRows++;
                    failedRecords.add(failed(
                            i,
                            name,
                            departmentCode,
                            e.getMessage() != null ? e.getMessage() : "Unexpected error while processing row."
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload sub-teams file", e);
        }

        return new SubTeamBulkUploadResponse(
                totalRows,
                insertedRows,
                failedRows,
                failedRecords
        );
    }

    private SubTeamBulkUploadFailedRow failed(
            int rowIndex,
            String name,
            String departmentCode,
            String reason
    ) {
        return new SubTeamBulkUploadFailedRow(
                rowIndex + 1,
                name,
                departmentCode,
                reason
        );
    }

    private boolean parseActive(String value) {
        if (value == null || value.isBlank()) return true;

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
package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.EmployeeRole;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.model.SeniorityLevel;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.EmployeeRoleRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.repository.SeniorityLevelRepo;
import com.fatayriTech.avarLMS.response.EmployeeRoleBulkUploadFailedRow;
import com.fatayriTech.avarLMS.response.EmployeeRoleBulkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeRoleBulkUploadService {

    private final EmployeeRoleRepo employeeRoleRepo;
    private final DepartmentRepo departmentRepo;
    private final SeniorityLevelRepo seniorityLevelRepo;
    private final OrganizationRepo organizationRepo;

    public EmployeeRoleBulkUploadResponse uploadRoles(
            Long organizationId,
            MultipartFile file
    ) {
        int totalRows = 0;
        int insertedRows = 0;
        int failedRows = 0;

        List<EmployeeRoleBulkUploadFailedRow> failedRecords = new ArrayList<>();

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
                String seniorityLevelName = getCellValue(row.getCell(2));
                String description = getCellValue(row.getCell(3));
                String activeValue = getCellValue(row.getCell(4));

                try {
                    if (name == null || name.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, departmentCode, "Role name is required."));
                        continue;
                    }

                    if (departmentCode == null || departmentCode.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, departmentCode, "Department code is required."));
                        continue;
                    }

                    Department department = departmentRepo
                            .findByCodeAndOrganizationId(departmentCode, organizationId)
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

                    if (employeeRoleRepo.existsByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
                            name,
                            department.getId(),
                            organizationId
                    )) {
                        failedRows++;
                        failedRecords.add(failed(
                                i,
                                name,
                                departmentCode,
                                "Role already exists in this department."
                        ));
                        continue;
                    }

                    SeniorityLevel seniorityLevel = null;

                    if (seniorityLevelName != null && !seniorityLevelName.isBlank()) {
                        seniorityLevel = seniorityLevelRepo
                                .findByNameIgnoreCaseAndOrganizationId(
                                        seniorityLevelName,
                                        organizationId
                                )
                                .orElse(null);

                        if (seniorityLevel == null) {
                            failedRows++;
                            failedRecords.add(failed(
                                    i,
                                    name,
                                    departmentCode,
                                    "Seniority level '" + seniorityLevelName + "' was not found."
                            ));
                            continue;
                        }
                    }

                    EmployeeRole employeeRole = new EmployeeRole();
                    employeeRole.setOrganization(organization);
                    employeeRole.setName(name.trim());
                    employeeRole.setDepartment(department);
                    employeeRole.setSeniorityLevel(seniorityLevel);
                    employeeRole.setDescription(description);
                    employeeRole.setActive(parseActive(activeValue));

                    employeeRoleRepo.save(employeeRole);
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
            throw new RuntimeException("Failed to upload employee roles file", e);
        }

        return new EmployeeRoleBulkUploadResponse(
                totalRows,
                insertedRows,
                failedRows,
                failedRecords
        );
    }

    private EmployeeRoleBulkUploadFailedRow failed(
            int rowIndex,
            String name,
            String departmentCode,
            String reason
    ) {
        return new EmployeeRoleBulkUploadFailedRow(
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
        if (row == null) {
            return true;
        }

        for (int i = 0; i <= 4; i++) {
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
}
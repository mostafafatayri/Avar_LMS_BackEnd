package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.Location;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;

import com.fatayriTech.avarLMS.repository.LocationRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.response.Department.DepartmentBulkUploadFailedRow;
import com.fatayriTech.avarLMS.response.Department.DepartmentBulkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentBulkUploadService {

    private final DepartmentRepo departmentRepo;
    private final OrganizationRepo organizationRepo;
    private final EmployeeRepo employeeRepo;
    private final LocationRepo locationRepo;

    public DepartmentBulkUploadResponse uploadDepartments(
            Long organizationId,
            MultipartFile file
    ) {
        int totalRows = 0;
        int insertedRows = 0;
        int failedRows = 0;

        List<DepartmentBulkUploadFailedRow> failedRecords = new ArrayList<>();

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
                String code = getCellValue(row.getCell(1));
                String description = getCellValue(row.getCell(2));
                String headEmployeeCode = getCellValue(row.getCell(3));
                String locationIdValue = getCellValue(row.getCell(4));
                String activeValue = getCellValue(row.getCell(5));

                try {
                    if (name == null || name.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, code, "Department name is required."));
                        continue;
                    }

                    if (code == null || code.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, code, "Department code is required."));
                        continue;
                    }

                    if (departmentRepo.existsByCodeAndOrganizationId(code, organizationId)) {
                        failedRows++;
                        failedRecords.add(failed(i, name, code, "Department code already exists in this organization."));
                        continue;
                    }

                    if (departmentRepo.existsByNameAndOrganizationId(name, organizationId)) {
                        failedRows++;
                        failedRecords.add(failed(i, name, code, "Department name already exists in this organization."));
                        continue;
                    }

                    Employee head = null;
                    if (headEmployeeCode != null && !headEmployeeCode.isBlank()) {
                        head = employeeRepo
                                .findByEmployeeIdAndOrganizationId(headEmployeeCode, organizationId)
                                .orElse(null);

                        if (head == null) {
                            failedRows++;
                            failedRecords.add(failed(
                                    i,
                                    name,
                                    code,
                                    "Head Employee ID '" + headEmployeeCode + "' was not found in this organization."
                            ));
                            continue;
                        }
                    }

                    Location location = null;
                    if (locationIdValue != null && !locationIdValue.isBlank()) {
                        Long locationId;

                        try {
                            locationId = Long.valueOf(locationIdValue);
                        } catch (NumberFormatException ex) {
                            failedRows++;
                            failedRecords.add(failed(i, name, code, "Location ID must be a valid number."));
                            continue;
                        }

                        location = locationRepo
                                .findByIdAndOrganizationId(locationId, organizationId)
                                .orElse(null);

                        if (location == null) {
                            failedRows++;
                            failedRecords.add(failed(
                                    i,
                                    name,
                                    code,
                                    "Location ID '" + locationIdValue + "' was not found in this organization."
                            ));
                            continue;
                        }
                    }

                    Department department = new Department();
                    department.setOrganization(organization);
                    department.setName(name.trim());
                    department.setCode(code.trim());
                    department.setDescription(description);
                    department.setHead(head);
                    department.setLocation(location);
                    department.setActive(parseActive(activeValue));

                    departmentRepo.save(department);
                    insertedRows++;

                } catch (Exception e) {
                    failedRows++;
                    failedRecords.add(failed(
                            i,
                            name,
                            code,
                            e.getMessage() != null ? e.getMessage() : "Unexpected error while processing this row."
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload departments file", e);
        }

        return new DepartmentBulkUploadResponse(
                totalRows,
                insertedRows,
                failedRows,
                failedRecords
        );
    }

    private DepartmentBulkUploadFailedRow failed(
            int rowIndex,
            String name,
            String code,
            String reason
    ) {
        return new DepartmentBulkUploadFailedRow(
                rowIndex + 1,
                name,
                code,
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

        for (int i = 0; i <= 5; i++) {
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
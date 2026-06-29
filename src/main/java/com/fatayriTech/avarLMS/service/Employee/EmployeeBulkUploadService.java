package com.fatayriTech.avarLMS.service.Employee;

import com.fatayriTech.avarLMS.enums.AcademyStatus;
import com.fatayriTech.avarLMS.enums.EmployeeType;
import com.fatayriTech.avarLMS.enums.EmploymentStatus;
import com.fatayriTech.avarLMS.model.*;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.PositionRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.LocationRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.repository.SeniorityLevelRepo;
import com.fatayriTech.avarLMS.repository.SpecializationRepo;
import com.fatayriTech.avarLMS.repository.SubTeamRepo;
import com.fatayriTech.avarLMS.response.employee.EmployeeBulkUploadFailedRow;
import com.fatayriTech.avarLMS.response.employee.EmployeeBulkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeBulkUploadService {

    private final EmployeeRepo employeeRepo;
    private final OrganizationRepo organizationRepo;
    private final DepartmentRepo departmentRepo;
    private final SubTeamRepo subTeamRepo;
    private final SpecializationRepo specializationRepo;
    private final PositionRepo positionRepo;
    private final SeniorityLevelRepo seniorityLevelRepo;
    private final LocationRepo locationRepo;

    public EmployeeBulkUploadResponse uploadEmployees(Long organizationId, MultipartFile file) {
        int totalRows = 0;
        int insertedRows = 0;
        int failedRows = 0;

        List<EmployeeBulkUploadFailedRow> failedRecords = new ArrayList<>();

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

                String employeeId = getCellValue(row.getCell(0));
                String firstName = getCellValue(row.getCell(1));
                String middleName = getCellValue(row.getCell(2));
                String lastName = getCellValue(row.getCell(3));
                String email = getCellValue(row.getCell(4));
                String phoneNumber = getCellValue(row.getCell(5));
                String departmentCode = getCellValue(row.getCell(6));
                String subTeamName = getCellValue(row.getCell(7));
                String specializationName = getCellValue(row.getCell(8));
                String jobTitleCode = getCellValue(row.getCell(9));
                String seniorityLevelName = getCellValue(row.getCell(10));
                String locationCode = getCellValue(row.getCell(11));
                String managerEmployeeId = getCellValue(row.getCell(12));
                String employmentStatusValue = getCellValue(row.getCell(13));
                String academyStatusValue = getCellValue(row.getCell(14));
                String employeeTypeValue = getCellValue(row.getCell(15));

                try {
                    if (employeeId == null || employeeId.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, employeeId, email, "Employee ID is required."));
                        continue;
                    }

                    if (firstName == null || firstName.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, employeeId, email, "First name is required."));
                        continue;
                    }

                    if (lastName == null || lastName.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, employeeId, email, "Last name is required."));
                        continue;
                    }

                    if (email == null || email.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, employeeId, email, "Email is required."));
                        continue;
                    }

                    if (employeeRepo.existsByEmployeeIdAndOrganizationId(employeeId.trim(), organizationId)) {
                        failedRows++;
                        failedRecords.add(failed(i, employeeId, email, "Employee ID already exists."));
                        continue;
                    }

                    if (employeeRepo.existsByEmailAndOrganizationId(email.trim(), organizationId)) {
                        failedRows++;
                        failedRecords.add(failed(i, employeeId, email, "Email already exists."));
                        continue;
                    }

                    Department department = null;
                    if (departmentCode != null && !departmentCode.isBlank()) {
                        department = departmentRepo.findByCodeAndOrganizationId(
                                departmentCode.trim(),
                                organizationId
                        ).orElse(null);

                        if (department == null) {
                            failedRows++;
                            failedRecords.add(failed(i, employeeId, email, "Department code not found."));
                            continue;
                        }
                    }

                    SubTeam subTeam = null;
                    if (subTeamName != null && !subTeamName.isBlank()) {
                        if (department == null) {
                            failedRows++;
                            failedRecords.add(failed(i, employeeId, email, "Department code is required when Sub-Team is provided."));
                            continue;
                        }

                        subTeam = subTeamRepo.findByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
                                subTeamName.trim(),
                                department.getId(),
                                organizationId
                        ).orElse(null);

                        if (subTeam == null) {
                            failedRows++;
                            failedRecords.add(failed(i, employeeId, email, "Sub-Team not found in this department."));
                            continue;
                        }
                    }

                    Specialization specialization = null;
                    if (specializationName != null && !specializationName.isBlank()) {
                        if (department == null) {
                            failedRows++;
                            failedRecords.add(failed(i, employeeId, email, "Department code is required when Specialization is provided."));
                            continue;
                        }

                        specialization = specializationRepo.findByNameIgnoreCaseAndDepartmentIdAndOrganizationId(
                                specializationName.trim(),
                                department.getId(),
                                organizationId
                        ).orElse(null);

                        if (specialization == null) {
                            failedRows++;
                            failedRecords.add(failed(i, employeeId, email, "Specialization not found in this department."));
                            continue;
                        }
                    }

                    Position position = null;
                    if (jobTitleCode != null && !jobTitleCode.isBlank()) {
                        position = positionRepo.findByCodeAndOrganizationId(
                                jobTitleCode.trim(),
                                organizationId
                        ).orElse(null);

                        if (position == null) {
                            failedRows++;
                            failedRecords.add(failed(i, employeeId, email, "Job title code not found."));
                            continue;
                        }
                    }

                    SeniorityLevel seniorityLevel = null;
                    if (seniorityLevelName != null && !seniorityLevelName.isBlank()) {
                        seniorityLevel = seniorityLevelRepo.findByNameIgnoreCaseAndOrganizationId(
                                seniorityLevelName.trim(),
                                organizationId
                        ).orElse(null);

                        if (seniorityLevel == null) {
                            failedRows++;
                            failedRecords.add(failed(i, employeeId, email, "Seniority level not found."));
                            continue;
                        }
                    }

                    Location location = null;
                    if (locationCode != null && !locationCode.isBlank()) {
                        location = locationRepo.findByOrganizationIdAndCodeIgnoreCase(
                                organizationId,
                                locationCode.trim()
                        ).orElse(null);

                        if (location == null) {
                            failedRows++;
                            failedRecords.add(failed(i, employeeId, email, "Location code not found."));
                            continue;
                        }
                    }

                    Employee manager = null;
                    if (managerEmployeeId != null && !managerEmployeeId.isBlank()) {
                        manager = employeeRepo.findByEmployeeIdAndOrganizationId(
                                managerEmployeeId.trim(),
                                organizationId
                        ).orElse(null);

                        if (manager == null) {
                            failedRows++;
                            failedRecords.add(failed(i, employeeId, email, "Reporting manager employee ID not found."));
                            continue;
                        }
                    }

                    Employee employee = new Employee();
                    employee.setOrganization(organization);
                    employee.setEmployeeId(employeeId.trim());
                    employee.setFirstName(firstName.trim());
                    employee.setMiddleName(emptyToNull(middleName));
                    employee.setLastName(lastName.trim());
                    employee.setEmail(email.trim());
                    employee.setPhoneNumber(emptyToNull(phoneNumber));

                    employee.setDepartment(department);
                    employee.setSubTeam(subTeam);
                    employee.setSpecialization(specialization);
                    employee.setPosition(position);
                    employee.setSeniorityLevel(seniorityLevel);
                    employee.setLocation(location);
                    employee.setManager(manager);

                    EmploymentStatus employmentStatus = parseEmploymentStatus(employmentStatusValue);
                    employee.setEmploymentStatus(employmentStatus);
                    employee.setAcademyStatus(parseAcademyStatus(academyStatusValue));
                    employee.setEmployeeType(parseEmployeeType(employeeTypeValue));
                    employee.setActive(employmentStatus == EmploymentStatus.ACTIVE);

                    employeeRepo.save(employee);
                    insertedRows++;

                } catch (Exception e) {
                    failedRows++;
                    failedRecords.add(failed(
                            i,
                            employeeId,
                            email,
                            e.getMessage() != null ? e.getMessage() : "Unexpected error while processing row."
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload employees file", e);
        }

        return new EmployeeBulkUploadResponse(
                totalRows,
                insertedRows,
                failedRows,
                failedRecords
        );
    }

    private EmployeeBulkUploadFailedRow failed(
            int rowIndex,
            String employeeId,
            String email,
            String reason
    ) {
        return new EmployeeBulkUploadFailedRow(
                rowIndex + 1,
                employeeId,
                email,
                reason
        );
    }

    private EmploymentStatus parseEmploymentStatus(String value) {
        if (value == null || value.isBlank()) return EmploymentStatus.ACTIVE;

        return switch (normalize(value)) {
            case "ACTIVE" -> EmploymentStatus.ACTIVE;
            case "INACTIVE" -> EmploymentStatus.INACTIVE;
            case "TRANSFERRED" -> EmploymentStatus.TRANSFERRED;
            case "RESIGNED" -> EmploymentStatus.RESIGNED;
            case "SUSPENDED" -> EmploymentStatus.SUSPENDED;
            default -> throw new RuntimeException("Invalid employment status: " + value);
        };
    }

    private AcademyStatus parseAcademyStatus(String value) {
        if (value == null || value.isBlank()) return AcademyStatus.NOT_APPLICABLE;

        return switch (normalize(value)) {
            case "NOT_APPLICABLE" -> AcademyStatus.NOT_APPLICABLE;
            case "IN_ACADEMY" -> AcademyStatus.IN_ACADEMY;
            case "COMPLETED_DEPLOYED" -> AcademyStatus.COMPLETED_DEPLOYED;
            default -> throw new RuntimeException("Invalid academy status: " + value);
        };
    }

    private EmployeeType parseEmployeeType(String value) {
        if (value == null || value.isBlank()) return EmployeeType.EXISTING_EMPLOYEE;

        return switch (normalize(value)) {
            case "EXISTING_EMPLOYEE" -> EmployeeType.EXISTING_EMPLOYEE;
            case "FRESH_GRADUATE" -> EmployeeType.FRESH_GRADUATE;
            case "NEW_HIRE" -> EmployeeType.NEW_HIRE;
            default -> throw new RuntimeException("Invalid employee type: " + value);
        };
    }

    private String normalize(String value) {
        return value.trim().toUpperCase().replace(" ", "_").replace("-", "_").replace("/", "_");
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;

        for (int i = 0; i <= 15; i++) {
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
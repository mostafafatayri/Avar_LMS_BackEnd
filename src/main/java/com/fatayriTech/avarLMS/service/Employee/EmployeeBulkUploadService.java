package com.fatayriTech.avarLMS.service.Employee;

import com.fatayriTech.avarLMS.exceptions.ResourceNotFoundException;
import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.Position;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.PositionRepo ;
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
    private final DepartmentRepo departmentRepo;
    private final PositionRepo positionRepo;

    public EmployeeBulkUploadResponse uploadEmployees(MultipartFile file) {
        int totalRows = 0;
        int inserted = 0;
        int skipped = 0;

        List<String> errors = new ArrayList<>();

        try (
                InputStream inputStream = file.getInputStream();
                Workbook workbook = WorkbookFactory.create(inputStream)
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                int excelRowNumber = i + 1;
                totalRows++;

                try {
                    Row row = sheet.getRow(i);

                    if (row == null) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": Empty row.");
                        continue;
                    }

                    String employeeId = getCellValue(row.getCell(0));
                    String firstName = getCellValue(row.getCell(1));
                    String middleName = getCellValue(row.getCell(2));
                    String lastName = getCellValue(row.getCell(3));
                    String email = getCellValue(row.getCell(4));
                    String phoneNumber = getCellValue(row.getCell(5));
                    String departmentCode = getCellValue(row.getCell(6));
                    String positionCode = getCellValue(row.getCell(7));
                    String managerEmployeeId = getCellValue(row.getCell(8));

                    if (employeeId == null || employeeId.isBlank()) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": Employee ID is required.");
                        continue;
                    }

                    if (firstName == null || firstName.isBlank()) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": First Name is required.");
                        continue;
                    }

                    if (lastName == null || lastName.isBlank()) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": Last Name is required.");
                        continue;
                    }

                    if (email == null || email.isBlank()) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": Email is required.");
                        continue;
                    }

                    if (departmentCode == null || departmentCode.isBlank()) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": Department Code is required.");
                        continue;
                    }

                    if (positionCode == null || positionCode.isBlank()) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": Position Code is required.");
                        continue;
                    }

                    if (employeeRepo.existsByEmployeeId(employeeId)) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": Employee ID already exists: " + employeeId);
                        continue;
                    }

                    if (employeeRepo.existsByEmail(email)) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": Email already exists: " + email);
                        continue;
                    }

                    Department department = departmentRepo.findByCode(departmentCode)
                            .orElse(null);

                    if (department == null) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": Department code not found: " + departmentCode);
                        continue;
                    }

                    Position position = positionRepo.findByCode(positionCode)
                            .orElse(null);

                    if (position == null) {
                        skipped++;
                        errors.add("Row " + excelRowNumber + ": Position code not found: " + positionCode);
                        continue;
                    }

                    Employee manager = null;

                    if (managerEmployeeId != null && !managerEmployeeId.isBlank()) {
                        manager = employeeRepo.findByEmployeeId(managerEmployeeId)
                                .orElse(null);

                        if (manager == null) {
                            skipped++;
                            errors.add("Row " + excelRowNumber + ": Manager Employee ID not found: " + managerEmployeeId);
                            continue;
                        }
                    }

                    Employee employee = new Employee();
                    employee.setEmployeeId(employeeId);
                    employee.setFirstName(firstName);
                    employee.setMiddleName(middleName);
                    employee.setLastName(lastName);
                    employee.setEmail(email);
                    employee.setPhoneNumber(phoneNumber);
                    employee.setDepartment(department);
                    employee.setPosition(position);
                    employee.setManager(manager);
                    employee.setActive(true);

                    employeeRepo.save(employee);
                    inserted++;

                } catch (Exception e) {
                    skipped++;
                    errors.add("Row " + excelRowNumber + ": Unexpected error - " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload employees file: " + e.getMessage(), e);
        }

        return new EmployeeBulkUploadResponse(
                totalRows,
                inserted,
                skipped,
                errors
        );
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
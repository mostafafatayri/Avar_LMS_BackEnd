package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;  ;
import com.fatayriTech.avarLMS.response.Department.DepartmentBulkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class DepartmentBulkUploadService {

    private final DepartmentRepo departmentRepo;

    public DepartmentBulkUploadResponse uploadDepartments(MultipartFile file) {
        int totalRows = 0;
        int insertedRows = 0;
        int failedRows = 0;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                totalRows++;

                try {
                    Row row = sheet.getRow(i);

                    if (row == null) {
                        failedRows++;
                        continue;
                    }

                    String name = getCellValue(row.getCell(0));
                    String code = getCellValue(row.getCell(1));
                    String description = getCellValue(row.getCell(2));
                    String costCenter = getCellValue(row.getCell(3));

                    if (name == null || name.isBlank() || code == null || code.isBlank()) {
                        failedRows++;
                        continue;
                    }

                    Department department = new Department();
                    department.setName(name);
                    department.setCode(code);
                    department.setDescription(description);
                   // department.setCostCenter(costCenter);
                    department.setActive(true);

                    departmentRepo.save(department);
                    insertedRows++;

                } catch (Exception e) {
                    failedRows++;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload departments file");
        }

        return new DepartmentBulkUploadResponse(
                totalRows,
                insertedRows,
                failedRows
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
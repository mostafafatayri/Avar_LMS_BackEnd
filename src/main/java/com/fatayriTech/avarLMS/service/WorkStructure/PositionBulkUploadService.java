package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Department;
import com.fatayriTech.avarLMS.model.Position;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.PositionRepo;
import com.fatayriTech.avarLMS.response.position.PositionBulkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class PositionBulkUploadService {

    private final PositionRepo positionRepo;
    private final DepartmentRepo departmentRepo;

    public PositionBulkUploadResponse uploadPositions(MultipartFile file) {

        int totalRows = 0;
        int insertedRows = 0;
        int failedRows = 0;

        try (
                InputStream inputStream = file.getInputStream();
                Workbook workbook = WorkbookFactory.create(inputStream)
        ) {

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
                    String departmentCode = getCellValue(row.getCell(3));

                    if (
                            name == null || name.isBlank() ||
                                    code == null || code.isBlank() ||
                                    departmentCode == null || departmentCode.isBlank()
                    ) {
                        failedRows++;
                        continue;
                    }

                    Department department = departmentRepo
                            .findByCode(departmentCode)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Department not found: " + departmentCode
                                    )
                            );

                    Position position = new Position();

                    position.setName(name);
                    position.setCode(code);
                    position.setDescription(description);
                    position.setDepartment(department);
                    position.setActive(true);

                    positionRepo.save(position);

                    insertedRows++;

                } catch (Exception ex) {
                    failedRows++;
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to upload positions file",
                    ex
            );
        }

        return new PositionBulkUploadResponse(
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

        return formatter
                .formatCellValue(cell)
                .trim();
    }
}
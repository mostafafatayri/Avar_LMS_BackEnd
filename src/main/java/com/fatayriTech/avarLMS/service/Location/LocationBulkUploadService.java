package com.fatayriTech.avarLMS.service.Location;

import com.fatayriTech.avarLMS.model.Location;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.repository.LocationRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.response.location.LocationBulkUploadFailedRow;
import com.fatayriTech.avarLMS.response.location.LocationBulkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationBulkUploadService {

    private final LocationRepo locationRepo;
    private final OrganizationRepo organizationRepo;

    public LocationBulkUploadResponse uploadLocations(
            Long organizationId,
            MultipartFile file
    ) {
        int totalRows = 0;
        int insertedRows = 0;
        int failedRows = 0;

        List<LocationBulkUploadFailedRow> failedRecords = new ArrayList<>();

        Organization organization = organizationRepo.findByIdAndActiveTrue(organizationId)
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
                String region = getCellValue(row.getCell(3));
                String country = getCellValue(row.getCell(4));
                String city = getCellValue(row.getCell(5));
                String address = getCellValue(row.getCell(6));
                String latitudeValue = getCellValue(row.getCell(7));
                String longitudeValue = getCellValue(row.getCell(8));
                String activeValue = getCellValue(row.getCell(9));

                try {
                    if (name == null || name.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, code, "Location name is required."));
                        continue;
                    }

                    if (country == null || country.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, code, "Country is required."));
                        continue;
                    }

                    if (city == null || city.isBlank()) {
                        failedRows++;
                        failedRecords.add(failed(i, name, code, "City is required."));
                        continue;
                    }

                    if (locationRepo.existsByOrganizationIdAndNameIgnoreCase(
                            organizationId,
                            name.trim()
                    )) {
                        failedRows++;
                        failedRecords.add(failed(
                                i,
                                name,
                                code,
                                "Location name already exists."
                        ));
                        continue;
                    }

                    if (code != null && !code.isBlank()) {
                        if (locationRepo.existsByOrganizationIdAndCodeIgnoreCase(
                                organizationId,
                                code.trim()
                        )) {
                            failedRows++;
                            failedRecords.add(failed(
                                    i,
                                    name,
                                    code,
                                    "Location code already exists."
                            ));
                            continue;
                        }
                    }

                    Location location = new Location();
                    location.setOrganization(organization);
                    location.setName(name.trim());
                    location.setCode(code == null || code.isBlank() ? null : code.trim());
                    location.setDescription(
                            description == null || description.isBlank()
                                    ? null
                                    : description.trim()
                    );
                    location.setRegion(region == null || region.isBlank() ? null : region.trim());
                    location.setCountry(country.trim());
                    location.setCity(city.trim());
                    location.setAddress(address == null || address.isBlank() ? null : address.trim());
                    location.setLatitude(parseDouble(latitudeValue));
                    location.setLongitude(parseDouble(longitudeValue));
                    location.setActive(parseActive(activeValue));

                    locationRepo.save(location);
                    insertedRows++;

                } catch (Exception e) {
                    failedRows++;
                    failedRecords.add(failed(
                            i,
                            name,
                            code,
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Unexpected error while processing this row."
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload locations file", e);
        }

        return new LocationBulkUploadResponse(
                totalRows,
                insertedRows,
                failedRows,
                failedRecords
        );
    }

    private LocationBulkUploadFailedRow failed(
            int rowIndex,
            String name,
            String code,
            String reason
    ) {
        return new LocationBulkUploadFailedRow(
                rowIndex + 1,
                name,
                code,
                reason
        );
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            throw new RuntimeException("Latitude/Longitude must be a valid number.");
        }
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

        for (int i = 0; i <= 9; i++) {
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
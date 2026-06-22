package com.fatayriTech.avarLMS.service.Location;

import com.fatayriTech.avarLMS.model.Location;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.LocationRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.request.location.LocationRequest;
import com.fatayriTech.avarLMS.response.location.LocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepo locationRepo;
    private final OrganizationRepo organizationRepo;
    private final EmployeeRepo employeeRepo;

    public List<LocationResponse> getAll(Long organizationId) {
        return locationRepo
                .findByOrganizationIdOrderByCreationDateDesc(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public LocationResponse getById(Long organizationId, Long locationId) {
        Location location = findLocation(organizationId, locationId);
        return mapToResponse(location);
    }

    public LocationResponse create(Long organizationId, LocationRequest request) {
        validateRequest(request);

        Organization organization = organizationRepo.findByIdAndActiveTrue(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        if (locationRepo.existsByOrganizationIdAndNameIgnoreCase(
                organizationId,
                request.getName().trim()
        )) {
            throw new RuntimeException("Location name already exists.");
        }

        if (request.getCode() != null && !request.getCode().isBlank()) {
            if (locationRepo.existsByOrganizationIdAndCodeIgnoreCase(
                    organizationId,
                    request.getCode().trim()
            )) {
                throw new RuntimeException("Location code already exists.");
            }
        }

        Location location = new Location();
        location.setOrganization(organization);

        applyRequest(location, request);

        return mapToResponse(locationRepo.save(location));
    }

    public LocationResponse update(
            Long organizationId,
            Long locationId,
            LocationRequest request
    ) {
        validateRequest(request);

        Location location = findLocation(organizationId, locationId);

        applyRequest(location, request);

        return mapToResponse(locationRepo.save(location));
    }

    public void delete(Long organizationId, Long locationId) {
        Location location = findLocation(organizationId, locationId);

        long employeeCount = employeeRepo.countByOrganizationIdAndLocationIdAndActiveTrue(
                organizationId,
                locationId
        );

        if (employeeCount > 0) {
            throw new RuntimeException(
                    "Cannot delete location because employees are assigned to it."
            );
        }

        location.setActive(false);
        locationRepo.save(location);
    }

    private Location findLocation(Long organizationId, Long locationId) {
        return locationRepo.findByIdAndOrganizationId(locationId, organizationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
    }

    private void validateRequest(LocationRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Location name is required.");
        }

        if (request.getCountry() == null || request.getCountry().isBlank()) {
            throw new RuntimeException("Country is required.");
        }

        if (request.getCity() == null || request.getCity().isBlank()) {
            throw new RuntimeException("City is required.");
        }
    }

    private void applyRequest(Location location, LocationRequest request) {
        location.setName(request.getName().trim());
        location.setCode(
                request.getCode() == null ? null : request.getCode().trim()
        );
        location.setRegion(
                request.getRegion() == null ? null : request.getRegion().trim()
        );
        location.setCountry(request.getCountry().trim());
        location.setCity(request.getCity().trim());
        location.setAddress(
                request.getAddress() == null ? null : request.getAddress().trim()
        );
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setActive(request.getActive() == null || request.getActive());
    }

    private LocationResponse mapToResponse(Location location) {
        Long organizationId = location.getOrganization().getId();

        long employeeCount = employeeRepo.countByOrganizationIdAndLocationIdAndActiveTrue(
                organizationId,
                location.getId()
        );

        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .code(location.getCode())
                .region(location.getRegion())
                .country(location.getCountry())
                .city(location.getCity())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .active(location.getActive())
                .organizationId(organizationId)
                .employeeCount(employeeCount)
                .creationDate(location.getCreationDate())
                .modificationDate(location.getModificationDate())
                .build();
    }
}
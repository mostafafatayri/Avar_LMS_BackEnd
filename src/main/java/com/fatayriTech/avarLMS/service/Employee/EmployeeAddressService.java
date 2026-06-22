package com.fatayriTech.avarLMS.service.Employee;

import com.fatayriTech.avarLMS.enums.AddressType;
import com.fatayriTech.avarLMS.exceptions.ResourceNotFoundException;
import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.EmployeeAddress;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeAddressRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.request.Employees.EmployeeAddressRequest;
import com.fatayriTech.avarLMS.response.employee.EmployeeAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EmployeeAddressService {

    private final EmployeeAddressRepo addressRepo;
    private final EmployeeRepo employeeRepo;

    public List<EmployeeAddressResponse> getEmployeeAddresses(
            Long organizationId,
            Long employeeId
    ) {
        validateEmployee(organizationId, employeeId);

        return addressRepo
                .findByOrganizationIdAndEmployeeIdAndActiveTrueOrderByPrimaryAddressDescCreationDateDesc(
                        organizationId,
                        employeeId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeAddressResponse createAddress(
            Long organizationId,
            Long employeeId,
            EmployeeAddressRequest request
    ) {
        Employee employee = validateEmployee(organizationId, employeeId);

        EmployeeAddress address = new EmployeeAddress();
        address.setOrganizationId(organizationId);
        address.setEmployee(employee);

        fillAddress(address, request);

        if (Boolean.TRUE.equals(address.getPrimaryAddress())) {
            clearCurrentPrimaryAddress(organizationId, employeeId);
        }

        return mapToResponse(addressRepo.save(address));
    }

    public EmployeeAddressResponse updateAddress(
            Long organizationId,
            Long employeeId,
            Long addressId,
            EmployeeAddressRequest request
    ) {
        validateEmployee(organizationId, employeeId);

        EmployeeAddress address = addressRepo
                .findByIdAndOrganizationIdAndEmployeeIdAndActiveTrue(
                        addressId,
                        organizationId,
                        employeeId
                )
                .orElseThrow(() -> new ResourceNotFoundException("Employee address not found"));

        fillAddress(address, request);

        if (Boolean.TRUE.equals(address.getPrimaryAddress())) {
            clearCurrentPrimaryAddress(organizationId, employeeId, address.getId());
        }

        return mapToResponse(addressRepo.save(address));
    }

    public void deleteAddress(
            Long organizationId,
            Long employeeId,
            Long addressId
    ) {
        validateEmployee(organizationId, employeeId);

        EmployeeAddress address = addressRepo
                .findByIdAndOrganizationIdAndEmployeeIdAndActiveTrue(
                        addressId,
                        organizationId,
                        employeeId
                )
                .orElseThrow(() -> new ResourceNotFoundException("Employee address not found"));

        address.setActive(false);
        addressRepo.save(address);
    }

    private Employee validateEmployee(Long organizationId, Long employeeId) {
        return employeeRepo
                .findByIdAndOrganizationId(employeeId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    private void fillAddress(EmployeeAddress address, EmployeeAddressRequest request) {
        address.setAddressType(
                request.getAddressType() != null ? request.getAddressType() : AddressType.HOME
        );

        address.setCountry(request.getCountry());
        address.setState(request.getState());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setStreet(request.getStreet());
        address.setBuilding(request.getBuilding());
        address.setFloor(request.getFloor());
        address.setApartment(request.getApartment());
        address.setPostalCode(request.getPostalCode());
        address.setLandmark(request.getLandmark());

        address.setPrimaryAddress(Boolean.TRUE.equals(request.getPrimaryAddress()));
    }

    private void clearCurrentPrimaryAddress(Long organizationId, Long employeeId) {
        addressRepo
                .findByOrganizationIdAndEmployeeIdAndPrimaryAddressTrueAndActiveTrue(
                        organizationId,
                        employeeId
                )
                .ifPresent(existing -> {
                    existing.setPrimaryAddress(false);
                    addressRepo.save(existing);
                });
    }

    private void clearCurrentPrimaryAddress(
            Long organizationId,
            Long employeeId,
            Long excludedAddressId
    ) {
        addressRepo
                .findByOrganizationIdAndEmployeeIdAndPrimaryAddressTrueAndActiveTrue(
                        organizationId,
                        employeeId
                )
                .ifPresent(existing -> {
                    if (!existing.getId().equals(excludedAddressId)) {
                        existing.setPrimaryAddress(false);
                        addressRepo.save(existing);
                    }
                });
    }

    private EmployeeAddressResponse mapToResponse(EmployeeAddress address) {
        return EmployeeAddressResponse.builder()
                .id(address.getId())
                .organizationId(address.getOrganizationId())
                .employeeId(address.getEmployee().getId())
                .addressType(address.getAddressType())
                .country(address.getCountry())
                .state(address.getState())
                .city(address.getCity())
                .district(address.getDistrict())
                .street(address.getStreet())
                .building(address.getBuilding())
                .floor(address.getFloor())
                .apartment(address.getApartment())
                .postalCode(address.getPostalCode())
                .landmark(address.getLandmark())
                .primaryAddress(address.getPrimaryAddress())
                .active(address.getActive())
                .fullAddress(buildFullAddress(address))
                .creationDate(address.getCreationDate())
                .modificationDate(address.getModificationDate())
                .build();
    }

    private String buildFullAddress(EmployeeAddress address) {
        return Stream.of(
                        address.getBuilding(),
                        address.getFloor() != null ? "Floor " + address.getFloor() : null,
                        address.getApartment() != null ? "Apt " + address.getApartment() : null,
                        address.getStreet(),
                        address.getDistrict(),
                        address.getCity(),
                        address.getState(),
                        address.getCountry(),
                        address.getPostalCode()
                )
                .filter(value -> value != null && !value.isBlank())
                .reduce((a, b) -> a + ", " + b)
                .orElse("-");
    }
}
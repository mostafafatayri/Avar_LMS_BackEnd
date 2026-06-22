package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.Employees.EmployeeAddressRequest;
import com.fatayriTech.avarLMS.response.employee.EmployeeAddressResponse;
import com.fatayriTech.avarLMS.service.Employee.EmployeeAddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/employees/{employeeId}/addresses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EmployeeAddressController {

    private final EmployeeAddressService addressService;

    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    @GetMapping
    public List<EmployeeAddressResponse> getEmployeeAddresses(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long employeeId
    ) {
        return addressService.getEmployeeAddresses(organizationId, employeeId);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PostMapping
    public EmployeeAddressResponse createAddress(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long employeeId,
            @RequestBody EmployeeAddressRequest request
    ) {
        return addressService.createAddress(organizationId, employeeId, request);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PutMapping("/{addressId}")
    public EmployeeAddressResponse updateAddress(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long employeeId,
            @PathVariable Long addressId,
            @RequestBody EmployeeAddressRequest request
    ) {
        return addressService.updateAddress(
                organizationId,
                employeeId,
                addressId,
                request
        );
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @DeleteMapping("/{addressId}")
    public void deleteAddress(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long employeeId,
            @PathVariable Long addressId
    ) {
        addressService.deleteAddress(
                organizationId,
                employeeId,
                addressId
        );
    }
}
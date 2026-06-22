package com.fatayriTech.avarLMS.response.employee;

import com.fatayriTech.avarLMS.enums.AddressType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EmployeeAddressResponse {
    private Long id;
    private Long organizationId;
    private Long employeeId;

    private AddressType addressType;

    private String country;
    private String state;
    private String city;
    private String district;
    private String street;
    private String building;
    private String floor;
    private String apartment;
    private String postalCode;
    private String landmark;

    private Boolean primaryAddress;
    private Boolean active;

    private String fullAddress;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
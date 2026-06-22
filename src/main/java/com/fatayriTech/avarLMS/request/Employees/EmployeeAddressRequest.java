package com.fatayriTech.avarLMS.request.Employees;

import com.fatayriTech.avarLMS.enums.AddressType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeAddressRequest {
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
}
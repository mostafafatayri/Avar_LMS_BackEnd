package com.fatayriTech.avarLMS.request.location;

import lombok.Data;

@Data
public class LocationRequest {

    private String name;

    private String code;

    private String description;

    private String region;

    private String country;

    private String city;

    private String address;

    private Double latitude;

    private Double longitude;

    private Boolean active;
}
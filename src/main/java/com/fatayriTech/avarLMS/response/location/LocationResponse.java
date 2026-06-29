package com.fatayriTech.avarLMS.response.location;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LocationResponse {

    private Long id;

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

    private Long organizationId;

    private Long employeeCount;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;
}
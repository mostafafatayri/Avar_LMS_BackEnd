package com.fatayriTech.avarLMS.model;

import com.fatayriTech.avarLMS.enums.AddressType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_addresses")
@Getter
@Setter
public class EmployeeAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddressType addressType = AddressType.HOME;

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

    @Column(nullable = false)
    private Boolean primaryAddress = false;

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }

    public String getFullAddress() {
        return java.util.stream.Stream.of(
                        building,
                        floor != null && !floor.isBlank() ? "Floor " + floor : null,
                        apartment != null && !apartment.isBlank() ? "Apt " + apartment : null,
                        street,
                        district,
                        city,
                        state,
                        country,
                        postalCode
                )
                .filter(value -> value != null && !value.isBlank())
                .reduce((a, b) -> a + ", " + b)
                .orElse("-");
    }
}
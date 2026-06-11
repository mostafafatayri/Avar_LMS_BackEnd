package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Example: WORK_ORDER_CREATE
    @Column(nullable = false, unique = true)
    private String name;

    // Example: Allows user to create work orders
    private String description;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }
}

/**
 WORK_ORDER_VIEW
 WORK_ORDER_CREATE
 WORK_ORDER_UPDATE
 WORK_ORDER_DELETE
 TERRITORY_VIEW
 TERRITORY_CREATE
 TERRITORY_UPDATE
 TERRITORY_DELETE
 USER_MANAGE
 */
package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "domains")
@Getter
@Setter
public class Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String domain; // example: fatayritech.com

    @Column(nullable = false)
    private boolean allowed = true;

/// here
    private LocalDateTime creationDate;
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
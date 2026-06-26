package com.fatayriTech.avarLMS.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "nationalities")
@Getter
@Setter
public class Nationality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 10)
    private String code;

    private boolean active = true;

    //here
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
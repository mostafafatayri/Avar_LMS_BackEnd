package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "specializations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_specialization_name_department",
                        columnNames = {"name", "department_id"}
                )
        }
)
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Example: Safety Training, Compliance, LMS Administration
    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_team_id")
    private SubTeam subTeam;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean active = true;

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
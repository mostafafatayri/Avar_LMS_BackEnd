package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "employee_roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_role_name_department_org",
                        columnNames = {
                                "name",
                                "department_id",
                                "organization_id"
                        }
                )
        }
)
public class EmployeeRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // Example: Instructor, Training Manager, Reviewer
    @Column(nullable = false, length = 150)
    private String name;

    // Example: Junior, Mid, Senior, Lead, Manager
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seniority_level_id")
    private SeniorityLevel seniorityLevel;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

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
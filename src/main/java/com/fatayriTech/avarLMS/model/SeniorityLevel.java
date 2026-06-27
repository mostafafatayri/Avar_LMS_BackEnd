package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "seniority_levels",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seniority_level_name_org",
                        columnNames = {"name", "organization_id"}
                ),
                @UniqueConstraint(
                        name = "uk_seniority_level_order_org",
                        columnNames = {"display_order", "organization_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeniorityLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

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
}
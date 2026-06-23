package com.fatayriTech.avarLMS.model;

import com.fatayriTech.avarLMS.enums.TrainingDisplayItemType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_display_module_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingDisplayModuleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private TrainingDisplayModule module;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingDisplayItemType itemType;

    @Column(nullable = false)
    private Long itemRefId;

    @Column(nullable = false)
    private Integer displayOrder;

    private Boolean required = true;

    private Boolean active = true;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (active == null) active = true;
        if (required == null) required = true;
        if (displayOrder == null) displayOrder = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
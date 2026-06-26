package com.fatayriTech.avarLMS.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_organizations",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "organization_id"}
                )
        }
)
@Getter
@Setter
public class UserOrganization  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;



    private Boolean defaultOrganization = false;

    private Boolean active = true;

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
package com.fatayriTech.avarLMS.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_organizations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "organization_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean active = true;

    private Boolean defaultOrganization = false;
}
package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "database_name", unique = true)
    private String databaseName;

    private boolean active = true;

    //@Column(nullable = false)
    private String code;

    //@Column(nullable = false)
    private String LogoUrl;

    @Column(nullable = false)
    private String slug;

    private String color;
}
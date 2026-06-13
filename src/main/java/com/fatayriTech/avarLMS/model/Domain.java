package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
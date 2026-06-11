package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "user_security_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSecurityRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // master user id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private SecurityRole role;
}
package com.fatayriTech.avarLMS.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String middleName;
    private String lastName;

    // 0 is no , 1 is true
    private boolean confirmed;
    private boolean twoFactorEnabled;

    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserOrganization> organizations = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id")
    private Domain domain;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_security_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<SecurityRole> roles = new HashSet<>();



}

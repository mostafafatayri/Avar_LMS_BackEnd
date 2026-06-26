package com.fatayriTech.avarLMS.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_queue")
@Getter
@Setter
public class EmailQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String toEmail;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    private int status = 0; // 0 = pending, 1 = sent, 2 = poisoned
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Integer retryCount = 0;

    private LocalDateTime lastAttemptAt;
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



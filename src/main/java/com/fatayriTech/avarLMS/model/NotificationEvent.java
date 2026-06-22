package com.fatayriTech.avarLMS.model;

import com.fatayriTech.avarLMS.enums.NotificationChannel;
import com.fatayriTech.avarLMS.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notification_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_event_key_channel",
                        columnNames = {"event_key", "channel"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private NotificationRule rule;

    @Column(nullable = false)
    private String targetType;

    @Column(nullable = false)
    private Long targetId;

    private Long recipientUserId;

    private Long recipientEmployeeId;

    private String recipientEmail;

    @Column(nullable = false)
    private String eventKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    private LocalDateTime sentAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        modificationDate = LocalDateTime.now();

        if (status == null) {
            status = NotificationStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
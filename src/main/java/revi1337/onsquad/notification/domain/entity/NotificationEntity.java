package revi1337.onsquad.notification.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "notification", indexes = {@Index(name = "idx_notification_receiver_id", columnList = "receiverId, id")})
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    private Long publisherId;

    @Enumerated(STRING)
    private NotificationTopic topic;

    @Enumerated(STRING)
    private NotificationDetail detail;

    private String json;

    @Column(name = "is_read")
    private boolean read;

    private LocalDateTime occurredAt;

    @Builder
    private NotificationEntity(Long receiverId, Long publisherId, NotificationTopic topic, NotificationDetail detail, String json, boolean read,
                               LocalDateTime occurredAt) {
        Objects.requireNonNull(receiverId, "receiverId cannot be null");
        Objects.requireNonNull(publisherId, "publisherId cannot be null");
        this.receiverId = receiverId;
        this.publisherId = publisherId;
        this.topic = topic;
        this.detail = detail;
        this.json = json;
        this.read = read;
        this.occurredAt = occurredAt == null ? LocalDateTime.now() : occurredAt;
    }
}

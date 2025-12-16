package revi1337.onsquad.notification.domain;

import java.util.Objects;
import lombok.Getter;

@Getter
public abstract class AbstractNotification implements Notification {

    private final Long publisherId;
    private final Long receiverId;
    private final NotificationTopic topic;
    private final NotificationDetail detail;

    protected AbstractNotification(Long publisherId, Long receiverId, NotificationTopic topic, NotificationDetail detail) {
        this.publisherId = publisherId;
        this.receiverId = receiverId;
        this.topic = topic;
        this.detail = detail;
    }

    protected AbstractNotification(NotificationTopic topic, NotificationDetail detail) {
        this.publisherId = null;
        this.receiverId = null;
        this.topic = topic;
        this.detail = detail;
    }

    @Override
    public boolean isSelfNotification() {
        return Objects.equals(publisherId, receiverId);
    }

    @Override
    public boolean shouldSend() {
        return !isSelfNotification();
    }
}

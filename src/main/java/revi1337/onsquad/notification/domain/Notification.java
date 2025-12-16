package revi1337.onsquad.notification.domain;

import java.util.Objects;

public interface Notification {

    Long getPublisherId();

    Long getReceiverId();

    NotificationTopic getTopic();

    NotificationDetail getDetail();

    Object getPayload();

    default boolean isSelfNotification() {
        return Objects.equals(getPublisherId(), getReceiverId());
    }

    default boolean shouldSend() {
        return !isSelfNotification();
    }
}

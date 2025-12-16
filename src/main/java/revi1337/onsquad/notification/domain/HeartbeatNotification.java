package revi1337.onsquad.notification.domain;

import lombok.Getter;

@Getter
public class HeartbeatNotification extends AbstractNotification {

    public HeartbeatNotification() {
        super(NotificationTopic.USER, NotificationDetail.HEARTBEAT);
    }

    @Override
    public Object getPayload() {
        return null;
    }
}

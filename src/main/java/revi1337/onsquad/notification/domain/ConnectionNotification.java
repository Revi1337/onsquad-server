package revi1337.onsquad.notification.domain;

import lombok.Getter;

@Getter
public class ConnectionNotification extends AbstractNotification {

    public ConnectionNotification() {
        super(NotificationTopic.USER, NotificationDetail.CONNECT);
    }
    
    @Override
    public Object getPayload() {
        return null;
    }
}

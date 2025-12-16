package revi1337.onsquad.notification.infrastructure;

import java.util.List;

public class NotificationMessages {

    private final List<NotificationMessage> notifications;

    public NotificationMessages(List<NotificationMessage> notifications) {
        this.notifications = List.copyOf(notifications);
    }

    public List<NotificationMessage> getMessages() {
        return notifications;
    }
}

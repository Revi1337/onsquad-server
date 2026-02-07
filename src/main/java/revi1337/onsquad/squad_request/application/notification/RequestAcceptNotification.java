package revi1337.onsquad.squad_request.application.notification;

import lombok.Getter;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_request.application.notification.RequestContext.RequestAcceptedContext;

@Getter
public class RequestAcceptNotification extends AbstractNotification {

    private final RequestAcceptPayload payload;

    public RequestAcceptNotification(RequestAcceptedContext context) {
        super(context.accepterId(), context.requesterId(), NotificationTopic.USER, NotificationDetail.SQUAD_ACCEPT);
        this.payload = new RequestAcceptPayload(
                context.crewId(),
                context.crewName(),
                context.squadId(),
                context.squadTitle(),
                NotificationDetail.SQUAD_ACCEPT.formatMessage()
        );
    }

    public record RequestAcceptPayload(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            String message
    ) {

    }
}

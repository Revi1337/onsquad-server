package revi1337.onsquad.crew_request.application.notification;

import lombok.Getter;
import revi1337.onsquad.crew_request.application.notification.RequestContext.RequestAcceptedContext;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

@Getter
public class RequestAcceptNotification extends AbstractNotification {

    private final RequestAcceptPayload payload;

    public RequestAcceptNotification(RequestAcceptedContext context) {
        super(context.accepterId(), context.requesterId(), NotificationTopic.USER, NotificationDetail.CREW_ACCEPT);
        this.payload = new RequestAcceptPayload(
                context.crewId(),
                context.crewName(),
                NotificationDetail.CREW_ACCEPT.formatMessage()
        );
    }

    public record RequestAcceptPayload(
            Long crewId,
            String crewName,
            String message
    ) {

    }
}

package revi1337.onsquad.crew_request.application.notification;

import lombok.Getter;
import revi1337.onsquad.crew_request.application.notification.RequestContext.RequestAddedContext;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

@Getter
public class RequestAddNotification extends AbstractNotification {

    private final RequestPayload payload;

    public RequestAddNotification(RequestAddedContext context) {
        super(context.requesterId(), context.crewMemberId(), NotificationTopic.USER, NotificationDetail.CREW_REQUEST);
        this.payload = new RequestPayload(
                context.crewId(),
                context.crewName(),
                context.requestId(),
                NotificationDetail.CREW_REQUEST.formatMessage(context.requesterNickname())
        );
    }

    public record RequestPayload(
            Long crewId,
            String crewName,
            Long requestId,
            String message
    ) {

    }
}

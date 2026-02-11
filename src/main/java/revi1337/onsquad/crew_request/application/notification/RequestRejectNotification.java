package revi1337.onsquad.crew_request.application.notification;

import lombok.Getter;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestRejectedContext;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

@Getter
public class RequestRejectNotification extends AbstractNotification {

    private final RequestRejectPayload payload;

    public RequestRejectNotification(RequestRejectedContext context) {
        super(context.rejecterId(), context.requesterId(), NotificationTopic.USER, NotificationDetail.CREW_REJECT);
        this.payload = new RequestRejectPayload(
                context.crewId(),
                context.crewName(),
                NotificationDetail.CREW_REJECT.formatMessage()
        );
    }

    public record RequestRejectPayload(
            Long crewId,
            String crewName,
            String message
    ) {

    }
}

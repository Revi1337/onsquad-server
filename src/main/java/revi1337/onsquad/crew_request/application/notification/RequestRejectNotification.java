package revi1337.onsquad.crew_request.application.notification;

import lombok.Getter;
import revi1337.onsquad.crew_request.application.notification.RequestContext.RequestRejectedContext;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

@Getter
public class RequestRejectNotification extends AbstractNotification {

    private static final String MESSAGE = "크루 합류가 거절되었습니다.";
    private final RequestRejectPayload payload;

    public RequestRejectNotification(RequestRejectedContext context) {
        super(context.rejecterId(), context.requesterId(), NotificationTopic.USER, NotificationDetail.CREW_REJECT);
        this.payload = new RequestRejectPayload(
                context.crewId(),
                context.crewName(),
                MESSAGE
        );
    }

    public record RequestRejectPayload(
            Long crewId,
            String crewName,
            String message
    ) {

    }
}

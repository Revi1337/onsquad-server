package revi1337.onsquad.squad_request.application.notification;

import lombok.Getter;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_request.application.notification.RequestContext.RequestRejectedContext;

@Getter
public class RequestRejectNotification extends AbstractNotification {

    private static final String MESSAGE = "스쿼드 합류가 거절되었습니다.";
    private final RequestRejectPayload payload;

    public RequestRejectNotification(RequestRejectedContext context) {
        super(context.rejecterId(), context.requesterId(), NotificationTopic.USER, NotificationDetail.SQUAD_REJECT);
        this.payload = new RequestRejectPayload(
                context.crewId(),
                context.crewName(),
                context.squadId(),
                context.squadTitle(),
                MESSAGE
        );
    }

    public record RequestRejectPayload(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            String message
    ) {

    }
}

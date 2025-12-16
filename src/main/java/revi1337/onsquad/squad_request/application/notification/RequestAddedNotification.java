package revi1337.onsquad.squad_request.application.notification;

import lombok.Getter;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_request.application.notification.RequestNotificationFetchResult.RequestAddedNotificationResult;

@Getter
public class RequestAddedNotification extends AbstractNotification {
    
    private static final String MESSAGE_FORMAT = "%s 님이 스쿼드 합류를 요청하였습니다.";
    private final RequestPayload payload;

    public RequestAddedNotification(RequestAddedNotificationResult notificationResult) {
        super(notificationResult.requesterId(), notificationResult.squadMemberId(), NotificationTopic.USER, NotificationDetail.SQUAD_REQUEST);
        this.payload = new RequestPayload(
                notificationResult.crewId(),
                notificationResult.crewName(),
                notificationResult.squadId(),
                notificationResult.squadTitle(),
                notificationResult.requestId(),
                String.format(MESSAGE_FORMAT, notificationResult.requesterNickname())
        );
    }

    public record RequestPayload(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            Long requestId,
            String message
    ) {

    }
}

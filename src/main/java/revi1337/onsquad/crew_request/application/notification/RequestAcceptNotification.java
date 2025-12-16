package revi1337.onsquad.crew_request.application.notification;

import lombok.Getter;
import revi1337.onsquad.crew_request.application.notification.RequestNotificationFetchResult.RequestAcceptedNotificationResult;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

@Getter
public class RequestAcceptNotification extends AbstractNotification {

    private static final String MESSAGE = "크루에 합류하였습니다. 지금 활동을 시작해보세요!";
    private final RequestAcceptPayload payload;

    public RequestAcceptNotification(RequestAcceptedNotificationResult notificationResult) {
        super(notificationResult.accepterId(), notificationResult.requesterId(), NotificationTopic.USER, NotificationDetail.CREW_ACCEPT);
        this.payload = new RequestAcceptPayload(
                notificationResult.crewId(),
                notificationResult.crewName(),
                MESSAGE
        );
    }

    public record RequestAcceptPayload(
            Long crewId,
            String crewName,
            String message
    ) {

    }
}

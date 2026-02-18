package revi1337.onsquad.crew_request.application.notification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestRejectedContext;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

class RequestRejectNotificationTest {

    @Test
    void constructor() {
        RequestRejectedContext context = new RequestRejectedContext(
                1L,
                "crew-name",
                1L,
                2L,
                "requester-nickname"
        );

        RequestRejectNotification notification = new RequestRejectNotification(context);

        assertSoftly(softly -> {
            softly.assertThat(notification.getPublisherId()).isEqualTo(context.rejecterId());
            softly.assertThat(notification.getReceiverId()).isEqualTo(context.requesterId());
            softly.assertThat(notification.getTopic()).isSameAs(NotificationTopic.USER);
            softly.assertThat(notification.getDetail()).isSameAs(NotificationDetail.CREW_REJECT);

            softly.assertThat(notification.getPayload().crewId()).isEqualTo(context.crewId());
            softly.assertThat(notification.getPayload().crewName()).isEqualTo(context.crewName());
            softly.assertThat(notification.getPayload().message()).isEqualTo("크루 합류가 거절되었습니다.");
        });
    }
}

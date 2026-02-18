package revi1337.onsquad.crew_request.application.notification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestAddedContext;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

class RequestAddNotificationTest {

    @Test
    void constructor() {
        RequestAddedContext context = new RequestAddedContext(
                1L,
                "crew-name",
                1L,
                3L,
                2L,
                "requester-nickname"
        );

        RequestAddNotification notification = new RequestAddNotification(context);

        assertSoftly(softly -> {
            softly.assertThat(notification.getPublisherId()).isEqualTo(context.requesterId());
            softly.assertThat(notification.getReceiverId()).isEqualTo(context.crewMemberId());
            softly.assertThat(notification.getTopic()).isSameAs(NotificationTopic.USER);
            softly.assertThat(notification.getDetail()).isSameAs(NotificationDetail.CREW_REQUEST);

            softly.assertThat(notification.getPayload().crewId()).isEqualTo(context.crewId());
            softly.assertThat(notification.getPayload().crewName()).isEqualTo(context.crewName());
            softly.assertThat(notification.getPayload().message()).isEqualTo("requester-nickname 님이 크루 합류를 요청하였습니다.");
        });
    }
}

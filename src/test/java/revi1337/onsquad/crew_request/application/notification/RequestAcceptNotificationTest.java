package revi1337.onsquad.crew_request.application.notification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestAcceptedContext;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;

class RequestAcceptNotificationTest {

    @Test
    @DisplayName("크루 합류 수락 컨텍스트로 알림을 생성하면 수락자가 발신자, 신청자가 수신자인 CREW_ACCEPT 알림이 만들어진다.")
    void constructor() {
        RequestAcceptedContext context = new RequestAcceptedContext(
                1L,
                "crew-name",
                1L,
                2L,
                "requester-nickname"
        );

        RequestAcceptNotification notification = new RequestAcceptNotification(context);

        assertSoftly(softly -> {
            softly.assertThat(notification.getPublisherId()).isEqualTo(context.accepterId());
            softly.assertThat(notification.getReceiverId()).isEqualTo(context.requesterId());
            softly.assertThat(notification.getTopic()).isSameAs(NotificationTopic.USER);
            softly.assertThat(notification.getDetail()).isSameAs(NotificationDetail.CREW_ACCEPT);

            softly.assertThat(notification.getPayload().crewId()).isEqualTo(context.crewId());
            softly.assertThat(notification.getPayload().crewName()).isEqualTo(context.crewName());
            softly.assertThat(notification.getPayload().message()).isEqualTo("크루에 합류하였습니다. 지금 활동을 시작해보세요!");
        });
    }
}

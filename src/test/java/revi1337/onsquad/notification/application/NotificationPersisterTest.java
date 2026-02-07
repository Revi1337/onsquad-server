package revi1337.onsquad.notification.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew_request.application.notification.RequestAcceptNotification;
import revi1337.onsquad.crew_request.application.notification.RequestContext.RequestAcceptedContext;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;

class NotificationPersisterTest extends ApplicationLayerTestSupport {

    @Autowired
    private ObjectMapper defaultObjectMapper;

    @Autowired
    private NotificationPersister notificationPersister;

    @Test
    @DisplayName("도메인 알림 객체를 수신하여 JSON 직렬화 후 DB에 성공적으로 영속화한다")
    void persist() throws JsonProcessingException {
        RequestAcceptNotification notification = new RequestAcceptNotification(new RequestAcceptedContext(
                1L,
                "crew-name",
                1L,
                2L,
                "requester-nickname"
        ));
        String expectedJson = defaultObjectMapper.writeValueAsString(notification.getPayload());

        NotificationEntity notificationEntity = notificationPersister.persist(notification);

        assertSoftly(softly -> {
            softly.assertThat(notificationEntity.getId()).isNotNull();
            softly.assertThat(notificationEntity.getPublisherId()).isEqualTo(notification.getPublisherId());
            softly.assertThat(notificationEntity.getReceiverId()).isEqualTo(notification.getReceiverId());
            softly.assertThat(notificationEntity.getTopic()).isSameAs(notification.getTopic());
            softly.assertThat(notificationEntity.getDetail()).isSameAs(notification.getDetail());
            softly.assertThat(notificationEntity.getJson()).isEqualTo(expectedJson);
            softly.assertThat(notificationEntity.getId()).isNotNull();
        });
    }
}

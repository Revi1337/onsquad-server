package revi1337.onsquad.notification.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.config.web.ObjectMapperConfig;
import revi1337.onsquad.crew_request.application.notification.RequestAcceptNotification;
import revi1337.onsquad.crew_request.application.notification.RequestContext.RequestAcceptedContext;
import revi1337.onsquad.notification.domain.HeartbeatNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;
import revi1337.onsquad.notification.domain.model.NotificationMessage;

@JsonTest
@Import({ObjectMapperConfig.class, NotificationMessageMapper.class})
class NotificationMessageMapperTest {

    @Autowired
    private NotificationMessageMapper notificationMessageMapper;

    @Test
    @DisplayName("도메인 객체(Notification)로부터 알림 전송용 메시지 객체를 생성한다")
    void fromNotification() {
        RequestAcceptNotification notification = new RequestAcceptNotification(new RequestAcceptedContext(
                1L,
                "crew-name",
                1L,
                2L,
                "requester-nickname"
        ));

        NotificationMessage notificationMessage = notificationMessageMapper.from(notification);

        assertSoftly(softly -> {
            softly.assertThat(notificationMessage.id()).isNull();
            softly.assertThat(notificationMessage.topic()).isSameAs(notification.getTopic());
            softly.assertThat(notificationMessage.detail()).isSameAs(notification.getDetail());
            softly.assertThat(notificationMessage.publisherId()).isEqualTo(notification.getPublisherId());
            softly.assertThat(notificationMessage.receiverId()).isEqualTo(notification.getReceiverId());
            softly.assertThat(notificationMessage.read()).isNull();

            softly.assertThat(notificationMessage.payload().isObject()).isTrue();
            softly.assertThat(notificationMessage.payload().get("crewId").asLong()).isEqualTo(notification.getPayload().crewId());
            softly.assertThat(notificationMessage.payload().get("crewName").asText()).isEqualTo(notification.getPayload().crewName());
            softly.assertThat(notificationMessage.payload().get("message").asText()).isEqualTo(notification.getPayload().message());
        });
    }

    @Test
    @DisplayName("페이로드가 없는 알림 도메인 객체는 빈 JSON 객체를 가진 메시지로 변환한다")
    void fromNotification2() {
        HeartbeatNotification notification = new HeartbeatNotification();

        NotificationMessage notificationMessage = notificationMessageMapper.from(notification);

        assertSoftly(softly -> {
            softly.assertThat(notificationMessage.id()).isNull();
            softly.assertThat(notificationMessage.topic()).isSameAs(notification.getTopic());
            softly.assertThat(notificationMessage.detail()).isSameAs(notification.getDetail());
            softly.assertThat(notificationMessage.publisherId()).isNull();
            softly.assertThat(notificationMessage.receiverId()).isNull();
            softly.assertThat(notificationMessage.read()).isNull();

            softly.assertThat(notificationMessage.payload().isObject()).isTrue();
            softly.assertThat(notificationMessage.payload().isEmpty()).isTrue();
        });
    }

    @Test
    @DisplayName("DB 엔티티(NotificationEntity)로부터 알림 전송용 메시지 객체를 생성한다")
    void fromNotificationEntity() {
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .receiverId(1L)
                .publisherId(2L)
                .topic(NotificationTopic.USER)
                .detail(NotificationDetail.COMMENT)
                .json("{\"message\": \"hello\", \"count\": 1}")
                .read(false)
                .occurredAt(LocalDateTime.now())
                .build();

        NotificationMessage notificationMessage = notificationMessageMapper.from(notificationEntity);

        assertSoftly(softly -> {
            softly.assertThat(notificationMessage.id()).isNull();
            softly.assertThat(notificationMessage.topic()).isSameAs(notificationEntity.getTopic());
            softly.assertThat(notificationMessage.detail()).isSameAs(notificationEntity.getDetail());
            softly.assertThat(notificationMessage.publisherId()).isEqualTo(notificationEntity.getPublisherId());
            softly.assertThat(notificationMessage.receiverId()).isEqualTo(notificationEntity.getReceiverId());
            softly.assertThat(notificationMessage.read()).isFalse();

            softly.assertThat(notificationMessage.payload().isObject()).isTrue();
            softly.assertThat(notificationMessage.payload().get("message").asText()).isEqualTo("hello");
            softly.assertThat(notificationMessage.payload().get("count").asInt()).isEqualTo(1);
        });
    }
}

package revi1337.onsquad.notification.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.NotificationFixture.createUserNotification;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;

class NotificationCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationCommandService notificationCommandService;

    @Test
    @DisplayName("특정 알림을 읽음 처리한다")
    void read() {
        NotificationEntity notification1 = createUserNotification(1L, 2L, false, NotificationDetail.COMMENT, LocalDateTime.now());
        NotificationEntity notification2 = createUserNotification(2L, 3L, false, NotificationDetail.COMMENT_REPLY, LocalDateTime.now());
        NotificationEntity notification3 = createUserNotification(2L, 4L, false, NotificationDetail.CREW_ACCEPT, LocalDateTime.now());
        NotificationEntity notification4 = createUserNotification(2L, 5L, false, NotificationDetail.CREW_REJECT, LocalDateTime.now());
        notificationRepository.saveAll(List.of(notification1, notification2, notification3, notification4));
        clearPersistenceContext();

        notificationCommandService.read(2L, notification2.getId());

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(notificationRepository.findById(notification2.getId()).get().isRead()).isTrue();
        });
    }

    @Test
    @DisplayName("사용자의 모든 알림을 일괄 읽음 처리한다")
    void readAll() {
        NotificationEntity notification1 = createUserNotification(1L, 2L, false, NotificationDetail.COMMENT, LocalDateTime.now());
        NotificationEntity notification2 = createUserNotification(2L, 3L, false, NotificationDetail.COMMENT_REPLY, LocalDateTime.now());
        NotificationEntity notification3 = createUserNotification(2L, 4L, false, NotificationDetail.CREW_ACCEPT, LocalDateTime.now());
        NotificationEntity notification4 = createUserNotification(2L, 5L, false, NotificationDetail.CREW_REJECT, LocalDateTime.now());
        notificationRepository.saveAll(List.of(notification1, notification2, notification3, notification4));
        clearPersistenceContext();

        notificationCommandService.readAll(2L);

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(notificationRepository.findAllByReceiverId(2L, null).stream()
                    .filter(NotificationEntity::isRead)
                    .toList()).hasSize(3);
        });
    }
}

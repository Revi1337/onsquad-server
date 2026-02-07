package revi1337.onsquad.notification.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.NotificationFixture.createUserNotification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;

class NotificationRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @DisplayName("수신자 ID를 기준으로 최신순 알림 목록을 페이징하여 조회한다")
    void findAllByReceiverId() {
        LocalDateTime baseDateTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        NotificationEntity notification1 = createUserNotification(1L, 2L, false, NotificationDetail.COMMENT, baseDateTime.plusDays(4));
        NotificationEntity notification2 = createUserNotification(2L, 3L, false, NotificationDetail.COMMENT_REPLY, baseDateTime.plusDays(2));
        NotificationEntity notification3 = createUserNotification(2L, 4L, false, NotificationDetail.CREW_ACCEPT, baseDateTime.plusDays(3));
        NotificationEntity notification4 = createUserNotification(2L, 5L, false, NotificationDetail.CREW_REJECT, baseDateTime.plusDays(4));
        notificationRepository.saveAll(List.of(notification1, notification2, notification3, notification4));
        clearPersistenceContext();
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));

        Page<NotificationEntity> notifications = notificationRepository.findAllByReceiverId(2L, pageRequest);

        assertSoftly(softly -> {
            List<NotificationEntity> contents = notifications.getContent();
            softly.assertThat(contents).hasSize(2);
            softly.assertThat(contents.get(0).getPublisherId()).isEqualTo(5L);
            softly.assertThat(contents.get(1).getPublisherId()).isEqualTo(4L);
        });
    }

    @Test
    @DisplayName("지정한 ID 이후에 발생한 특정 사용자의 미수신 알림을 ID 오름차순으로 모두 복구한다")
    void findAllByReceiverIdAndIdAfterOrderByIdAsc() {
        NotificationEntity notification1 = createUserNotification(2L, 3L, false, NotificationDetail.COMMENT_REPLY, LocalDateTime.now());
        NotificationEntity notification2 = createUserNotification(2L, 4L, false, NotificationDetail.CREW_ACCEPT, LocalDateTime.now());
        NotificationEntity notification3 = createUserNotification(2L, 5L, false, NotificationDetail.CREW_REJECT, LocalDateTime.now());
        NotificationEntity notification4 = createUserNotification(2L, 6L, false, NotificationDetail.CREW_REJECT, LocalDateTime.now());
        NotificationEntity notification5 = createUserNotification(2L, 7L, false, NotificationDetail.CREW_REJECT, LocalDateTime.now());
        notificationRepository.saveAll(List.of(notification1, notification2, notification3, notification4, notification5));
        clearPersistenceContext();

        List<NotificationEntity> notifications = notificationRepository.findAllByReceiverIdAndIdAfterOrderByIdAsc(2L, 2L);

        assertSoftly(softly -> {
            softly.assertThat(notifications).hasSize(3);
            softly.assertThat(notifications.get(0).getPublisherId()).isEqualTo(5L);
            softly.assertThat(notifications.get(1).getPublisherId()).isEqualTo(6L);
            softly.assertThat(notifications.get(2).getPublisherId()).isEqualTo(7L);
        });
    }

    @Test
    @DisplayName("사용자의 읽지 않은 모든 알림을 한꺼번에 읽음 상태로 변경한다")
    void markAllAsRead() {
        NotificationEntity notification1 = createUserNotification(2L, 3L, false, NotificationDetail.COMMENT_REPLY, LocalDateTime.now());
        NotificationEntity notification2 = createUserNotification(2L, 4L, false, NotificationDetail.CREW_ACCEPT, LocalDateTime.now());
        NotificationEntity notification3 = createUserNotification(2L, 5L, false, NotificationDetail.CREW_REJECT, LocalDateTime.now());
        notificationRepository.saveAll(List.of(notification1, notification2, notification3));
        clearPersistenceContext();

        int readed = notificationRepository.markAllAsRead(2L);

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(readed).isEqualTo(3);
        });
    }

    @Test
    @DisplayName("전체 읽음 처리 시 이미 읽은 상태의 알림은 변경 대상에서 제외한다")
    void markAllAsRead2() {
        NotificationEntity notification1 = createUserNotification(2L, 3L, true, NotificationDetail.COMMENT_REPLY, LocalDateTime.now());
        NotificationEntity notification2 = createUserNotification(2L, 4L, false, NotificationDetail.CREW_ACCEPT, LocalDateTime.now());
        NotificationEntity notification3 = createUserNotification(2L, 5L, false, NotificationDetail.CREW_REJECT, LocalDateTime.now());
        notificationRepository.saveAll(List.of(notification1, notification2, notification3));
        clearPersistenceContext();

        int readed = notificationRepository.markAllAsRead(2L);

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(readed).isEqualTo(2);
        });
    }

    @Test
    @DisplayName("특정 알림을 수신자 ID와 알림 ID 검증을 거쳐 읽음 상태로 변경한다")
    void markAsRead() {
        NotificationEntity notification = notificationRepository.save(createUserNotification(1L, 2L, false, NotificationDetail.COMMENT, LocalDateTime.now()));
        clearPersistenceContext();

        int readed = notificationRepository.markAsRead(notification.getReceiverId(), notification.getId());

        assertSoftly(softly -> {
            clearPersistenceContext();
            assertThat(readed).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("이미 읽은 알림을 다시 읽음 처리 시도하면 변경된 행의 수는 0이다")
    void markAsRead2() {
        NotificationEntity notification = notificationRepository.save(createUserNotification(1L, 2L, true, NotificationDetail.COMMENT, LocalDateTime.now()));
        clearPersistenceContext();

        int readed = notificationRepository.markAsRead(notification.getReceiverId(), notification.getId());

        assertSoftly(softly -> {
            clearPersistenceContext();
            assertThat(readed).isZero();
        });
    }

    @Test
    @DisplayName("수신자 ID에 해당하는 모든 알림 데이터를 영구 삭제한다")
    void deleteByReceiverId() {
        NotificationEntity notification1 = createUserNotification(2L, 3L, false, NotificationDetail.COMMENT_REPLY, LocalDateTime.now());
        NotificationEntity notification2 = createUserNotification(2L, 4L, false, NotificationDetail.CREW_ACCEPT, LocalDateTime.now());
        NotificationEntity notification3 = createUserNotification(2L, 5L, false, NotificationDetail.CREW_REJECT, LocalDateTime.now());
        notificationRepository.saveAll(List.of(notification1, notification2, notification3));
        clearPersistenceContext();

        int deleted = notificationRepository.deleteByReceiverId(2L);

        assertThat(deleted).isEqualTo(3);
    }
}

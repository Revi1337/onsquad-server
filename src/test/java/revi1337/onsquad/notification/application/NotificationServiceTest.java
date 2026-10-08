package revi1337.onsquad.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import revi1337.onsquad.notification.domain.ConnectionNotification;
import revi1337.onsquad.notification.domain.model.NotificationMessage;
import revi1337.onsquad.notification.domain.model.NotificationMessages;
import revi1337.onsquad.notification.infrastructure.redis.RedisNotificationMessageManager;
import revi1337.onsquad.notification.infrastructure.redis.RedisTopic;
import revi1337.onsquad.notification.infrastructure.sse.NamedSseEmitter;
import revi1337.onsquad.notification.infrastructure.sse.SseEmitterManager;
import revi1337.onsquad.notification.infrastructure.sse.SseEmitterRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    private NotificationMessageMapper notificationMessageMapper;

    @Mock
    private NotificationMessageRecoverer notificationMessageRecoverer;

    @Mock
    private RedisNotificationMessageManager redisMessageManager;

    private SseEmitterRepository sseEmitterRepository;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        sseEmitterRepository = new SseEmitterRepository();
        notificationService = new NotificationService(
                new SseEmitterManager(sseEmitterRepository),
                notificationMessageMapper,
                notificationMessageRecoverer,
                redisMessageManager
        );
    }

    @Test
    @DisplayName("구독에 실패하면 예외를 그대로 던지고 emitter 를 저장소에 남기지 않는다")
    void doesNotLeaveEmitter_whenSubscribeFails() {
        doThrow(new RedisConnectionFailureException("down")).when(redisMessageManager).subscribe(anyLong(), any());

        assertThatThrownBy(() -> notificationService.connect(USER_ID, null))
                .isInstanceOf(RedisConnectionFailureException.class);

        assertThat(sseEmitterRepository.findAll()).isEmpty();
        verify(notificationMessageMapper, never()).from(any(ConnectionNotification.class));
        verify(notificationMessageRecoverer, never()).recover(any(), any());
    }

    @Test
    @DisplayName("구독은 emitter 를 저장소에 등록하기 전에 수행된다")
    void subscribesBeforeEmitterIsRegistered() {
        List<Integer> emitterCountsAtSubscribe = new ArrayList<>();
        doAnswer(invocation -> {
            emitterCountsAtSubscribe.add(sseEmitterRepository.findAll().size());
            return null;
        }).when(redisMessageManager).subscribe(anyLong(), any());
        stubConnectionMessages();

        notificationService.connect(USER_ID, null);

        assertThat(emitterCountsAtSubscribe).containsExactly(0);
    }

    @Test
    @DisplayName("연결에 성공하면 emitter 를 저장소에 등록하고 연결 알림과 누락 알림 복구를 처리한다")
    void registersEmitter_whenConnected() {
        Long lastEventId = 10L;
        stubConnectionMessages();

        NamedSseEmitter emitter = notificationService.connect(USER_ID, lastEventId);

        assertThat(emitter).isNotNull();
        assertThat(sseEmitterRepository.findAll()).containsExactly(emitter);
        verify(redisMessageManager).subscribe(USER_ID, RedisTopic.SSE_NOTIFICATION);
        verify(notificationMessageRecoverer).recover(USER_ID, lastEventId);
    }

    @Test
    @DisplayName("알림 발행은 수신자 채널로 위임한다")
    void publishesToReceiver() {
        NotificationMessage message = new NotificationMessage(null, null, null, null, 7L, null, null);

        notificationService.sendMessage(message);

        verify(redisMessageManager).publish(eq(7L), eq(RedisTopic.SSE_NOTIFICATION), eq(message));
    }

    private void stubConnectionMessages() {
        NotificationMessage connectionMessage = new NotificationMessage(null, null, null, null, USER_ID, null, null);
        given(notificationMessageMapper.from(any(ConnectionNotification.class))).willReturn(connectionMessage);
        given(notificationMessageRecoverer.recover(any(), any())).willReturn(new NotificationMessages(List.of()));
    }
}

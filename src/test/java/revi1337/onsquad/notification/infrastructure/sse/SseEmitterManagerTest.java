package revi1337.onsquad.notification.infrastructure.sse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import revi1337.onsquad.notification.domain.model.NotificationMessage;

@ExtendWith(MockitoExtension.class)
class SseEmitterManagerTest {

    @Mock
    private SseEmitterRepository sseEmitterRepository;

    @InjectMocks
    private SseEmitterManager sseEmitterManager;

    @Test
    @DisplayName("새로운 emitter를 생성할 때 타임아웃, 에러, 완료 콜백을 설정하고 저장소에 보관한다")
    void createEmitterSetsCallbacks() {
        Long userId = 1L;
        SseTopic topic = SseTopic.USER;
        Runnable mockCompletionTask = mock(Runnable.class);
        String identifier = topic.toIdentifier(userId.toString());
        given(sseEmitterRepository.save(eq(identifier), any(NamedSseEmitter.class)))
                .willAnswer(invocation -> invocation.getArgument(1));

        NamedSseEmitter emitter = sseEmitterManager.createEmitter(userId, topic, mockCompletionTask);

        assertThat(emitter.getIdentifier()).isEqualTo(identifier);
        verify(sseEmitterRepository).save(eq(identifier), any(NamedSseEmitter.class));
    }

    @Test
    @DisplayName("알림 전송 중 네트워크 오류가 발생하면 즉시 저장소에서 해당 emitter를 삭제한다")
    void deleteEmitterOnSendError() throws IOException {
        String identifier = "user:1";
        NamedSseEmitter spyEmitter = spy(new NamedSseEmitter(identifier, SseTopic.USER, 1000L));
        NotificationMessage message = mock(NotificationMessage.class);
        doThrow(new IOException("Connection Closed")).when(spyEmitter).send(any(SseEventBuilder.class));

        sseEmitterManager.send(spyEmitter, message);

        verify(sseEmitterRepository).deleteById(identifier);
        verify(spyEmitter).completeWithError(any(IOException.class));
    }
}

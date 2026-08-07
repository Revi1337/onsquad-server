package revi1337.onsquad.squad_request.application.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import revi1337.onsquad.squad_request.application.history.RequestAcceptHistory;
import revi1337.onsquad.squad_request.domain.event.RequestAccepted;
import revi1337.onsquad.squad_request.domain.model.SquadRequestContext.RequestAcceptedContext;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestContextReader;

@ExtendWith(MockitoExtension.class)
class SquadRequestEventListenerTest {

    @Mock
    private SquadRequestContextReader contextReader;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SquadRequestEventListener squadRequestEventListener;

    @Test
    @DisplayName("스쿼드 참가 신청 수락 시, 히스토리와 알림 이벤트가 함께 발행된다")
    void onRequestAccepted_publishesHistoryAndNotification() {
        RequestAcceptedContext context = new RequestAcceptedContext(1L, "crew-name", 100L, "squad-title", 10L, 20L, "requester-nickname");
        when(contextReader.readAcceptedContext(100L, 10L, 20L)).thenReturn(Optional.of(context));

        squadRequestEventListener.onRequestAccepted(new RequestAccepted(100L, 20L, 10L));

        verify(eventPublisher, times(2)).publishEvent(any(Object.class));
        verify(eventPublisher).publishEvent(any(RequestAcceptHistory.class));
    }

    @Test
    @DisplayName("컨텍스트 조회 결과가 없으면 아무 이벤트도 발행하지 않는다")
    void onRequestAccepted_doesNothing_whenContextAbsent() {
        when(contextReader.readAcceptedContext(100L, 10L, 20L)).thenReturn(Optional.empty());

        squadRequestEventListener.onRequestAccepted(new RequestAccepted(100L, 20L, 10L));

        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }
}

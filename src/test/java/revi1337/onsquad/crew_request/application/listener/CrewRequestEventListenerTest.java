package revi1337.onsquad.crew_request.application.listener;

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
import revi1337.onsquad.crew_request.application.history.RequestAcceptHistory;
import revi1337.onsquad.crew_request.domain.event.RequestAccepted;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestAcceptedContext;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestContextReader;

@ExtendWith(MockitoExtension.class)
class CrewRequestEventListenerTest {

    @Mock
    private CrewRequestContextReader contextReader;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CrewRequestEventListener crewRequestEventListener;

    @Test
    @DisplayName("크루 가입 신청 수락 시, 히스토리와 알림 이벤트가 함께 발행된다")
    void onRequestAccepted_publishesHistoryAndNotification() {
        RequestAcceptedContext context = new RequestAcceptedContext(1L, "crew-name", 10L, 20L, "requester-nickname");
        when(contextReader.readAcceptedContext(1L, 10L, 20L)).thenReturn(Optional.of(context));

        crewRequestEventListener.onRequestAccepted(new RequestAccepted(1L, 10L, 20L));

        verify(eventPublisher, times(2)).publishEvent(any(Object.class));
        verify(eventPublisher).publishEvent(any(RequestAcceptHistory.class));
    }

    @Test
    @DisplayName("컨텍스트 조회 결과가 없으면 아무 이벤트도 발행하지 않는다")
    void onRequestAccepted_doesNothing_whenContextAbsent() {
        when(contextReader.readAcceptedContext(1L, 10L, 20L)).thenReturn(Optional.empty());

        crewRequestEventListener.onRequestAccepted(new RequestAccepted(1L, 10L, 20L));

        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }
}

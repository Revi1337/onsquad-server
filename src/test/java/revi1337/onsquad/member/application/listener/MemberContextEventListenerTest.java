package revi1337.onsquad.member.application.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.announce.domain.model.AnnounceReference;
import revi1337.onsquad.history.domain.repository.HistoryRepository;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.member.domain.event.MemberContextDisposed;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;
import revi1337.onsquad.token.application.RefreshTokenManager;

@ExtendWith(MockitoExtension.class)
class MemberContextEventListenerTest {

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private RefreshTokenManager refreshTokenManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private AnnounceCacheService announceCacheService;

    @InjectMocks
    private MemberContextEventListener eventListener;

    @Test
    @DisplayName("회원 컨텍스트 처분 이벤트 발생 시, 연관된 토큰/히스토리/알림/캐시 정보를 모두 삭제한다.")
    void onDeleteTest() {
        Long memberId = 1L;
        String memberImage = "member-image";
        List<AnnounceReference> announceReferences = List.of(new AnnounceReference(1L, 2L));
        MemberContextDisposed event = new MemberContextDisposed(memberId, memberImage, announceReferences);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

        eventListener.onContextDisposed(event);

        verify(refreshTokenManager).deleteTokenBy(event.memberId());
        verify(historyRepository).deleteByMemberId(event.memberId());
        verify(notificationRepository).deleteByReceiverId(event.memberId());
        verify(announceCacheService).evictAnnounceLists(announceReferences.stream().map(AnnounceReference::crewId).toList());
        verify(announceCacheService).evictAnnouncesByReferences(announceReferences);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(FileDeleteEvent.class);
    }
}

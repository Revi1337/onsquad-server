package revi1337.onsquad.squad_comment.application.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import revi1337.onsquad.squad_comment.application.history.CommentHistory;
import revi1337.onsquad.squad_comment.application.history.CommentReplyHistory;
import revi1337.onsquad.squad_comment.domain.event.CommentAdded;
import revi1337.onsquad.squad_comment.domain.event.CommentReplyAdded;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentAddedContext;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentReplyAddedContext;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentContextReader;

@ExtendWith(MockitoExtension.class)
class SquadCommentEventListenerTest {

    @Mock
    private SquadCommentContextReader contextReader;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SquadCommentEventListener squadCommentEventListener;

    @Nested
    @DisplayName("onCommentAdded")
    class onCommentAdded {

        @Test
        @DisplayName("댓글 작성 시, 히스토리와 알림 이벤트가 함께 발행된다")
        void publishesHistoryAndNotification() {
            CommentAddedContext context = new CommentAddedContext(1L, "crew-name", 100L, "squad-title", 5L, 200L, 20L, "writer-nickname");
            when(contextReader.readAddedContext(20L, 200L)).thenReturn(Optional.of(context));

            squadCommentEventListener.onCommentAdded(new CommentAdded(20L, 200L));

            verify(eventPublisher, times(2)).publishEvent(any(Object.class));
            verify(eventPublisher).publishEvent(any(CommentHistory.class));
        }

        @Test
        @DisplayName("컨텍스트 조회 결과가 없으면 아무 이벤트도 발행하지 않는다")
        void doesNothing_whenContextAbsent() {
            when(contextReader.readAddedContext(20L, 200L)).thenReturn(Optional.empty());

            squadCommentEventListener.onCommentAdded(new CommentAdded(20L, 200L));

            verify(eventPublisher, never()).publishEvent(any(Object.class));
        }
    }

    @Nested
    @DisplayName("onCommentReplyAdded")
    class onCommentReplyAdded {

        @Test
        @DisplayName("답글 작성 시, 히스토리와 알림 이벤트가 함께 발행된다")
        void publishesHistoryAndNotification() {
            CommentReplyAddedContext context =
                    new CommentReplyAddedContext(1L, "crew-name", 100L, "squad-title", 300L, 30L, 301L, 21L, "reply-writer-nickname");
            when(contextReader.readReplyAddedContext(300L, 21L, 301L)).thenReturn(Optional.of(context));

            squadCommentEventListener.onCommentReplyAdded(new CommentReplyAdded(300L, 21L, 301L));

            verify(eventPublisher, times(2)).publishEvent(any(Object.class));
            verify(eventPublisher).publishEvent(any(CommentReplyHistory.class));
        }

        @Test
        @DisplayName("컨텍스트 조회 결과가 없으면 아무 이벤트도 발행하지 않는다")
        void doesNothing_whenContextAbsent() {
            when(contextReader.readReplyAddedContext(300L, 21L, 301L)).thenReturn(Optional.empty());

            squadCommentEventListener.onCommentReplyAdded(new CommentReplyAdded(300L, 21L, 301L));

            verify(eventPublisher, never()).publishEvent(any(Object.class));
        }
    }
}

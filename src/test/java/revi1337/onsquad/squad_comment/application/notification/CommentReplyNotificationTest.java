package revi1337.onsquad.squad_comment.application.notification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentReplyAddedContext;

@DisplayName("대댓글 알림 테스트")
class CommentReplyNotificationTest {

    @Test
    @DisplayName("대댓글 작성자가 발행자가 되고, 부모 댓글 작성자가 수신자가 되는 알림이 생성된다.")
    void constructor() {
        CommentReplyAddedContext context = new CommentReplyAddedContext(
                100L,
                "테니스 크루",
                10L,
                "주말 복식",
                1000L,
                1L,
                1001L,
                2L,
                "리뷰어"
        );

        CommentReplyNotification notification = new CommentReplyNotification(context);

        assertSoftly(softly -> {
            softly.assertThat(notification.getPublisherId()).isEqualTo(context.replyCommentWriterId());
            softly.assertThat(notification.getReceiverId()).isEqualTo(context.parentCommentWriterId());

            CommentReplyNotification.CommentReplyPayload payload = notification.getPayload();
            softly.assertThat(payload.parentId()).isEqualTo(context.parentCommentId());
            softly.assertThat(payload.replyId()).isEqualTo(context.replyCommentId());
            softly.assertThat(payload.message()).isEqualTo("리뷰어 님이 대댓글을 남겼습니다.");
        });
    }
}

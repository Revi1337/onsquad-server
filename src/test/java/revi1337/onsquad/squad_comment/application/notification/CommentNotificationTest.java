package revi1337.onsquad.squad_comment.application.notification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentAddedContext;

@DisplayName("댓글 알림 테스트")
class CommentNotificationTest {

    @Test
    @DisplayName("댓글 작성자가 발행자가 되고, 스쿼드 리더가 수신자가 되는 알림이 생성된다.")
    void constructor() {
        CommentAddedContext context = new CommentAddedContext(
                100L,
                "테니스 크루",
                10L,
                "주말 복식",
                50L,
                1000L,
                1L,
                "경학"
        );

        CommentNotification notification = new CommentNotification(context);

        assertSoftly(softly -> {
            softly.assertThat(notification.getPublisherId()).isEqualTo(context.commentWriterId());
            softly.assertThat(notification.getReceiverId()).isEqualTo(context.squadMemberId());
            softly.assertThat(notification.getDetail()).isEqualTo(NotificationDetail.COMMENT);

            CommentNotification.CommentPayload payload = notification.getPayload();
            softly.assertThat(payload.commentId()).isEqualTo(context.commentId());
            softly.assertThat(payload.message()).isEqualTo("경학 님이 스쿼드에 댓글을 남겼습니다.");
        });
    }
}

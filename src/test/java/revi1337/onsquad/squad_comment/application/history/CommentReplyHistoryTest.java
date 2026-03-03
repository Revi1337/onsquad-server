package revi1337.onsquad.squad_comment.application.history;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentReplyAddedContext;

@DisplayName("대댓글 생성 이력 테스트")
class CommentReplyHistoryTest {

    @Test
    @DisplayName("대댓글 추가 컨텍스트를 바탕으로 작성자 기준의 활동 이력이 생성된다.")
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

        CommentReplyHistory history = new CommentReplyHistory(context);

        assertSoftly(softly -> {
            softly.assertThat(history.getMemberId()).isEqualTo(context.replyCommentWriterId());
            softly.assertThat(history.getSquadCommentId()).isEqualTo(context.replyCommentId());
            softly.assertThat(history.getType()).isEqualTo(HistoryType.SQUAD_COMMENT_REPLY);
            softly.assertThat(history.getMessage()).isEqualTo("[테니스 크루 | 주말 복식] 스쿼드에 대댓글을 남겼습니다.");

            HistoryEntity entity = history.toEntity();
            softly.assertThat(entity.getMemberId()).isEqualTo(history.getMemberId());
            softly.assertThat(entity.getSquadCommentId()).isEqualTo(history.getSquadCommentId());
        });
    }
}

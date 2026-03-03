package revi1337.onsquad.squad_comment.application.history;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentAddedContext;

@DisplayName("댓글 생성 이력 테스트")
class CommentHistoryTest {

    @Test
    @DisplayName("댓글 추가 컨텍스트를 바탕으로 작성자 기준의 활동 이력이 생성된다.")
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

        CommentHistory history = new CommentHistory(context);

        assertSoftly(softly -> {
            softly.assertThat(history.getMemberId()).isEqualTo(context.commentWriterId());
            softly.assertThat(history.getSquadCommentId()).isEqualTo(context.commentId());
            softly.assertThat(history.getType()).isEqualTo(HistoryType.SQUAD_COMMENT);
            softly.assertThat(history.getMessage()).isEqualTo("[테니스 크루 | 주말 복식] 스쿼드에 댓글을 남겼습니다.");

            HistoryEntity entity = history.toEntity();
            softly.assertThat(entity.getMemberId()).isEqualTo(history.getMemberId());
            softly.assertThat(entity.getSquadCommentId()).isEqualTo(history.getSquadCommentId());
        });
    }
}

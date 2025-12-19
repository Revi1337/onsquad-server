package revi1337.onsquad.squad_comment.application.policy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.squad_comment.application.CommentMaskPolicy;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

class DefaultCommentMaskPolicyTest {

    private final CommentMaskPolicy commentMaskPolicy = new DefaultCommentMaskPolicy();

    @Test
    @DisplayName("댓글 마스킹 처리에 성공한다.")
    void success() {
        SquadCommentResult comment = new SquadCommentResult(null, 1L, "comment_1", true, null, null, null);

        SquadCommentResult applied = commentMaskPolicy.apply(comment);

        assertThat(applied.content()).isEmpty();
    }
}

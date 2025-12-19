package revi1337.onsquad.squad_comment.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

class CommentCombinatorTest {

    private final CommentCombinator commentCombinator = new CommentCombinator();

    @Nested
    @DisplayName("부모와 자식 댓글 연결을 테스트한다.")
    class Combine {

        @Test
        @DisplayName("부모와 자식 댓글 연결에 성공한다.")
        void success() {
            SquadCommentResult parent1 = new SquadCommentResult(null, 1L, "parent_1", false, null, null, null);
            SquadCommentResult parent2 = new SquadCommentResult(null, 2L, "parent_2", false, null, null, null);
            List<SquadCommentResult> children = List.of(
                    new SquadCommentResult(parent1.id(), 3L, "reply_1", false, null, null, null),
                    new SquadCommentResult(parent1.id(), 4L, "reply_2", false, null, null, null),
                    new SquadCommentResult(parent2.id(), 5L, "reply_3", false, null, null, null),
                    new SquadCommentResult(parent2.id(), 6L, "reply_4", false, null, null, null)
            );
            List<SquadCommentResult> parents = List.of(parent1, parent2);

            List<SquadCommentResult> combined = commentCombinator.combine(parents, children);

            assertThat(combined).hasSize(2);
            assertThat(combined.get(0).replies()).hasSize(2);
            assertThat(combined.get(0).replies().get(0).id()).isEqualTo(3L);
            assertThat(combined.get(0).replies().get(1).id()).isEqualTo(4L);
            assertThat(combined.get(1).replies()).hasSize(2);
            assertThat(combined.get(1).replies().get(0).id()).isEqualTo(5L);
            assertThat(combined.get(1).replies().get(1).id()).isEqualTo(6L);
        }
    }

    @Nested
    @DisplayName("모든 댓글의 계층화를 테스트한다")
    class MakeHierarchy {

        @Test
        @DisplayName("모든 댓글의 계층화에 성공한다.")
        void success() {
            List<SquadCommentResult> comments = List.of(
                    new SquadCommentResult(null, 1L, "parent_1", false, null, null, null),
                    new SquadCommentResult(null, 2L, "parent_2", false, null, null, null),
                    new SquadCommentResult(1L, 3L, "reply_1", false, null, null, null),
                    new SquadCommentResult(1L, 4L, "reply_2", false, null, null, null),
                    new SquadCommentResult(2L, 5L, "reply_3", false, null, null, null),
                    new SquadCommentResult(2L, 6L, "reply_4", false, null, null, null)
            );

            List<SquadCommentResult> combined = commentCombinator.makeHierarchy(comments);

            assertThat(combined).hasSize(2);
            assertThat(combined.get(0).replies()).hasSize(2);
            assertThat(combined.get(0).replies().get(0).id()).isEqualTo(3L);
            assertThat(combined.get(0).replies().get(1).id()).isEqualTo(4L);
            assertThat(combined.get(1).replies()).hasSize(2);
            assertThat(combined.get(1).replies().get(0).id()).isEqualTo(5L);
            assertThat(combined.get(1).replies().get(1).id()).isEqualTo(6L);
        }
    }
}

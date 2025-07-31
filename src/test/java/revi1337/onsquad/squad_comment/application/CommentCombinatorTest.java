package revi1337.onsquad.squad_comment.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

class CommentCombinatorTest {

    private final CommentCombinator commentCombinator = new CommentCombinator();

    @Nested
    @DisplayName("부모와 자식 댓글 연결을 테스트한다.")
    class Combine {

        @Test
        @DisplayName("부모와 자식 댓글 연결에 성공한다.")
        void success() {
            SquadCommentDomainDto parent1 = new SquadCommentDomainDto(null, 1L, "parent_1", null, null, null);
            SquadCommentDomainDto parent2 = new SquadCommentDomainDto(null, 2L, "parent_2", null, null, null);
            List<SquadCommentDomainDto> children = List.of(
                    new SquadCommentDomainDto(parent1.commentId(), 3L, "reply_1", null, null, null),
                    new SquadCommentDomainDto(parent1.commentId(), 4L, "reply_2", null, null, null),
                    new SquadCommentDomainDto(parent2.commentId(), 5L, "reply_3", null, null, null),
                    new SquadCommentDomainDto(parent2.commentId(), 6L, "reply_4", null, null, null)
            );
            Map<Long, SquadCommentDomainDto> parentMap1 = new HashMap<>() {{
                put(parent1.commentId(), parent1);
                put(parent2.commentId(), parent2);
            }};

            List<SquadCommentDomainDto> combined = commentCombinator.combine(parentMap1, children);

            assertThat(combined).hasSize(2);
            assertThat(combined.get(0).replies()).hasSize(2);
            assertThat(combined.get(0).replies().get(0).commentId()).isEqualTo(3L);
            assertThat(combined.get(0).replies().get(1).commentId()).isEqualTo(4L);
            assertThat(combined.get(1).replies()).hasSize(2);
            assertThat(combined.get(1).replies().get(0).commentId()).isEqualTo(5L);
            assertThat(combined.get(1).replies().get(1).commentId()).isEqualTo(6L);
        }
    }

    @Nested
    @DisplayName("모든 댓글의 계층화를 테스트한다")
    class MakeHierarchy {

        @Test
        @DisplayName("모든 댓글의 계층화에 성공한다.")
        void success() {
            List<SquadCommentDomainDto> comments = List.of(
                    new SquadCommentDomainDto(null, 1L, "parent_1", null, null, null),
                    new SquadCommentDomainDto(null, 2L, "parent_2", null, null, null),
                    new SquadCommentDomainDto(1L, 3L, "reply_1", null, null, null),
                    new SquadCommentDomainDto(1L, 4L, "reply_2", null, null, null),
                    new SquadCommentDomainDto(2L, 5L, "reply_3", null, null, null),
                    new SquadCommentDomainDto(2L, 6L, "reply_4", null, null, null)
            );

            List<SquadCommentDomainDto> combined = commentCombinator.makeHierarchy(comments);

            assertThat(combined).hasSize(2);
            assertThat(combined.get(0).replies()).hasSize(2);
            assertThat(combined.get(0).replies().get(0).commentId()).isEqualTo(3L);
            assertThat(combined.get(0).replies().get(1).commentId()).isEqualTo(4L);
            assertThat(combined.get(1).replies()).hasSize(2);
            assertThat(combined.get(1).replies().get(0).commentId()).isEqualTo(5L);
            assertThat(combined.get(1).replies().get(1).commentId()).isEqualTo(6L);
        }
    }
}

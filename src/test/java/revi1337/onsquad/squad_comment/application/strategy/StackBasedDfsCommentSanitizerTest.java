package revi1337.onsquad.squad_comment.application.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.squad_comment.application.CommentCombinator;
import revi1337.onsquad.squad_comment.application.CommentSanitizer;
import revi1337.onsquad.squad_comment.application.policy.DefaultCommentMaskPolicy;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

class StackBasedDfsCommentSanitizerTest {

    private final CommentCombinator commentCombinator = new CommentCombinator();
    private final CommentSanitizer commentSanitizer = new CommentSanitizer(new StackBasedDfsCommentSanitizer(new DefaultCommentMaskPolicy()));

    @Test
    @DisplayName("계층화된 댓글 구조에서 스택 DFS를 이용한 삭제된 댓글 마스킹에 성공한다.")
    void sanitizeUsingStack() {
        List<SquadCommentDomainDto> hierarchy = buildCommentHierarchy();

        List<SquadCommentDomainDto> comments = commentSanitizer.sanitize(hierarchy);

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).content()).isEqualTo("");
        assertThat(comments.get(0).replies().get(0).content()).isEqualTo("");
        assertThat(comments.get(1).replies().get(1).content()).isEqualTo("");
    }

    private List<SquadCommentDomainDto> buildCommentHierarchy() {
        SquadCommentDomainDto parent1 = new SquadCommentDomainDto(null, 1L, "parent_1", true, null, null, null);
        SquadCommentDomainDto parent2 = new SquadCommentDomainDto(null, 2L, "parent_2", false, null, null, null);
        List<SquadCommentDomainDto> replies = List.of(
                new SquadCommentDomainDto(parent1.id(), 3L, "reply_1", true, null, null, null),
                new SquadCommentDomainDto(parent1.id(), 4L, "reply_2", false, null, null, null),
                new SquadCommentDomainDto(parent2.id(), 5L, "reply_3", false, null, null, null),
                new SquadCommentDomainDto(parent2.id(), 6L, "reply_4", true, null, null, null)
        );
        return commentCombinator.combine(List.of(parent1, parent2), replies);
    }
}

package revi1337.onsquad.squad_comment.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

class CommentSanitizerTest {

    private final CommentCombinator commentCombinator = new CommentCombinator();
    private final CommentSanitizer commentSanitizer = new CommentSanitizer();

    @Test
    @DisplayName("계층화된 댓글 구조에서 반복문을 이용한 삭제된 댓글 마스킹에 성공한다.")
    void sanitizeIteratively() {
        List<SquadCommentDomainDto> hierarchy = buildCommentHierarchy();

        List<SquadCommentDomainDto> comments = commentSanitizer.sanitizeIteratively(hierarchy);

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).content()).isEqualTo("");
        assertThat(comments.get(0).replies().get(0).content()).isEqualTo("");
        assertThat(comments.get(1).replies().get(1).content()).isEqualTo("");
    }

    @Test
    @DisplayName("계층화된 댓글 구조에서 스택 DFS를 이용한 삭제된 댓글 마스킹에 성공한다.")
    void sanitizeUsingStack() {
        List<SquadCommentDomainDto> hierarchy = buildCommentHierarchy();

        List<SquadCommentDomainDto> comments = commentSanitizer.sanitizeUsingStack(hierarchy);

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).content()).isEqualTo("");
        assertThat(comments.get(0).replies().get(0).content()).isEqualTo("");
        assertThat(comments.get(1).replies().get(1).content()).isEqualTo("");
    }

    @Test
    @DisplayName("계층화된 댓글 구조에서 재귀 DFS를 이용한 삭제된 댓글 마스킹에 성공한다.")
    void sanitizeRecursively() {
        List<SquadCommentDomainDto> hierarchy = buildCommentHierarchy();

        List<SquadCommentDomainDto> comments = commentSanitizer.sanitizeRecursively(hierarchy);

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).content()).isEqualTo("");
        assertThat(comments.get(0).replies().get(0).content()).isEqualTo("");
        assertThat(comments.get(1).replies().get(1).content()).isEqualTo("");
    }


    @Test
    @DisplayName("하나의 댓글 구조에서 재귀 DFS를 이용한 삭제된 댓글 마스킹에 성공한다.")
    void sanitizeSingleRecursively() {
        List<SquadCommentDomainDto> hierarchy = buildCommentHierarchy();

        SquadCommentDomainDto comments = commentSanitizer.sanitizeSingleRecursively(hierarchy.get(0));

        assertThat(comments.content()).isEqualTo("");
        assertThat(comments.replies().get(0).content()).isEqualTo("");
        assertThat(comments.replies().get(1).content()).isEqualTo("reply_2");
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
        List<SquadCommentDomainDto> hierarchy = commentCombinator.combine(List.of(parent1, parent2), replies);
        return hierarchy;
    }
}
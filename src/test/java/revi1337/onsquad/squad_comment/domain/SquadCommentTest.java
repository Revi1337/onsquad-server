package revi1337.onsquad.squad_comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.squad_comment.error.exception.SquadCommentDomainException;

class SquadCommentTest {

    @Test
    @DisplayName("댓글 생성에 성공한다.")
    void success1() {
        String content = "content";

        SquadComment comment = SquadComment.create(content, null, null);

        assertThat(comment).isNotNull();
    }

    @Test
    @DisplayName("댓글 동등성 비교에 성공한다.")
    void success2() {
        SquadComment comment = SquadComment.create("content", null, null);
        ReflectionTestUtils.setField(comment, "id", 1L);
        SquadComment other = SquadComment.create("content", null, null);
        ReflectionTestUtils.setField(other, "id", 1L);

        assertThat(comment).isEqualTo(other);
    }

    @Test
    @DisplayName("댓글이 부모댓글인지 확인에 성공한다.")
    void success3() {
        SquadComment parent = SquadComment.create("parent", null, null);
        SquadComment child = SquadComment.createReply(parent, "child", null, null);
        ReflectionTestUtils.setField(parent, "id", 1L);
        ReflectionTestUtils.setField(child, "id", 2L);

        assertThat(parent.isParent()).isTrue();
        assertThat(child.isNotParent()).isTrue();
    }

    @Test
    @DisplayName("댓글 내용이 null 이면 실패한다.")
    void fail1() {
        String content = null;

        assertThatThrownBy(() -> SquadComment.create(content, null, null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("댓글 내용이 1 ~ 250자가 아니면 실패한다.")
    void fail2() {
        String content = "";

        assertThatThrownBy(() -> SquadComment.create(content, null, null))
                .isExactlyInstanceOf(SquadCommentDomainException.InvalidLength.class);
    }
}
package revi1337.onsquad.squad_comment.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.domain.entity.Squad;
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

    @Test
    @DisplayName("댓글이 업데이트에 성공한다.")
    void success4() {
        SquadComment comment = SquadComment.create("parent", null, null);
        String content = "update-content";

        comment.update(content);

        assertThat(comment.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("댓글 작성자 정보가 일치하지 않으면 true 를 반환한다.")
    void success5() {
        CrewMember crewMember = defaultInstance(CrewMember.class, 1L);
        SquadComment comment = SquadComment.create("parent", null, crewMember);

        boolean mismatch = comment.mismatchWriterId(100L);

        assertThat(mismatch).isTrue();
    }

    @Test
    @DisplayName("댓글 작성자 정보가 일치하지 않으면 false 를 반환한다.")
    void success6() {
        CrewMember crewMember = defaultInstance(CrewMember.class, 1L);
        SquadComment comment = SquadComment.create("parent", null, crewMember);

        boolean mismatch = comment.mismatchWriterId(crewMember.getId());

        assertThat(mismatch).isFalse();
    }

    @Test
    @DisplayName("댓글이 속한 스쿼드 정보가 일치하지 않으면 true 를 반환한다.")
    void success7() {
        Squad squad = defaultInstance(Squad.class, 1L);
        SquadComment comment = SquadComment.create("parent", squad, null);

        boolean mismatch = comment.mismatchSquadId(100L);

        assertThat(mismatch).isTrue();
    }

    @Test
    @DisplayName("댓글이 속한 스쿼드 정보가 일치하면 false 를 반환한다.")
    void success8() {
        Squad squad = defaultInstance(Squad.class, 1L);
        SquadComment comment = SquadComment.create("parent", squad, null);

        boolean mismatch = comment.mismatchSquadId(squad.getId());

        assertThat(mismatch).isFalse();
    }

    private <T> T defaultInstance(Class<T> clazz, Long value) {
        try {
            Constructor<T> constructor = ReflectionUtils.accessibleConstructor(clazz);
            constructor.setAccessible(true);
            T instance = constructor.newInstance();
            ReflectionTestUtils.setField(instance, "id", value);
            return instance;
        } catch (Exception exception) {
            throw new IllegalArgumentException();
        }
    }
}

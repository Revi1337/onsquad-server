package revi1337.onsquad.squad_comment.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_comment.domain.error.SquadCommentDomainException;

@DisplayName("스쿼드 댓글 엔티티 테스트")
class SquadCommentTest {

    @Nested
    @DisplayName("댓글 생성 테스트")
    class Create {

        @Test
        @DisplayName("정적 팩토리 메서드 create를 통해 부모 댓글을 생성한다.")
        void create() {
            Member writer = createMember(1L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(1L, crew, writer);
            String content = "안녕하세요. 스쿼드 참여하고 싶습니다!";

            SquadComment comment = SquadComment.create(content, squad, writer);

            assertSoftly(softly -> {
                softly.assertThat(comment.getContent()).isEqualTo(content);
                softly.assertThat(comment.getSquad()).isEqualTo(squad);
                softly.assertThat(comment.getMember()).isEqualTo(writer);
                softly.assertThat(comment.getParent()).isNull();
                softly.assertThat(comment.isParent()).isTrue();
            });
        }

        @Test
        @DisplayName("정적 팩토리 메서드 createReply를 통해 답글을 생성한다.")
        void createReply() {
            Member writer = createMember(1L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(1L, crew, writer);
            SquadComment parent = SquadComment.create("부모 댓글", squad, writer);
            String replyContent = "답글입니다.";

            SquadComment reply = SquadComment.createReply(parent, replyContent, squad, writer);

            assertSoftly(softly -> {
                softly.assertThat(reply.getContent()).isEqualTo(replyContent);
                softly.assertThat(reply.getParent()).isEqualTo(parent);
                softly.assertThat(reply.isNotParent()).isTrue();
            });
        }
    }

    @Nested
    @DisplayName("댓글 유효성 검증 테스트")
    class Validation {

        @Test
        @DisplayName("댓글 내용이 null이면 예외가 발생한다.")
        void validateNull() {
            assertThatThrownBy(() -> SquadComment.create(null, null, null))
                    .isExactlyInstanceOf(NullPointerException.class)
                    .hasMessage("댓글은 null 일 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("댓글 내용이 비어있으면 예외가 발생한다.")
        void validateEmpty(String content) {
            assertThatThrownBy(() -> SquadComment.create(content, null, null))
                    .isExactlyInstanceOf(SquadCommentDomainException.InvalidLength.class);
        }

        @Test
        @DisplayName("댓글 내용이 최대 길이를 초과하면 예외가 발생한다.")
        void validateMaxLength() {
            String longContent = "a".repeat(251);

            assertThatThrownBy(() -> SquadComment.create(longContent, null, null))
                    .isExactlyInstanceOf(SquadCommentDomainException.InvalidLength.class);
        }
    }

    @Test
    @DisplayName("댓글 내용을 수정할 수 있다.")
    void update() {
        SquadComment comment = SquadComment.create("기존 내용", null, null);
        String newContent = "수정된 내용";

        comment.update(newContent);

        assertThat(comment.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("댓글 삭제 시 삭제 상태가 변경되고 삭제 시간이 기록된다.")
    void delete() {
        SquadComment comment = SquadComment.create("삭제할 내용", null, null);

        comment.delete();

        assertSoftly(softly -> {
            softly.assertThat(comment.isDeleted()).isTrue();
            softly.assertThat(comment.getDeletedAt()).isNotNull();
        });
    }

    @Test
    @DisplayName("동일한 ID를 가진 댓글 객체는 동등한 것으로 판단한다.")
    void equalsAndHashCode() {
        SquadComment c1 = SquadComment.create("내용1", null, null);
        SquadComment c2 = SquadComment.create("내용2", null, null);
        ReflectionTestUtils.setField(c1, "id", 1L);
        ReflectionTestUtils.setField(c2, "id", 1L);

        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }
}

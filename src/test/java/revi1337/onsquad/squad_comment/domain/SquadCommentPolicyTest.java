package revi1337.onsquad.squad_comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createLeaderSquadMember;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.error.SquadCommentBusinessException;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@DisplayName("스쿼드 댓글 정책 테스트")
class SquadCommentPolicyTest {

    @Nested
    @DisplayName("댓글 상태 확인")
    class status {

        @Test
        @DisplayName("삭제된 댓글이면 true를 반환한다.")
        void isDeleted() {
            Member writer = createMember(1L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(1L, crew, writer);
            SquadComment comment = createSquadComment(squad, writer, true);

            boolean isDeleted = SquadCommentPolicy.isDeleted(comment);

            assertThat(isDeleted).isTrue();
        }

        @Test
        @DisplayName("부모 댓글이 아니면(답글이면) true를 반환한다.")
        void isNotParent() {
            Member writer = createMember(1L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(1L, crew, writer);
            SquadComment parent = createSquadComment(squad, writer, false);
            SquadComment reply = createSquadCommentReply(parent, squad, writer, false);

            boolean isNotParent = SquadCommentPolicy.isNotParent(reply);

            assertThat(isNotParent).isTrue();
        }
    }

    @Nested
    @DisplayName("댓글 삭제 가능 여부 검증")
    class canDelete {

        @Test
        @DisplayName("이미 삭제된 댓글은 다시 삭제할 수 없다.")
        void fail1() {
            Member writer = createMember(1L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(1L, crew, writer);
            SquadComment comment = createSquadComment(squad, writer, true);

            Member leaderMember = createMember(2L);
            SquadMember me = createLeaderSquadMember(squad, leaderMember);

            boolean canDelete = SquadCommentPolicy.canDelete(comment, me);

            assertThat(canDelete).isFalse();
        }

        @Test
        @DisplayName("댓글 작성자는 본인의 댓글을 삭제할 수 있다.")
        void success1() {
            Member writer = createMember(1L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(1L, crew, writer);
            SquadComment comment = createSquadComment(squad, writer, false);

            SquadMember me = createGeneralSquadMember(squad, writer);

            boolean canDelete = SquadCommentPolicy.canDelete(comment, me);

            assertThat(canDelete).isTrue();
        }

        @Test
        @DisplayName("스쿼드 리더는 다른 사람의 댓글을 삭제할 수 있다.")
        void success2() {
            Member writer = createMember(1L);
            Member leaderMember = createMember(2L);
            Crew crew = createCrew(1L, leaderMember);
            Squad squad = createSquad(1L, crew, leaderMember);

            SquadComment comment = createSquadComment(squad, writer, false);
            SquadMember me = createLeaderSquadMember(squad, leaderMember);

            boolean canDelete = SquadCommentPolicy.canDelete(comment, me);

            assertThat(canDelete).isTrue();
        }

        @Test
        @DisplayName("작성자도 아니고 리더도 아니면 삭제할 수 없다.")
        void fail2() {
            Member writer = createMember(1L);
            Member stranger = createMember(2L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(1L, crew, writer);

            SquadComment comment = createSquadComment(squad, writer, false);
            SquadMember me = createGeneralSquadMember(squad, stranger);

            boolean canDelete = SquadCommentPolicy.canDelete(comment, me);

            assertThat(canDelete).isFalse();
        }
    }

    @Nested
    @DisplayName("정합성 및 권한 예외 검증")
    class ensure {

        @Test
        @DisplayName("부모 댓글이 아닐 경우(답글인 경우) 부모 검증 시 예외가 발생한다.")
        void ensureParent() {
            Member writer = createMember(1L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(1L, crew, writer);
            SquadComment parent = createSquadComment(squad, writer, false);
            SquadComment reply = createSquadCommentReply(parent, squad, writer, false);

            assertThatThrownBy(() -> SquadCommentPolicy.ensureParent(reply))
                    .isExactlyInstanceOf(SquadCommentBusinessException.NotParent.class);
        }

        @Test
        @DisplayName("삭제된 댓글에 대해 alive 검증 시 예외가 발생한다.")
        void ensureAlive() {
            Member writer = createMember(1L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(1L, crew, writer);
            SquadComment comment = createSquadComment(squad, writer, true);

            assertThatThrownBy(() -> SquadCommentPolicy.ensureAlive(comment))
                    .isExactlyInstanceOf(SquadCommentBusinessException.Deleted.class);
        }

        @Test
        @DisplayName("댓글의 스쿼드 식별자와 요청된 스쿼드 식별자가 다르면 예외가 발생한다.")
        void ensureMatchSquad() {
            Member writer = createMember(1L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(100L, crew, writer);
            SquadComment comment = createSquadComment(squad, writer, false);

            assertThatThrownBy(() -> SquadCommentPolicy.ensureMatchSquad(comment, 200L))
                    .isExactlyInstanceOf(SquadCommentBusinessException.MismatchReference.class);
        }

        @Test
        @DisplayName("본인의 댓글이 아닌데 작성자 검증을 시도하면 예외가 발생한다.")
        void ensureMatchWriter() {
            Member writer = createMember(1L);
            Member otherMember = createMember(2L);
            Crew crew = createCrew(1L, writer);
            Squad squad = createSquad(1L, crew, writer);
            SquadComment comment = createSquadComment(squad, writer, false);

            assertThatThrownBy(() -> SquadCommentPolicy.ensureMatchWriter(comment, otherMember.getId()))
                    .isExactlyInstanceOf(SquadCommentBusinessException.InsufficientAuthority.class);
        }

        @Test
        @DisplayName("댓글이 달린 스쿼드와 삭제를 시도하는 멤버의 소속 스쿼드가 다르면 예외가 발생한다.")
        void ensureDeletableMismatchSquad() {
            Member writer = createMember(1L);
            Crew crew1 = createCrew(1L, writer);
            Squad squad1 = createSquad(1L, crew1, writer);
            SquadComment commentFromSquad1 = createSquadComment(squad1, writer, false);

            Member leader = createMember(2L);
            Crew crew2 = createCrew(2L, leader);
            Squad squad2 = createSquad(2L, crew2, leader);
            SquadMember leaderInSquad2 = createLeaderSquadMember(squad2, leader);

            assertThatThrownBy(() -> SquadCommentPolicy.ensureDeletable(commentFromSquad1, leaderInSquad2))
                    .isExactlyInstanceOf(SquadCommentBusinessException.MismatchReference.class);
        }
    }

    public static SquadComment createSquadComment(Squad squad, Member member, boolean deleted) {
        SquadComment comment = SquadComment.create(UUID.randomUUID().toString(), squad, member);
        ReflectionTestUtils.setField(comment, "deleted", deleted);
        return comment;
    }

    public static SquadComment createSquadCommentReply(SquadComment parent, Squad squad, Member member, boolean deleted) {
        SquadComment reply = SquadComment.createReply(parent, UUID.randomUUID().toString(), squad, member);
        ReflectionTestUtils.setField(reply, "deleted", deleted);
        return reply;
    }
}

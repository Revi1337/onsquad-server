package revi1337.onsquad.squad_comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD_1;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD_2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_comment.domain.SquadComment;
import revi1337.onsquad.squad_comment.domain.SquadCommentRepository;
import revi1337.onsquad.squad_comment.error.exception.SquadCommentBusinessException;
import revi1337.onsquad.squad_comment.error.exception.SquadCommentBusinessException.NotFound;

class SquadCommentCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private SquadCommentRepository squadCommentRepository;

    @Autowired
    private SquadRepository squadRepository;

    @Autowired
    private SquadCommentCommandService squadCommentCommandService;

    @Nested
    @DisplayName("댓글 생성을 테스트한다.")
    class Add {

        @Test
        @DisplayName("댓글 생성에 성공한다")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            clearPersistenceContext();

            Long COMMENT_ID = squadCommentCommandService.add(REVI.getId(), CREW.getId(), SQUAD.getId(), "comment_1");

            assertThat(COMMENT_ID).isNotNull();
        }

        @Test
        @DisplayName("사용자가 크루에 속해있지 않다면 실패한다.")
        void fail1() {
            Member REVI = memberJpaRepository.save(REVI());
            crewJpaRepository.save(CREW_1(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Crew CREW2 = crewJpaRepository.save(CREW_2(ANDONG));
            Long DUMMY_SQUAD_ID = 1L;
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService
                    .add(REVI.getId(), CREW2.getId(), DUMMY_SQUAD_ID, "comment_1"))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotParticipant.class);
        }

        @Test
        @DisplayName("스쿼드가 존재하지 않으면 실패한다.")
        void fail2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Long DUMMY_SQUAD_ID = 1L;
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService
                    .add(REVI.getId(), CREW.getId(), DUMMY_SQUAD_ID, "comment_1"))
                    .isExactlyInstanceOf(SquadBusinessException.NotFound.class);
        }

        @Test
        @DisplayName("스쿼드가 속한 크루 정보가 일치하지 않으면 실패한다.")
        void fail3() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Crew CREW2 = crewJpaRepository.save(CREW_2(ANDONG));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            squadRepository.save(SQUAD_1(OWNER, CREW1));
            Squad SQUAD2 = squadRepository.save(SQUAD_2(OWNER, CREW2));
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService
                    .add(REVI.getId(), CREW1.getId(), SQUAD2.getId(), "comment_1"))
                    .isExactlyInstanceOf(SquadBusinessException.MismatchReference.class);
        }
    }

    @Nested
    @DisplayName("대댓글 생성을 테스트한다.")
    class AddReply {

        @Test
        @DisplayName("대댓글 생성에 성공한다")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT1 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            clearPersistenceContext();

            Long REPLY_ID = squadCommentCommandService.addReply(REVI.getId(), CREW.getId(), SQUAD.getId(), PARENT1.getId(), "reply_1");

            assertThat(REPLY_ID).isNotNull();
        }

        @Test
        @DisplayName("사용자가 크루에 속해있지 않다면 실패한다.")
        void fail1() {
            Member REVI = memberJpaRepository.save(REVI());
            crewJpaRepository.save(CREW_1(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Crew CREW2 = crewJpaRepository.save(CREW_2(ANDONG));
            Long DUMMY_SQUAD_ID = 1L;
            Long DUMMY_PARENT_ID = 1L;
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService
                    .addReply(REVI.getId(), CREW2.getId(), DUMMY_SQUAD_ID, DUMMY_PARENT_ID, "reply_1"))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotParticipant.class);
        }

        @Test
        @DisplayName("부모 댓글이 없으면 실패한다.")
        void fail2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            Long DUMMY_PARENT_ID = 2L;
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService
                    .addReply(REVI.getId(), CREW.getId(), SQUAD.getId(), DUMMY_PARENT_ID, "reply_1"))
                    .isExactlyInstanceOf(NotFound.class);
        }

        @Test
        @DisplayName("부모 댓글이 속한 스쿼드의 정보가 일치하지 않으면 실패한다.")
        void fail3() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD1 = squadRepository.save(SQUAD_1(OWNER, CREW));
            Squad SQUAD2 = squadRepository.save(SQUAD_2(OWNER, CREW));
            SquadComment PARENT1 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD1, OWNER));
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService
                    .addReply(REVI.getId(), CREW.getId(), SQUAD2.getId(), PARENT1.getId(), "reply_1"))
                    .isExactlyInstanceOf(NotFound.class);
        }

        @Test
        @DisplayName("댓글이 부모 댓글이 아니라, 대댓글이면 실패한다.")
        void fail4() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            SquadComment REPLY = squadCommentRepository.save(SquadComment.createReply(PARENT, "reply_1", SQUAD, OWNER));
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService
                    .addReply(REVI.getId(), CREW.getId(), SQUAD.getId(), REPLY.getId(), "reply_2"))
                    .isExactlyInstanceOf(SquadCommentBusinessException.NotParent.class);
        }
    }

    @Nested
    @DisplayName("댓글 수정을 테스트한다.")
    class Update {

        @Test
        @DisplayName("본인이 작성한 댓글 수정에 성공한다.")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            String content = "update-content";
            clearPersistenceContext();

            squadCommentCommandService.update(REVI.getId(), CREW.getId(), SQUAD.getId(), PARENT.getId(), content);
            clearPersistenceContext();

            SquadComment comment = squadCommentRepository.getById(PARENT.getId());
            assertThat(comment.getContent()).isEqualTo(content);
        }

        @Test
        @DisplayName("본인이 작성한 대댓글 수정에 성공한다.")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            SquadComment REPLY = squadCommentRepository.save(SquadComment.createReply(PARENT, "reply_1", SQUAD, OWNER));
            String content = "update-content";
            clearPersistenceContext();

            squadCommentCommandService.update(REVI.getId(), CREW.getId(), SQUAD.getId(), REPLY.getId(), content);
            clearPersistenceContext();

            SquadComment comment = squadCommentRepository.getById(REPLY.getId());
            assertThat(comment.getContent()).isEqualTo(content);
        }

        @Test
        @DisplayName("댓글이 속한 스쿼드 정보가 다르면 댓글 수정에 실패한다.")
        void fail1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            CrewMember OWNER1 = crewMemberRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            Squad SQUAD1 = squadRepository.save(SQUAD(OWNER1, CREW1));
            squadCommentRepository.save(SquadComment.create("parent_1", SQUAD1, OWNER1));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Crew CREW2 = crewJpaRepository.save(CREW_2(ANDONG));
            CrewMember OWNER2 = crewMemberRepository.findByCrewIdAndMemberId(CREW2.getId(), ANDONG.getId()).get();
            Squad SQUAD2 = squadRepository.save(SQUAD(OWNER2, CREW2));
            SquadComment PARENT2 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD2, OWNER2));
            String content = "update-content";
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService.update(REVI.getId(), CREW1.getId(), SQUAD2.getId(), PARENT2.getId(), content))
                    .isExactlyInstanceOf(SquadBusinessException.MismatchReference.class);
        }

        @Test
        @DisplayName("삭제된 댓글이면, 댓글 수정에 실패한다.")
        void fail2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT = SquadComment.create("parent_1", SQUAD, OWNER);
            PARENT.delete();
            squadCommentRepository.save(PARENT);
            String content = "update-content";
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService.update(REVI.getId(), CREW.getId(), SQUAD.getId(), PARENT.getId(), content))
                    .isExactlyInstanceOf(SquadCommentBusinessException.Deleted.class);
        }

        @Test
        @DisplayName("댓글 작성자 정보가 다르면 댓글 수정에 실패한다.")
        void fail3() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            SquadComment REPLY = squadCommentRepository.save(SquadComment.createReply(PARENT, "reply_1", SQUAD, OWNER));
            String content = "update-content";
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService.update(GENERAL.getId(), CREW.getId(), SQUAD.getId(), REPLY.getId(), content))
                    .isExactlyInstanceOf(SquadCommentBusinessException.NonMatchWriterId.class);
        }
    }

    @Nested
    @DisplayName("댓글 삭제를 테스트한다.")
    class Delete {

        @Test
        @DisplayName("본인이 작성한 댓글 삭제에 성공한다.")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            CrewMember GENERAL = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, GENERAL));
            squadCommentRepository.save(SquadComment.createReply(PARENT, "reply_1", SQUAD, OWNER));
            clearPersistenceContext();

            squadCommentCommandService.delete(ANDONG.getId(), CREW.getId(), SQUAD.getId(), PARENT.getId());
            clearPersistenceContext();

            assertThat(squadCommentRepository.getById(PARENT.getId()).isDeleted()).isTrue();
        }

        @Test
        @DisplayName("스쿼드 작성자는 스쿼드에 속한 댓글을 지울 수 있다.")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            CrewMember GENERAL = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            SquadComment REPLY = squadCommentRepository.save(SquadComment.createReply(PARENT, "reply_1", SQUAD, GENERAL));
            clearPersistenceContext();

            squadCommentCommandService.delete(REVI.getId(), CREW.getId(), SQUAD.getId(), REPLY.getId());
            clearPersistenceContext();

            assertThat(squadCommentRepository.getById(REPLY.getId()).isDeleted()).isTrue();
        }

        @Test
        @DisplayName("댓글이 속한 스쿼드가 크루에 속하지 않으면 삭제에 실패한다.")
        void fail1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            CrewMember OWNER1 = crewMemberRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            Squad SQUAD1 = squadRepository.save(SQUAD(OWNER1, CREW1));
            squadCommentRepository.save(SquadComment.create("parent_1", SQUAD1, OWNER1));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Crew CREW2 = crewJpaRepository.save(CREW_2(ANDONG));
            CrewMember OWNER2 = crewMemberRepository.findByCrewIdAndMemberId(CREW2.getId(), ANDONG.getId()).get();
            Squad SQUAD2 = squadRepository.save(SQUAD(OWNER2, CREW2));
            SquadComment PARENT2 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD2, OWNER2));
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService.delete(REVI.getId(), CREW1.getId(), SQUAD2.getId(), PARENT2.getId()))
                    .isExactlyInstanceOf(SquadBusinessException.MismatchReference.class);
        }

        @Test
        @DisplayName("스쿼드장이 아니거나, 댓글의 작성자가 아니라면 삭제에 실패한다.")
        void fail2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            CrewMember OWNER1 = crewMemberRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            Squad SQUAD1 = squadRepository.save(SQUAD(OWNER1, CREW1));
            SquadComment PARENT1 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD1, OWNER1));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW1, ANDONG));
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService.delete(ANDONG.getId(), CREW1.getId(), SQUAD1.getId(), PARENT1.getId()))
                    .isExactlyInstanceOf(SquadCommentBusinessException.NonMatchWriterId.class);
        }
    }
}

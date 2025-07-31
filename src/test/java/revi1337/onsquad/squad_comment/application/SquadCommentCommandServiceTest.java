package revi1337.onsquad.squad_comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
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

            Long REPLY_ID = squadCommentCommandService.addReply(
                    REVI.getId(), CREW.getId(), SQUAD.getId(), PARENT1.getId(), "reply_1"
            );

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
}

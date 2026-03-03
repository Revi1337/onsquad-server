package revi1337.onsquad.squad_comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.event.CommentAdded;
import revi1337.onsquad.squad_comment.domain.event.CommentReplyAdded;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentJpaRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;

class SquadCommentCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentRepository;

    @Autowired
    private SquadCommentCommandService squadCommentCommandService;

    @Autowired
    private ApplicationEvents events;

    @Nested
    @DisplayName("댓글 등록")
    class add {

        @Test
        @DisplayName("크루 멤버라면 스쿼드에 댓글을 등록할 수 있고 이벤트를 발행한다.")
        void success() {
            Member leader = memberRepository.save(createMember("리더"));
            Member writer = memberRepository.save(createMember("작성자"));
            Crew crew = createCrew(leader, "우리 크루");
            crew.addCrewMember(createGeneralCrewMember(crew, writer));
            crewRepository.save(crew);
            Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
            clearPersistenceContext();

            squadCommentCommandService.add(writer.getId(), squad.getId(), "댓글 내용입니다.");

            assertSoftly(softly -> {
                softly.assertThat(squadCommentRepository.findAll()).hasSize(1);
                softly.assertThat(events.stream(CommentAdded.class).count()).isEqualTo(1);
            });
        }
    }

    @Nested
    @DisplayName("답글(대댓글) 등록")
    class addReply {

        @Test
        @DisplayName("살아있는 부모 댓글에 답글을 등록할 수 있고 이벤트를 발행한다.")
        void success() {
            Member leader = memberRepository.save(createMember("리더"));
            Member replyWriter = memberRepository.save(createMember("답글작성자"));
            Crew crew = createCrew(leader, "우리 크루");
            crew.addCrewMember(createGeneralCrewMember(crew, replyWriter));
            crewRepository.save(crew);
            Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
            SquadComment parent = squadCommentRepository.save(createSquadComment(squad, leader, false));
            clearPersistenceContext();

            squadCommentCommandService.addReply(replyWriter.getId(), squad.getId(), parent.getId(), "답글 내용입니다.");

            assertSoftly(softly -> {
                softly.assertThat(squadCommentRepository.findAll()).hasSize(2);
                softly.assertThat(events.stream(CommentReplyAdded.class).count()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("이미 삭제된 댓글에는 답글을 등록할 수 없다.")
        void failWithDeletedParent() {
            Member leader = memberRepository.save(createMember("리더"));
            Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
            Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
            SquadComment parent = squadCommentRepository.save(createSquadComment(squad, leader, true));
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService.addReply(leader.getId(), squad.getId(), parent.getId(), "답글"));
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class update {

        @Test
        @DisplayName("댓글 작성자는 본인의 댓글 내용을 수정할 수 있다.")
        void success() {
            Member writer = memberRepository.save(createMember("작성자"));
            Crew crew = crewRepository.save(createCrew(writer, "우리 크루"));
            Squad squad = squadRepository.save(createSquad(crew, writer, "신규 스쿼드"));
            SquadComment comment = squadCommentRepository.save(createSquadComment(squad, writer, false));
            clearPersistenceContext();

            squadCommentCommandService.update(writer.getId(), squad.getId(), comment.getId(), "수정된 내용");

            clearPersistenceContext();
            SquadComment updatedComment = squadCommentRepository.findById(comment.getId()).orElseThrow();
            assertThat(updatedComment.getContent()).isEqualTo("수정된 내용");
        }

        @Test
        @DisplayName("작성자가 아닌 다른 사람이 수정을 시도하면 예외가 발생한다.")
        void failWithMismatchWriter() {
            Member writer = memberRepository.save(createMember("작성자"));
            Member stranger = memberRepository.save(createMember("침입자"));
            Crew crew = crewRepository.save(createCrew(writer, "우리 크루"));
            Squad squad = squadRepository.save(createSquad(crew, writer, "신규 스쿼드"));
            SquadComment comment = squadCommentRepository.save(createSquadComment(squad, writer, false));
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService.update(stranger.getId(), squad.getId(), comment.getId(), "수정 시도"));
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class delete {

        @Test
        @DisplayName("스쿼드 리더는 권한을 이용해 다른 사람의 댓글을 삭제(논리 삭제)할 수 있다.")
        void successByLeader() {
            Member leader = memberRepository.save(createMember("리더"));
            Member writer = memberRepository.save(createMember("작성자"));
            Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
            Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
            SquadComment comment = squadCommentRepository.save(createSquadComment(squad, writer, false));
            clearPersistenceContext();

            squadCommentCommandService.delete(leader.getId(), squad.getId(), comment.getId());

            clearPersistenceContext();
            SquadComment deletedComment = squadCommentRepository.findById(comment.getId()).orElseThrow();
            assertThat(deletedComment.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("스쿼드 참여자가 아닌 크루원이 삭제를 시도하면 예외가 발생한다.")
        void failByNonSquadMember() {
            Member leader = memberRepository.save(createMember("리더"));
            Member crewMember = memberRepository.save(createMember("크루원"));
            Crew crew = createCrew(leader, "우리 크루");
            crew.addCrewMember(createGeneralCrewMember(crew, crewMember));
            crewRepository.save(crew);
            Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
            SquadComment comment = squadCommentRepository.save(createSquadComment(squad, leader, false));
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommentCommandService.delete(crewMember.getId(), squad.getId(), comment.getId()));
        }
    }

    private static CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }

    public static SquadComment createSquadComment(Squad squad, Member member, boolean deleted) {
        SquadComment comment = SquadComment.create(UUID.randomUUID().toString(), squad, member);
        ReflectionTestUtils.setField(comment, "deleted", deleted);
        return comment;
    }
}

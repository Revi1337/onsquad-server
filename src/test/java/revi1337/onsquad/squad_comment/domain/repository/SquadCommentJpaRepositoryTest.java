package revi1337.onsquad.squad_comment.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

class SquadCommentJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentRepository;

    @Test
    @DisplayName("스쿼드 정보와 함께 댓글을 식별자로 조회한다. (EntityGraph 확인)")
    void findWithSquadById() {
        Member leader = memberRepository.save(createMember("리더"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
        SquadComment comment = squadCommentRepository.save(createSquadComment(squad, leader, false));
        clearPersistenceContext();

        Optional<SquadComment> result = squadCommentRepository.findWithSquadById(comment.getId());

        assertSoftly(softly -> {
            softly.assertThat(result).isPresent();
            softly.assertThat(result.get().getSquad().getTitle().getValue()).isEqualTo("신규 스쿼드");
        });
    }

    @Test
    @DisplayName("특정 회원이 작성한 모든 댓글을 한 번에 삭제한다.")
    void deleteByMemberId() {
        Member leader = memberRepository.save(createMember("리더"));
        Member writer = memberRepository.save(createMember("작성자"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
        squadCommentRepository.save(createSquadComment(squad, writer, false));
        squadCommentRepository.save(createSquadComment(squad, writer, false));
        squadCommentRepository.save(createSquadComment(squad, leader, false));
        clearPersistenceContext();

        int deletedCount = squadCommentRepository.deleteByMemberId(writer.getId());

        assertSoftly(softly -> {
            softly.assertThat(deletedCount).isEqualTo(2);
            softly.assertThat(squadCommentRepository.findAll()).hasSize(1);
        });
    }

    @Test
    @DisplayName("여러 스쿼드 ID에 속한 모든 댓글을 답글 먼저, 부모 댓글 나중 순서로 삭제한다.")
    void deleteBySquadIdIn() {
        Member leader = memberRepository.save(createMember("리더"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad1 = squadRepository.save(createSquad(crew, leader, "스쿼드1"));
        Squad squad2 = squadRepository.save(createSquad(crew, leader, "스쿼드2"));
        Squad squad3 = squadRepository.save(createSquad(crew, leader, "스쿼드3"));
        SquadComment parent1 = squadCommentRepository.save(createSquadComment(squad1, leader, false));
        squadCommentRepository.save(SquadComment.createReply(parent1, "reply1", squad1, leader));
        squadCommentRepository.save(createSquadComment(squad2, leader, false));
        squadCommentRepository.save(createSquadComment(squad3, leader, false));
        clearPersistenceContext();
        List<Long> targets = List.of(squad1.getId(), squad2.getId());

        int deletedReplies = squadCommentRepository.deleteRepliesBySquadIdIn(targets);
        int deletedParents = squadCommentRepository.deleteParentsBySquadIdIn(targets);

        assertSoftly(softly -> {
            softly.assertThat(deletedReplies).isEqualTo(1);
            softly.assertThat(deletedParents).isEqualTo(2);
            List<SquadComment> remains = squadCommentRepository.findAll();
            softly.assertThat(remains).hasSize(1);
            softly.assertThat(remains.get(0).getSquad().getId()).isEqualTo(squad3.getId());
        });
    }

    @Test
    @DisplayName("답글 삭제는 부모 댓글을 건드리지 않고 답글만 삭제한다.")
    void deleteRepliesBySquadIdIn() {
        Member leader = memberRepository.save(createMember("리더"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "스쿼드"));
        SquadComment parent = squadCommentRepository.save(createSquadComment(squad, leader, false));
        squadCommentRepository.save(SquadComment.createReply(parent, "reply1", squad, leader));
        squadCommentRepository.save(SquadComment.createReply(parent, "reply2", squad, leader));
        clearPersistenceContext();

        int deleted = squadCommentRepository.deleteRepliesBySquadIdIn(List.of(squad.getId()));

        assertSoftly(softly -> {
            softly.assertThat(deleted).isEqualTo(2);
            softly.assertThat(squadCommentRepository.findAll()).extracting(SquadComment::getId).containsExactly(parent.getId());
        });
    }

    @Test
    @DisplayName("특정 회원이 쓴 부모 댓글에 달린 답글의 ID만 조회한다. 답글 작성자는 상관없다.")
    void findReplyIdsByParentWriterId() {
        Member leader = memberRepository.save(createMember("리더"));
        Member writer = memberRepository.save(createMember("작성자"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "스쿼드"));
        SquadComment writerParent = squadCommentRepository.save(SquadComment.create("writer parent", squad, writer));
        SquadComment leaderParent = squadCommentRepository.save(SquadComment.create("leader parent", squad, leader));
        SquadComment replyByLeader = squadCommentRepository.save(SquadComment.createReply(writerParent, "r1", squad, leader));
        SquadComment replyByWriter = squadCommentRepository.save(SquadComment.createReply(writerParent, "r2", squad, writer));
        squadCommentRepository.save(SquadComment.createReply(leaderParent, "r3", squad, writer));
        clearPersistenceContext();

        List<Long> replyIds = squadCommentRepository.findReplyIdsByParentWriterId(writer.getId());

        assertThat(replyIds).containsExactlyInAnyOrder(replyByLeader.getId(), replyByWriter.getId());
    }

    @Test
    @DisplayName("부모 댓글을 쓴 적이 없는 회원은 조회되는 답글 ID가 없다.")
    void findReplyIdsByParentWriterId_whenNoParentComment() {
        Member leader = memberRepository.save(createMember("리더"));
        Member writer = memberRepository.save(createMember("작성자"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "스쿼드"));
        SquadComment leaderParent = squadCommentRepository.save(SquadComment.create("leader parent", squad, leader));
        squadCommentRepository.save(SquadComment.createReply(leaderParent, "r1", squad, writer));
        clearPersistenceContext();

        assertThat(squadCommentRepository.findReplyIdsByParentWriterId(writer.getId())).isEmpty();
    }

    @Test
    @DisplayName("전달한 ID에 해당하는 댓글만 삭제한다.")
    void deleteByIdIn() {
        Member leader = memberRepository.save(createMember("리더"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "스쿼드"));
        SquadComment keep = squadCommentRepository.save(createSquadComment(squad, leader, false));
        SquadComment remove1 = squadCommentRepository.save(createSquadComment(squad, leader, false));
        SquadComment remove2 = squadCommentRepository.save(createSquadComment(squad, leader, false));
        clearPersistenceContext();

        int deleted = squadCommentRepository.deleteByIdIn(List.of(remove1.getId(), remove2.getId()));

        assertSoftly(softly -> {
            softly.assertThat(deleted).isEqualTo(2);
            softly.assertThat(squadCommentRepository.findAll()).extracting(SquadComment::getId).containsExactly(keep.getId());
        });
    }

    public static SquadComment createSquadComment(Squad squad, Member member, boolean deleted) {
        SquadComment comment = SquadComment.create(UUID.randomUUID().toString(), squad, member);
        ReflectionTestUtils.setField(comment, "deleted", deleted);
        return comment;
    }
}

package revi1337.onsquad.squad_comment.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentDetail;

@Import(SquadCommentJdbcRepository.class)
class SquadCommentJdbcRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentRepository;

    @Autowired
    private SquadCommentJdbcRepository squadCommentJdbcRepository;

    @Test
    @DisplayName("각 부모 댓글 ID들에 대해 설정된 개수 제한만큼의 최신 답글 목록을 조회한다.")
    void fetchAllChildrenByParentIdIn() {
        Member leader = memberRepository.save(createMember("리더"));
        Member writer = memberRepository.save(createMember("작성자"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
        SquadComment parent1 = squadCommentRepository.save(createSquadComment(squad, leader, false));
        SquadComment parent2 = squadCommentRepository.save(createSquadComment(squad, leader, false));
        squadCommentRepository.save(createSquadCommentReply(parent1, squad, writer, false));
        squadCommentRepository.save(createSquadCommentReply(parent1, squad, writer, false));
        SquadComment parent1NewestReply = squadCommentRepository.save(createSquadCommentReply(parent1, squad, writer, false));
        SquadComment parent2Reply = squadCommentRepository.save(createSquadCommentReply(parent2, squad, writer, false));
        clearPersistenceContext();
        List<Long> parentIds = List.of(parent1.getId(), parent2.getId());
        int childLimit = 2;

        List<SquadCommentDetail> results = squadCommentJdbcRepository.fetchAllChildrenByParentIdIn(parentIds, childLimit);

        assertSoftly(softly -> {
            softly.assertThat(results).hasSize(3);

            List<SquadCommentDetail> parent1Replies = results.stream()
                    .filter(r -> r.parentId().equals(parent1.getId()))
                    .toList();
            softly.assertThat(parent1Replies).hasSize(2);
            softly.assertThat(parent1Replies.get(0).id()).isEqualTo(parent1NewestReply.getId());

            List<SquadCommentDetail> parent2Replies = results.stream()
                    .filter(r -> r.parentId().equals(parent2.getId()))
                    .toList();
            softly.assertThat(parent2Replies).hasSize(1);
            softly.assertThat(parent2Replies.get(0).id()).isEqualTo(parent2Reply.getId());

            softly.assertThat(results.get(0).writer().nickname().getValue()).isEqualTo("작성자");
        });
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

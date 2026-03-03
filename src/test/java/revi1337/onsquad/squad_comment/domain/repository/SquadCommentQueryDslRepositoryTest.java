package revi1337.onsquad.squad_comment.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

@Import(SquadCommentQueryDslRepository.class)
class SquadCommentQueryDslRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentRepository;

    @Autowired
    private SquadCommentQueryDslRepository squadCommentQueryDslRepository;

    @Test
    @DisplayName("부모 댓글 목록을 조회할 때 createdAt 내림차순으로 정렬되어 반환된다.")
    void fetchAllParentsBySquadId() {
        Member leader = memberRepository.save(createMember("리더"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
        SquadComment oldComment = squadCommentRepository.save(createSquadComment(squad, leader, false));
        SquadComment newComment = squadCommentRepository.save(createSquadComment(squad, leader, false));
        clearPersistenceContext();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Page<SquadComment> result = squadCommentQueryDslRepository.fetchAllParentsBySquadId(squad.getId(), pageRequest);

        assertSoftly(softly -> {
            softly.assertThat(result.getTotalElements()).isEqualTo(2);
            softly.assertThat(result.getContent().get(0).getId()).isEqualTo(newComment.getId());
            softly.assertThat(result.getContent().get(1).getId()).isEqualTo(oldComment.getId());
        });
    }

    @Test
    @DisplayName("답글 목록을 조회할 때 createdAt 내림차순으로 정렬되어 반환된다.")
    void fetchAllChildrenBySquadIdAndParentId() {
        Member leader = memberRepository.save(createMember("리더"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad = squadRepository.save(createSquad(crew, leader, "신규 스쿼드"));
        SquadComment parent = squadCommentRepository.save(createSquadComment(squad, leader, false));
        SquadComment oldReply = squadCommentRepository.save(SquadComment.createReply(parent, "옛날 답글", squad, leader));
        SquadComment newReply = squadCommentRepository.save(SquadComment.createReply(parent, "최신 답글", squad, leader));
        clearPersistenceContext();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Page<SquadComment> result = squadCommentQueryDslRepository.fetchAllChildrenBySquadIdAndParentId(squad.getId(), parent.getId(), pageRequest);

        assertSoftly(softly -> {
            softly.assertThat(result.getTotalElements()).isEqualTo(2);
            softly.assertThat(result.getContent().get(0).getId()).isEqualTo(newReply.getId());
            softly.assertThat(result.getContent().get(1).getId()).isEqualTo(oldReply.getId());
        });
    }

    public static SquadComment createSquadComment(Squad squad, Member member, boolean deleted) {
        SquadComment comment = SquadComment.create(UUID.randomUUID().toString(), squad, member);
        ReflectionTestUtils.setField(comment, "deleted", deleted);
        return comment;
    }
}

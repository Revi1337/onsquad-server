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
    @DisplayName("여러 스쿼드 ID에 속한 모든 댓글을 한 번에 삭제한다.")
    void deleteBySquadIdIn() {
        Member leader = memberRepository.save(createMember("리더"));
        Crew crew = crewRepository.save(createCrew(leader, "우리 크루"));
        Squad squad1 = squadRepository.save(createSquad(crew, leader, "스쿼드1"));
        Squad squad2 = squadRepository.save(createSquad(crew, leader, "스쿼드2"));
        Squad squad3 = squadRepository.save(createSquad(crew, leader, "스쿼드3"));
        squadCommentRepository.save(createSquadComment(squad1, leader, false));
        squadCommentRepository.save(createSquadComment(squad2, leader, false));
        squadCommentRepository.save(createSquadComment(squad3, leader, false));
        clearPersistenceContext();

        int deletedCount = squadCommentRepository.deleteBySquadIdIn(List.of(squad1.getId(), squad2.getId()));

        assertSoftly(softly -> {
            assertThat(deletedCount).isEqualTo(2);
            List<SquadComment> remains = squadCommentRepository.findAll();
            assertThat(remains).hasSize(1);
            assertThat(remains.get(0).getSquad().getId()).isEqualTo(squad3.getId());
        });
    }

    public static SquadComment createSquadComment(Squad squad, Member member, boolean deleted) {
        SquadComment comment = SquadComment.create(UUID.randomUUID().toString(), squad, member);
        ReflectionTestUtils.setField(comment, "deleted", deleted);
        return comment;
    }
}

package revi1337.onsquad.squad_comment.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

@Import(SquadCommentJdbcRepository.class)
class SquadCommentJdbcRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadCommentJdbcRepository squadCommentJdbcRepository;

    @Test
    @DisplayName("부모 댓글마다 특정 개수의 대댓글을 가져오는데 성공한다.")
    void success() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
        SquadComment PARENT1 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
        squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_1", SQUAD, OWNER));
        squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_2", SQUAD, OWNER));
        squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_3", SQUAD, OWNER));
        SquadComment PARENT2 = squadCommentRepository.save(SquadComment.create("parent_2", SQUAD, OWNER));
        squadCommentRepository.save(SquadComment.createReply(PARENT2, "reply_4", SQUAD, OWNER));
        squadCommentRepository.save(SquadComment.createReply(PARENT2, "reply_5", SQUAD, OWNER));
        squadCommentRepository.save(SquadComment.createReply(PARENT2, "reply_6", SQUAD, OWNER));
        int childSize = 2;
        clearPersistenceContext();

        List<SquadCommentResult> children = squadCommentJdbcRepository
                .fetchAllChildrenByParentIdIn(List.of(PARENT1.getId(), PARENT2.getId()), childSize);

        assertThat(children).hasSize(4);
        assertThat(children).extracting("id").containsExactlyInAnyOrder(3L, 4L, 7L, 8L);
    }
}

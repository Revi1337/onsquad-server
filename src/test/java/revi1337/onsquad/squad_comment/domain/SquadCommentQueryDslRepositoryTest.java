package revi1337.onsquad.squad_comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadJpaRepository;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

@Import({SquadCommentQueryDslRepository.class})
class SquadCommentQueryDslRepositoryTest extends PersistenceLayerTestSupport {

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
    private SquadCommentQueryDslRepository squadCommentQueryDslRepository;

    @Nested
    @DisplayName("부모 댓글 조회를 테스트한다.")
    class FetchAllParentsBySquadId {

        @Test
        @DisplayName("부모 댓글 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.create("parent_2", SQUAD, OWNER));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            Crew CREW2 = crewJpaRepository.save(CREW_2(ANDONG));
            CrewMember OWNER2 = crewMemberRepository.findByCrewIdAndMemberId(CREW2.getId(), ANDONG.getId()).get();
            Squad SQUAD2 = squadRepository.save(SQUAD(OWNER2, CREW2));
            squadCommentRepository.save(SquadComment.create("parent_3", SQUAD2, OWNER2));
            SquadComment PARENT_1 = squadCommentRepository.save(SquadComment.create("parent_4", SQUAD2, OWNER2));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 1);
            clearPersistenceContext();

            List<SquadCommentDomainDto> parents = squadCommentQueryDslRepository.fetchAllParentsBySquadId(SQUAD2.getId(), PAGE_REQUEST);

            assertThat(parents).hasSize(1);
            assertThat(parents.get(0).content()).isEqualTo("parent_4");
        }
    }

    @Nested
    @DisplayName("대댓글 조회를 테스트한다.")
    class fetchAllChildrenBySquadIdAndParentId {

        @Test
        @DisplayName("대댓글 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT1 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_2", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_3", SQUAD, OWNER));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 2);
            clearPersistenceContext();

            List<SquadCommentDomainDto> chilren = squadCommentQueryDslRepository
                    .fetchAllChildrenBySquadIdAndParentId(SQUAD.getId(), PARENT1.getId(), PAGE_REQUEST);

            assertThat(chilren).hasSize(2);
            assertThat(chilren.get(0).content()).isEqualTo("reply_3");
            assertThat(chilren.get(1).content()).isEqualTo("reply_2");
        }
    }

    @Nested
    @DisplayName("모든 댓글 조회를 테스트한다.")
    class FindAllWithMemberBySquadId {

        @Test
        @DisplayName("모든 댓글 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(OWNER, CREW));
            SquadComment PARENT1 = squadCommentRepository.save(SquadComment.create("parent_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_1", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_2", SQUAD, OWNER));
            squadCommentRepository.save(SquadComment.createReply(PARENT1, "reply_3", SQUAD, OWNER));
            clearPersistenceContext();

            List<SquadCommentDomainDto> chilren = squadCommentQueryDslRepository
                    .findAllWithMemberBySquadId(SQUAD.getId());

            assertThat(chilren).hasSize(4);
        }
    }
}
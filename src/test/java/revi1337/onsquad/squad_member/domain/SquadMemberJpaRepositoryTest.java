package revi1337.onsquad.squad_member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadJpaRepository;

class SquadMemberJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberJpaRepository;

    @Nested
    @DisplayName("findBySquadIdAndCrewMemberId 를 테스트한다.")
    class FindBySquadIdAndCrewMemberId {

        @Test
        @DisplayName("SquadId 와 CrewMemberId 로 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW1));

            assertThat(squadMemberJpaRepository
                    .findBySquadIdAndCrewMemberId(SQUAD.getId(), CREW_OWNER.getId()))
                    .isPresent();
        }

        @Test
        @DisplayName("SquadId 와 CrewMemberId 로 조회에 실패한다.")
        void fail() {
            Long SQUAD_ID = 1L;
            Long CREW_MEMBER_ID = 2L;

            assertThat(squadMemberJpaRepository.findBySquadIdAndCrewMemberId(SQUAD_ID, CREW_MEMBER_ID))
                    .isEmpty();
        }
    }
}

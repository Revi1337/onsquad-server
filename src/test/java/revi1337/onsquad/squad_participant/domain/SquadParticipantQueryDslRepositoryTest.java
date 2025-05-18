package revi1337.onsquad.squad_participant.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadParticipantFixture.SQUAD_PARTICIPANT;

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
import revi1337.onsquad.squad_participant.domain.dto.SimpleSquadParticipantDomainDto;

@Import(SquadParticipantQueryDslRepository.class)
class SquadParticipantQueryDslRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadParticipantJpaRepository squadParticipantJpaRepository;

    @Autowired
    private SquadParticipantQueryDslRepository squadParticipantQueryDslRepository;

    @Nested
    @DisplayName("스쿼드 참가신청 목록 조회를 테스트한다.")
    class FetchAllBySquadId {

        @Test
        @DisplayName("스쿼드 참가신청 목록 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGON = memberJpaRepository.save(KWANGWON());
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            CrewMember CREW_MEMBER1 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            CrewMember CREW_MEMBER2 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, KWANGON));
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
            squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER1));
            squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER2));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 2);

            List<SimpleSquadParticipantDomainDto> RESULTS = squadParticipantQueryDslRepository
                    .fetchAllBySquadId(SQUAD.getId(), PAGE_REQUEST)
                    .getContent();

            assertThat(RESULTS).hasSize(2);
            assertThat(RESULTS.get(0).member().nickname()).isEqualTo(KWANGON.getNickname());
            assertThat(RESULTS.get(1).member().nickname()).isEqualTo(ANDONG.getNickname());
        }
    }
}

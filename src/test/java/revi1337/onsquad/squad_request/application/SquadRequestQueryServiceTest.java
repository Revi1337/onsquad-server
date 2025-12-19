package revi1337.onsquad.squad_request.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.GENERAL_SQUAD_MEMBER;
import static revi1337.onsquad.common.fixture.SquadParticipantFixture.SQUAD_PARTICIPANT;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_request.application.response.SquadRequestResponse;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestJpaRepository;

class SquadRequestQueryServiceTest extends ApplicationLayerTestSupport {

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

    @Autowired
    private SquadRequestJpaRepository squadRequestJpaRepository;

    @Autowired
    private SquadRequestQueryService squadRequestQueryService;

    @Nested
    @DisplayName("스쿼드 참가신청 목록 조회를 테스트한다.")
    class FetchAllRequests {

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
            squadRequestJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER1));
            squadRequestJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER2));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 2);

            List<SquadRequestResponse> RESULTS = squadRequestQueryService
                    .fetchAllRequests(REVI.getId(), CREW.getId(), SQUAD.getId(), PAGE_REQUEST);

            assertThat(RESULTS).hasSize(2);
            assertThat(RESULTS.get(0).member().nickname()).isEqualTo(KWANGON.getNickname().getValue());
            assertThat(RESULTS.get(1).member().nickname()).isEqualTo(ANDONG.getNickname().getValue());
        }

        @Test
        @DisplayName("스쿼드 리더가 아니면 스쿼드 참가신청 목록 조회에 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            CrewMember CREW_MEMBER1 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
            squadMemberJpaRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, CREW_MEMBER1));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 2);

            assertThatThrownBy(() -> squadRequestQueryService
                    .fetchAllRequests(ANDONG.getId(), CREW.getId(), SQUAD.getId(), PAGE_REQUEST))
                    .isExactlyInstanceOf(SquadMemberBusinessException.NotLeader.class);
        }
    }
}

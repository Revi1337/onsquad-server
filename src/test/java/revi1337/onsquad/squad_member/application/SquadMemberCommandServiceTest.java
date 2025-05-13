package revi1337.onsquad.squad_member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixtures.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.GENERAL_SQUAD_MEMBER;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadJpaRepository;
import revi1337.onsquad.squad_member.domain.SquadMemberJpaRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;

class SquadMemberCommandServiceTest extends ApplicationLayerTestSupport {

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
    private SquadMemberCommandService squadMemberCommandService;

    @Nested
    @DisplayName("SquadMember 의 Squad 탈퇴를 테스트한다.")
    class Leave {

        @Test
        @DisplayName("SquadMember 의 Squad 탈퇴에 성공한다.")
        void success1() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_MEMBER1 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            squadMemberJpaRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, CREW_MEMBER1));
            clearPersistenceContext();

            // when
            squadMemberCommandService.leaveSquad(ANDONG.getId(), CREW.getId(), SQUAD.getId());
            entityManager.flush();

            // then
            assertThat(squadMemberJpaRepository.findBySquadIdAndCrewMemberId(SQUAD.getId(), CREW_MEMBER1.getId()))
                    .isEmpty();
        }

        @Test
        @DisplayName("SquadMember 가 Leader 이고 현재 Squad 잔류 멤버가 1명이면 탈퇴에 성공한다.")
        void success2() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
            clearPersistenceContext();

            // when
            squadMemberCommandService.leaveSquad(REVI.getId(), CREW.getId(), SQUAD.getId());
            entityManager.flush();

            // then
            assertThat(squadMemberJpaRepository.findBySquadIdAndCrewMemberId(SQUAD.getId(), CREW_OWNER.getId()))
                    .isEmpty();
        }

        @Test
        @DisplayName("SquadMember 가 Leader 이고 현재 Squad 잔류 멤버가 2명 이상이면 탈퇴에 실패한다.")
        void fail() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_MEMBER1 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            squadMemberJpaRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, CREW_MEMBER1));
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> squadMemberCommandService.leaveSquad(REVI.getId(), CREW.getId(), SQUAD.getId()))
                    .isExactlyInstanceOf(SquadMemberBusinessException.CannotLeaveLeader.class);
        }
    }
}

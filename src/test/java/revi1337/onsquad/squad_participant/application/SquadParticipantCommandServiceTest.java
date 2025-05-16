package revi1337.onsquad.squad_participant.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixtures.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixtures.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.GENERAL_SQUAD_MEMBER;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.LEADER_SQUAD_MEMBER;
import static revi1337.onsquad.common.fixture.SquadParticipantFixture.SQUAD_PARTICIPANT;

import java.time.LocalDateTime;
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
import revi1337.onsquad.squad.application.dto.SquadAcceptDto;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadJpaRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad.error.exception.SquadBusinessException.MismatchReference;
import revi1337.onsquad.squad.error.exception.SquadDomainException;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_member.domain.SquadMemberJpaRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_participant.domain.SquadParticipant;
import revi1337.onsquad.squad_participant.domain.SquadParticipantJpaRepository;

class SquadParticipantCommandServiceTest extends ApplicationLayerTestSupport {

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
    private SquadMemberJpaRepository squadMemberJpaRepository;

    @Autowired
    private SquadParticipantCommandService commandService;

    @Nested
    @DisplayName("스쿼드 참가 신청을 테스트한다.")
    class Request {

        @Test
        @DisplayName("스쿼드 참가 신청에 성공한다.")
        void success1() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            clearPersistenceContext();

            // when
            commandService.request(ANDONG.getId(), CREW.getId(), SQUAD.getId());

            // then
            assertThat(squadParticipantJpaRepository.findBySquadIdAndCrewMemberId(SQUAD.getId(), CREW_MEMBER.getId()))
                    .isPresent();
        }

        @Test
        @DisplayName("이미 신청한적 있으면 업데이트 된다.")
        void success2() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            LocalDateTime NOW = LocalDateTime.now();
            squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER, NOW));
            clearPersistenceContext();

            // when
            commandService.request(ANDONG.getId(), CREW.getId(), SQUAD.getId());

            // then
            assertThat(squadParticipantJpaRepository
                    .findBySquadIdAndCrewMemberId(SQUAD.getId(), CREW_MEMBER.getId()).get().getRequestAt())
                    .isNotEqualTo(NOW);
        }

        @Test
        @DisplayName("스쿼드가 속한 크루정보가 일치하지 않으면 실패한다.")
        void fail1() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            Crew CREW2 = crewJpaRepository.save(CREW_2(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW1));
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> commandService.request(REVI.getId(), CREW2.getId(), SQUAD.getId()))
                    .isExactlyInstanceOf(MismatchReference.class);
        }

        @Test
        @DisplayName("스쿼드에 이미 속한 사용자는 실패한다.")
        void fail2() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW_1(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> commandService.request(REVI.getId(), CREW.getId(), SQUAD.getId()))
                    .isExactlyInstanceOf(SquadBusinessException.AlreadyParticipant.class);
        }
    }

    @Nested
    @DisplayName("스쿼드 참가 신청 수락을 테스트한다.")
    class Accept {

        @Test
        @DisplayName("스쿼드 참가 신청 수락에 성공한다.")
        void success() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            clearPersistenceContext();

            // when
            commandService.request(ANDONG.getId(), CREW.getId(), SQUAD.getId());

            // then
            assertThat(squadParticipantJpaRepository.findBySquadIdAndCrewMemberId(SQUAD.getId(), CREW_MEMBER.getId()))
                    .isPresent();
        }

        @Test
        @DisplayName("수락하는 사용자가 스쿼드 리더가 아니면 실패한다.")
        void fail1() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            squadMemberJpaRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, CREW_MEMBER));

            SquadAcceptDto ACCEPT_DTO = new SquadAcceptDto(ANDONG.getId());
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> commandService
                    .acceptRequest(ANDONG.getId(), CREW.getId(), SQUAD.getId(), ACCEPT_DTO))
                    .isExactlyInstanceOf(SquadMemberBusinessException.NotLeader.class);
        }

        @Test
        @DisplayName("스쿼드가 속한 크루정보가 일치하지 않으면 실패한다.")
        void fail2() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            CrewMember CREW1_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            Squad SQUAD1 = squadJpaRepository.save(SQUAD(CREW1_OWNER, CREW1));

            Crew CREW2 = crewJpaRepository.save(CREW_2(REVI));
            CrewMember CREW2_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW2.getId(), REVI.getId()).get();
            squadMemberJpaRepository.save(LEADER_SQUAD_MEMBER(SQUAD1, CREW2_OWNER));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW2_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW2, ANDONG));
            SquadAcceptDto ACCEPT_DTO = new SquadAcceptDto(ANDONG.getId());
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> commandService
                    .acceptRequest(REVI.getId(), CREW2.getId(), SQUAD1.getId(), ACCEPT_DTO))
                    .isExactlyInstanceOf(MismatchReference.class);
        }

        @Test
        @DisplayName("스쿼드 정원이 다차면 실패한다.")
        void fail3() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad PRE_SQUAD = SQUAD(CREW_OWNER, CREW, 2);

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_MEMBER1 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            PRE_SQUAD.addMembers(GENERAL_SQUAD_MEMBER(CREW_MEMBER1));
            Squad SQUAD = squadJpaRepository.save(PRE_SQUAD);

            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, KWANGWON));
            SquadAcceptDto ACCEPT_DTO = new SquadAcceptDto(KWANGWON.getId());
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> commandService
                    .acceptRequest(REVI.getId(), CREW.getId(), SQUAD.getId(), ACCEPT_DTO))
                    .isExactlyInstanceOf(SquadDomainException.NotEnoughLeft.class);
        }
    }

    @Nested
    @DisplayName("스쿼드 참가 신청 거절을 테스트한다.")
    class Reject {

        @Test
        @DisplayName("스쿼드 참가 신청 거절을 성공한다.")
        void success() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadParticipant REQUEST = squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER));
            clearPersistenceContext();

            // when
            commandService.rejectRequest(REVI.getId(), CREW.getId(), SQUAD.getId(), REQUEST.getId());

            // then
            assertThat(squadParticipantJpaRepository.findBySquadIdAndCrewMemberId(SQUAD.getId(), CREW_MEMBER.getId()))
                    .isEmpty();
        }

        @Test
        @DisplayName("거절하는 사용자가 스쿼드 리더가 아니면 실패한다.")
        void fail1() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW_1(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadMember SQUAD_MEMBER = squadMemberJpaRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, CREW_MEMBER));

            Long TEST_REQUEST_ID = 1L;
            clearPersistenceContext();

            // given & then
            assertThatThrownBy(() -> commandService
                    .rejectRequest(ANDONG.getId(), CREW.getId(), SQUAD.getId(), TEST_REQUEST_ID))
                    .isExactlyInstanceOf(SquadMemberBusinessException.NotLeader.class);
        }

        /**
         * 이 테스트는 도메인 상 불가능한 상황을 일부러 구성한 것으로, Squad 가 속한 Crew 와 리더의 Crew 가 다를 때 방어적 예외를 테스트한다.
         */
        @Test
        @DisplayName("스쿼드가 속한 크루정보가 일치하지 않으면 실패한다.")
        void fail2() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            CrewMember CREW1_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            Squad SQUAD1 = squadJpaRepository.save(SQUAD(CREW1_OWNER, CREW1));

            Crew CREW2 = crewJpaRepository.save(CREW_2(REVI));
            CrewMember CREW2_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW2.getId(), REVI.getId()).get();
            Squad SQUAD2 = squadJpaRepository.save(SQUAD(CREW2_OWNER, CREW2));

            squadMemberJpaRepository.save(LEADER_SQUAD_MEMBER(SQUAD1, CREW2_OWNER));

            Long TEST_REQUEST_ID = 1L;
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> commandService
                    .rejectRequest(REVI.getId(), CREW2.getId(), SQUAD1.getId(), TEST_REQUEST_ID))
                    .isExactlyInstanceOf(MismatchReference.class);
        }
    }

    @Nested
    @DisplayName("내가 신청한 스쿼드 신청 취소를 테스트한다.")
    class Cancel {

        @Test
        @DisplayName("내가 신청한 스쿼드 신청 취소에 성공한다.")
        void success() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER));
            clearPersistenceContext();

            // when
            commandService.cancelMyRequest(ANDONG.getId(), CREW.getId(), SQUAD.getId());

            assertThat(squadParticipantJpaRepository.findBySquadIdAndCrewMemberId(SQUAD.getId(), CREW_MEMBER.getId()))
                    .isEmpty();
        }
    }
}

package revi1337.onsquad.squad_participant.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.GENERAL_SQUAD_MEMBER;
import static revi1337.onsquad.common.fixture.SquadParticipantFixture.SQUAD_PARTICIPANT;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad.error.exception.SquadBusinessException.MismatchReference;
import revi1337.onsquad.squad.error.exception.SquadDomainException;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_participant.domain.entity.SquadParticipant;
import revi1337.onsquad.squad_participant.domain.repository.SquadParticipantJpaRepository;
import revi1337.onsquad.squad_participant.error.exception.SquadParticipantBusinessException;

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
            SquadParticipant PARTICIPANT = squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER));
            clearPersistenceContext();

            // when
            commandService.acceptRequest(REVI.getId(), CREW.getId(), SQUAD.getId(), PARTICIPANT.getId());
            clearPersistenceContext();
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
            Long REQUEST_ID = 1L;
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> commandService
                    .acceptRequest(ANDONG.getId(), CREW.getId(), SQUAD.getId(), REQUEST_ID))
                    .isExactlyInstanceOf(SquadMemberBusinessException.NotLeader.class);
        }

        @Test
        @DisplayName("신청정보와 스쿼드정보가 일치하지 않으면 실패한다.")
        void fail2() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER1 = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            Squad SQUAD1 = squadJpaRepository.save(SQUAD(OWNER1, CREW1));

            Member ANDONG = memberJpaRepository.save(ANDONG());
            Crew CREW2 = crewJpaRepository.save(CREW_2(ANDONG));
            CrewMember OWNER2 = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW2.getId(), ANDONG.getId()).get();
            Squad SQUAD2 = squadJpaRepository.save(SQUAD(OWNER2, CREW2));

            SquadParticipant PARTICIPANT = squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD2, OWNER2));
            clearPersistenceContext();

            assertThatThrownBy(() -> commandService
                    .acceptRequest(REVI.getId(), CREW1.getId(), SQUAD1.getId(), PARTICIPANT.getId()))
                    .isExactlyInstanceOf(SquadParticipantBusinessException.MismatchReference.class);
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
            CrewMember CREW_MEMBER2 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, KWANGWON));
            SquadParticipant PARTICIPANT = squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER2));
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> commandService
                    .acceptRequest(REVI.getId(), CREW.getId(), SQUAD.getId(), PARTICIPANT.getId()))
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

        @Test
        @DisplayName("신청정보와 스쿼드정보가 일치하지 않으면 실패한다.")
        void fail2() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            CrewMember CREW1_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            Squad SQUAD1 = squadJpaRepository.save(SQUAD(CREW1_OWNER, CREW1));

            Member ANDO = memberJpaRepository.save(ANDONG());
            Crew CREW2 = crewJpaRepository.save(CREW_2(ANDO));
            CrewMember CREW2_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW2.getId(), ANDO.getId()).get();
            Squad SQUAD2 = squadJpaRepository.save(SQUAD(CREW2_OWNER, CREW2));

            SquadParticipant PARTICIPANT = squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD2, CREW2_OWNER));
            clearPersistenceContext();

            assertThatThrownBy(() -> commandService
                    .rejectRequest(REVI.getId(), CREW1.getId(), SQUAD1.getId(), PARTICIPANT.getId()))
                    .isExactlyInstanceOf(SquadParticipantBusinessException.MismatchReference.class);
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

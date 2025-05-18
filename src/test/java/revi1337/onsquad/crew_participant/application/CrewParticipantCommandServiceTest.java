package revi1337.onsquad.crew_participant.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.CrewParticipantFixture.CREW_PARTICIPANT;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.aspect.ExpiredMapRequestCacheHandler;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.error.exception.CommonBusinessException;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

@MockBean(ThrottlingAspect.class)
class CrewParticipantCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private CrewParticipantRepository crewParticipantRepository;

    @Autowired
    private CrewParticipantCommandService crewParticipantCommandService;

    @Nested
    @DisplayName("Crew 참가신청을 테스트한다.")
    class RequestInCrew {

        @Test
        @DisplayName("Crew 개설자 이외의 사용자가 Crew 참가신청에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            clearPersistenceContext();

            crewParticipantCommandService.request(ANDONG.getId(), CREW.getId());

            assertThat(crewParticipantRepository.findByCrewIdAndMemberId(CREW.getId(), ANDONG.getId())).isPresent();
        }

        @Test
        @DisplayName("동일한 Crew 참가신청 방지에 성공한다.")
        void success2() {
            // given
            AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(crewParticipantCommandService);
            aspectJProxyFactory.setProxyTargetClass(true);
            aspectJProxyFactory.addAspect(new ThrottlingAspect(new ExpiredMapRequestCacheHandler()));
            CrewParticipantCommandService proxyService = aspectJProxyFactory.getProxy();
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> {
                proxyService.request(ANDONG.getId(), CREW.getId());
                proxyService.request(ANDONG.getId(), CREW.getId());
            }).isExactlyInstanceOf(CommonBusinessException.RequestConflict.class);
        }

        @Test
        @DisplayName("Crew 개설자는 자신의 Crew 에 참가신청할 수 없다.")
        void fail1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewParticipantCommandService.request(REVI.getId(), CREW.getId()))
                    .isExactlyInstanceOf(CrewBusinessException.OwnerCantParticipant.class);
        }

        @Test
        @DisplayName("이미 Crew 에 가입되어있으면 참가신청할 수 없다.")
        void fail2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewParticipantCommandService.request(ANDONG.getId(), CREW.getId()))
                    .isExactlyInstanceOf(CrewBusinessException.AlreadyJoin.class);
        }
    }

    @Nested
    @DisplayName("Crew 참가신청 수락을 테스트한다.")
    class AcceptCrewRequest {

        @Test
        @DisplayName("Crew 참가신청 수락에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewParticipant PARTICIPANT = crewParticipantRepository.save(CREW_PARTICIPANT(CREW, ANDONG));
            clearPersistenceContext();

            crewParticipantCommandService.acceptRequest(REVI.getId(), CREW.getId(), PARTICIPANT.getId());

            assertThat(crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), ANDONG.getId())).isPresent();
            assertThat(crewParticipantRepository.findByCrewIdAndMemberId(CREW.getId(), ANDONG.getId())).isEmpty();
        }

        @Test
        @DisplayName("Crew Owner 가 아니라면, Crew 참가 신청 수락에 실패한다.")
        void fail1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            CrewParticipant PARTICIPANT = crewParticipantRepository.save(CREW_PARTICIPANT(CREW, KWANGWON));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewParticipantCommandService
                    .acceptRequest(ANDONG.getId(), CREW.getId(), PARTICIPANT.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotOwner.class);
        }

        @Test
        @DisplayName("신청정보가 존재하지만, Crew 정보가 다르면 참가 신청 수락에 실패한다.")
        void fail2() {
            Member REVI = memberJpaRepository.save(REVI());
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            Crew CREW1 = crewJpaRepository.save(CREW_1(KWANGWON));
            Crew CREW2 = crewJpaRepository.save(CREW_2(REVI));
            CrewParticipant REQUEST1 = crewParticipantRepository.save(CREW_PARTICIPANT(CREW1, ANDONG));
            crewParticipantRepository.save(CREW_PARTICIPANT(CREW2, ANDONG));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewParticipantCommandService
                    .acceptRequest(REVI.getId(), CREW2.getId(), REQUEST1.getId()))
                    .isExactlyInstanceOf(CrewParticipantBusinessException.InvalidReference.class);
        }

        @Test
        @DisplayName("이미 Crew 에 가입된 사용자라면 Crew 참가 신청 수락에 실패한다.")
        void fail3() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            CrewParticipant PARTICIPANT = crewParticipantRepository.save(CREW_PARTICIPANT(CREW, ANDONG));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewParticipantCommandService
                    .acceptRequest(REVI.getId(), CREW.getId(), PARTICIPANT.getId()))
                    .isExactlyInstanceOf(CrewBusinessException.AlreadyJoin.class);
        }
    }

    @Nested
    @DisplayName("Crew 참가 신청 거절을 테스트한다.")
    class RejectCrewRequest {

        @Test
        @DisplayName("Crew 참가 신청 거절에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewParticipant REQUEST1 = crewParticipantRepository.save(CREW_PARTICIPANT(CREW, ANDONG));
            clearPersistenceContext();

            crewParticipantCommandService.rejectRequest(REVI.getId(), CREW.getId(), REQUEST1.getId());

            assertThat(crewParticipantRepository.findById(REQUEST1.getId())).isEmpty();
        }

        @Test
        @DisplayName("Crew Owner 가 아니라면, Crew 참가 신청 거절에 실패한다.")
        void fail1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            clearPersistenceContext();
            Long TEST_REQUEST_ID = 3L;

            assertThatThrownBy(() -> crewParticipantCommandService
                    .rejectRequest(ANDONG.getId(), CREW.getId(), TEST_REQUEST_ID))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotOwner.class);
        }

        @Test
        @DisplayName("신청정보가 존재하지만, Crew 정보가 다르면 참가 신청 거절에 실패한다.")
        void fail2() {
            Member REVI = memberJpaRepository.save(REVI());
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            Crew CREW1 = crewJpaRepository.save(CREW_1(KWANGWON));
            Crew CREW2 = crewJpaRepository.save(CREW_2(REVI));
            CrewParticipant REQUEST1 = crewParticipantRepository.save(CREW_PARTICIPANT(CREW1, ANDONG));
            crewParticipantRepository.save(CREW_PARTICIPANT(CREW2, ANDONG));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewParticipantCommandService
                    .rejectRequest(REVI.getId(), CREW2.getId(), REQUEST1.getId()))
                    .isExactlyInstanceOf(CrewParticipantBusinessException.InvalidReference.class);
        }
    }

    @Nested
    @DisplayName("내가 신청한 Crew 취소를 테스트한다.")
    class CancelCrewRequest {

        @Test
        @DisplayName("내가 신청한 Crew 취소에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewParticipantRepository.save(CREW_PARTICIPANT(CREW, ANDONG));
            clearPersistenceContext();

            crewParticipantCommandService.cancelMyRequest(ANDONG.getId(), CREW.getId());

            assertThat(crewParticipantRepository.findByCrewIdAndMemberId(CREW.getId(), ANDONG.getId())).isEmpty();
        }

        @Test
        @DisplayName("Crew 에 신청한 요청이 없으면 참가 신청 취소에 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());

            assertThatThrownBy(() -> crewParticipantCommandService.cancelMyRequest(ANDONG.getId(), CREW.getId()))
                    .isExactlyInstanceOf(CrewParticipantBusinessException.NeverRequested.class);
        }
    }
}

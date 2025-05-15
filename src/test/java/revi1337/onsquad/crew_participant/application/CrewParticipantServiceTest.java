package revi1337.onsquad.crew_participant.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.CrewParticipantFixture.CREW_PARTICIPANT;
import static revi1337.onsquad.common.fixture.MemberFixtures.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixtures.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.aspect.ExpiredMapRequestCacheHandler;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.error.exception.CommonBusinessException;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithCrewDto;
import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithMemberDto;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

@MockBean(ThrottlingAspect.class)
class CrewParticipantServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private CrewParticipantRepository crewParticipantRepository;

    @Autowired
    private CrewParticipantService crewParticipantService;

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

            crewParticipantService.requestInCrew(ANDONG.getId(), CREW.getId());

            assertThat(crewParticipantRepository.findByCrewIdAndMemberId(CREW.getId(), ANDONG.getId())).isPresent();
        }

        @Test
        @DisplayName("동일한 Crew 참가신청 방지에 성공한다.")
        void success2() {
            // given
            AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(crewParticipantService);
            aspectJProxyFactory.setProxyTargetClass(true);
            aspectJProxyFactory.addAspect(new ThrottlingAspect(new ExpiredMapRequestCacheHandler()));
            CrewParticipantService proxyService = aspectJProxyFactory.getProxy();
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            clearPersistenceContext();

            // when & then
            assertThatThrownBy(() -> {
                proxyService.requestInCrew(ANDONG.getId(), CREW.getId());
                proxyService.requestInCrew(ANDONG.getId(), CREW.getId());
            }).isExactlyInstanceOf(CommonBusinessException.RequestConflict.class);
        }

        @Test
        @DisplayName("Crew 개설자는 자신의 Crew 에 참가신청할 수 없다.")
        void fail1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewParticipantService.requestInCrew(REVI.getId(), CREW.getId()))
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

            assertThatThrownBy(() -> crewParticipantService.requestInCrew(ANDONG.getId(), CREW.getId()))
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

            crewParticipantService.acceptCrewRequest(REVI.getId(), CREW.getId(), PARTICIPANT.getId());

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

            assertThatThrownBy(() -> crewParticipantService
                    .acceptCrewRequest(ANDONG.getId(), CREW.getId(), PARTICIPANT.getId()))
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

            assertThatThrownBy(() -> crewParticipantService
                    .acceptCrewRequest(REVI.getId(), CREW2.getId(), REQUEST1.getId()))
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

            assertThatThrownBy(() -> crewParticipantService
                    .acceptCrewRequest(REVI.getId(), CREW.getId(), PARTICIPANT.getId()))
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

            crewParticipantService.rejectCrewRequest(REVI.getId(), CREW.getId(), REQUEST1.getId());

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

            assertThatThrownBy(() -> crewParticipantService
                    .rejectCrewRequest(ANDONG.getId(), CREW.getId(), TEST_REQUEST_ID))
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

            assertThatThrownBy(() -> crewParticipantService
                    .rejectCrewRequest(REVI.getId(), CREW2.getId(), REQUEST1.getId()))
                    .isExactlyInstanceOf(CrewParticipantBusinessException.InvalidReference.class);
        }
    }

    @Nested
    @DisplayName("특정 Crew 의 참가신청 목록 조회를 테스트한다.")
    class FetchCrewRequests {

        @Test
        @DisplayName("특정 Crew 의 참가신청 목록 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW_1(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            LocalDateTime NOW = LocalDateTime.now();
            crewParticipantRepository.save(CREW_PARTICIPANT(CREW, ANDONG, NOW));
            crewParticipantRepository.save(CREW_PARTICIPANT(CREW, KWANGWON, NOW.plusHours(1)));
            clearPersistenceContext();

            List<CrewRequestWithMemberDto> REQUESTS = crewParticipantService
                    .fetchCrewRequests(REVI.getId(), CREW.getId(), PageRequest.of(0, 3));

            assertAll(() -> {
                assertThat(REQUESTS).hasSize(2);
                assertThat(REQUESTS.get(0).member().id()).isEqualTo(KWANGWON.getId());
                assertThat(REQUESTS.get(0).member().nickname()).isEqualTo(KWANGWON_NICKNAME_VALUE);
                assertThat(REQUESTS.get(0).member().mbti()).isSameAs(KWANGWON_MBTI_VALUE);

                assertThat(REQUESTS.get(0).request().id()).isEqualTo(2L);
                assertThat(REQUESTS.get(0).request().requestAt()).isEqualTo(NOW.plusHours(1));
            });
        }

        @Test
        @DisplayName("Crew 에 속해 있지 않으면, Crew 참가신청 목록 조회에 실패한다.")
        void fail1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW_1(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            LocalDateTime NOW = LocalDateTime.now();
            crewParticipantRepository.save(CREW_PARTICIPANT(CREW, ANDONG, NOW));
            crewParticipantRepository.save(CREW_PARTICIPANT(CREW, KWANGWON, NOW.plusHours(1)));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewParticipantService
                    .fetchCrewRequests(ANDONG.getId(), CREW.getId(), PageRequest.of(0, 3)))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotParticipant.class);
        }

        @Test
        @DisplayName("Crew 작성자가 아니면, Crew 참가신청 목록 조회에 실패한다.")
        void fail2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW_1(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            clearPersistenceContext();

            assertThatThrownBy(() -> crewParticipantService
                    .fetchCrewRequests(ANDONG.getId(), CREW.getId(), PageRequest.of(0, 3)))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotOwner.class);
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

            crewParticipantService.cancelCrewRequest(ANDONG.getId(), CREW.getId());

            assertThat(crewParticipantRepository.findByCrewIdAndMemberId(CREW.getId(), ANDONG.getId())).isEmpty();
        }

        @Test
        @DisplayName("Crew 에 신청한 요청이 없으면 참가 신청 취소에 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());

            assertThatThrownBy(() -> crewParticipantService.cancelCrewRequest(ANDONG.getId(), CREW.getId()))
                    .isExactlyInstanceOf(CrewParticipantBusinessException.NeverRequested.class);
        }
    }

    @Nested
    @DisplayName("내가 보낸 Crew 신청들을 조회한다.")
    class FetchAllCrewRequests {

        @Test
        @DisplayName("내가 보낸 Crew 신청들을 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            Crew CREW1 = crewJpaRepository.save(CREW_1(KWANGWON));
            Crew CREW2 = crewJpaRepository.save(CREW_2(REVI));
            LocalDateTime NOW = LocalDateTime.now();
            crewParticipantRepository.save(CREW_PARTICIPANT(CREW1, ANDONG, NOW));
            crewParticipantRepository.save(CREW_PARTICIPANT(CREW1, REVI, NOW.plusHours(1)));
            crewParticipantRepository.save(CREW_PARTICIPANT(CREW2, ANDONG, NOW.plusHours(2)));
            clearPersistenceContext();

            List<CrewRequestWithCrewDto> REQUESTS = crewParticipantService.fetchAllCrewRequests(ANDONG.getId());

            assertAll(() -> {
                assertThat(REQUESTS).hasSize(2);

                assertThat(REQUESTS.get(0).crew().id()).isEqualTo(2);
                assertThat(REQUESTS.get(0).crew().name()).isEqualTo(CREW2.getName().getValue());
                assertThat(REQUESTS.get(0).crew().introduce()).isEqualTo(CREW2.getIntroduce().getValue());
                assertThat(REQUESTS.get(0).crew().kakaoLink()).isEqualTo(CREW2.getKakaoLink());
                assertThat(REQUESTS.get(0).crew().imageUrl()).isBlank();
                assertThat(REQUESTS.get(0).crew().owner().id()).isEqualTo(1);
                assertThat(REQUESTS.get(0).crew().owner().nickname()).isEqualTo(REVI_NICKNAME_VALUE);
                assertThat(REQUESTS.get(0).crew().owner().mbti()).isEqualTo(REVI_MBTI_VALUE);
                assertThat(REQUESTS.get(0).request().id()).isEqualTo(3);
                assertThat(REQUESTS.get(0).request().requestAt()).isEqualTo(NOW.plusHours(2));

                assertThat(REQUESTS.get(1).crew().id()).isEqualTo(1);
                assertThat(REQUESTS.get(1).crew().name()).isEqualTo(CREW1.getName().getValue());
                assertThat(REQUESTS.get(1).crew().introduce()).isEqualTo(CREW1.getIntroduce().getValue());
                assertThat(REQUESTS.get(1).crew().kakaoLink()).isEqualTo(CREW1.getKakaoLink());
                assertThat(REQUESTS.get(1).crew().imageUrl()).isBlank();
                assertThat(REQUESTS.get(1).crew().owner().id()).isEqualTo(3);
                assertThat(REQUESTS.get(1).crew().owner().nickname()).isEqualTo(KWANGWON_NICKNAME_VALUE);
                assertThat(REQUESTS.get(1).crew().owner().mbti()).isEqualTo(KWANGWON_MBTI_VALUE);
                assertThat(REQUESTS.get(1).request().id()).isEqualTo(1);
                assertThat(REQUESTS.get(1).request().requestAt()).isEqualTo(NOW);
            });
        }
    }
}

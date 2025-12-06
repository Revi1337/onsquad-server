package revi1337.onsquad.crew_participant.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.CrewParticipantFixture.CREW_PARTICIPANT;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithCrewDto;
import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithMemberDto;
import revi1337.onsquad.crew_participant.domain.repository.CrewParticipantRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewParticipantQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private CrewParticipantRepository crewParticipantRepository;

    @Autowired
    private CrewParticipantQueryService crewParticipantQueryService;

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

            List<CrewRequestWithMemberDto> REQUESTS = crewParticipantQueryService
                    .fetchAllRequests(REVI.getId(), CREW.getId(), PageRequest.of(0, 3));

            assertAll(() -> {
                assertThat(REQUESTS).hasSize(2);
                assertThat(REQUESTS.get(0).member().id()).isEqualTo(KWANGWON.getId());
                assertThat(REQUESTS.get(0).member().nickname()).isEqualTo(KWANGWON_NICKNAME_VALUE);
                assertThat(REQUESTS.get(0).member().mbti()).isSameAs(KWANGWON_MBTI_VALUE);

                assertThat(REQUESTS.get(0).request().id()).isEqualTo(2L);
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

            assertThatThrownBy(() -> crewParticipantQueryService
                    .fetchAllRequests(ANDONG.getId(), CREW.getId(), PageRequest.of(0, 3)))
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

            assertThatThrownBy(() -> crewParticipantQueryService
                    .fetchAllRequests(ANDONG.getId(), CREW.getId(), PageRequest.of(0, 3)))
                    .isExactlyInstanceOf(CrewMemberBusinessException.NotOwner.class);
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

            List<CrewRequestWithCrewDto> REQUESTS = crewParticipantQueryService.fetchAllCrewRequests(ANDONG.getId());

            assertAll(() -> {
                assertThat(REQUESTS).hasSize(2);

                assertThat(REQUESTS.get(0).crew().id()).isEqualTo(2);
                assertThat(REQUESTS.get(0).crew().name()).isEqualTo(CREW2.getName().getValue());
                assertThat(REQUESTS.get(0).crew().introduce()).isEqualTo(CREW2.getIntroduce().getValue());
                assertThat(REQUESTS.get(0).crew().kakaoLink()).isEmpty();
                assertThat(REQUESTS.get(0).crew().imageUrl()).isEmpty();
                assertThat(REQUESTS.get(0).crew().owner().id()).isEqualTo(1);
                assertThat(REQUESTS.get(0).crew().owner().nickname()).isEqualTo(REVI_NICKNAME_VALUE);
                assertThat(REQUESTS.get(0).crew().owner().mbti()).isEqualTo(REVI_MBTI_VALUE);
                assertThat(REQUESTS.get(0).request().id()).isEqualTo(3);

                assertThat(REQUESTS.get(1).crew().id()).isEqualTo(1);
                assertThat(REQUESTS.get(1).crew().name()).isEqualTo(CREW1.getName().getValue());
                assertThat(REQUESTS.get(1).crew().introduce()).isEqualTo(CREW1.getIntroduce().getValue());
                assertThat(REQUESTS.get(1).crew().kakaoLink()).isEmpty();
                assertThat(REQUESTS.get(1).crew().imageUrl()).isEmpty();
                assertThat(REQUESTS.get(1).crew().owner().id()).isEqualTo(3);
                assertThat(REQUESTS.get(1).crew().owner().nickname()).isEqualTo(KWANGWON_NICKNAME_VALUE);
                assertThat(REQUESTS.get(1).crew().owner().mbti()).isEqualTo(KWANGWON_MBTI_VALUE);
                assertThat(REQUESTS.get(1).request().id()).isEqualTo(1);
            });
        }
    }
}

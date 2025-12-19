package revi1337.onsquad.crew_request.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_3;
import static revi1337.onsquad.common.fixture.CrewParticipantFixture.CREW_PARTICIPANT;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_request.domain.result.CrewRequestWithCrewResult;
import revi1337.onsquad.crew_request.domain.result.CrewRequestWithMemberResult;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Import(CrewRequestQueryDslRepository.class)
class CrewRequestQueryDslRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestJpaRepository;

    @Autowired
    private CrewRequestQueryDslRepository crewRequestQueryDslRepository;

    @Nested
    @DisplayName("자신이 신청한 Crew 참가신청 목록 조회를 테스트한다.")
    class FetchAllWithSimpleCrewByMemberId {

        @Test
        @DisplayName("자신이 신청한 Crew 참가신청 목록 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            Crew CREW2 = crewJpaRepository.save(CREW_2(REVI));
            Crew CREW3 = crewJpaRepository.save(CREW_3(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            LocalDateTime NOW = LocalDateTime.now();
            crewRequestJpaRepository.save(CREW_PARTICIPANT(CREW1, ANDONG, NOW));
            crewRequestJpaRepository.save(CREW_PARTICIPANT(CREW2, ANDONG, NOW.plusHours(1)));
            crewRequestJpaRepository.save(CREW_PARTICIPANT(CREW3, ANDONG, NOW.plusHours(2)));

            List<CrewRequestWithCrewResult> REQUESTS = crewRequestQueryDslRepository
                    .fetchAllWithSimpleCrewByMemberId(ANDONG.getId());

            assertAll(() -> {
                assertThat(REQUESTS).hasSize(3);
                assertThat(REQUESTS.get(0).crew().id()).isEqualTo(3);
                assertThat(REQUESTS.get(0).crew().name()).isEqualTo(CREW3.getName());
                assertThat(REQUESTS.get(0).crew().introduce()).isEqualTo(CREW3.getIntroduce());
                assertThat(REQUESTS.get(0).crew().imageUrl()).isEqualTo(CREW3.getImageUrl());
                assertThat(REQUESTS.get(0).crew().kakaoLink()).isEqualTo(CREW3.getKakaoLink());

                assertThat(REQUESTS.get(0).crew().owner().id()).isEqualTo(1);
                assertThat(REQUESTS.get(0).crew().owner().nickname()).isEqualTo(REVI.getNickname());
                assertThat(REQUESTS.get(0).crew().owner().mbti()).isSameAs(REVI.getMbti());

                assertThat(REQUESTS.get(0).request().id()).isEqualTo(3);
            });
        }
    }

    @Nested
    @DisplayName("Crew 참가신청 목록 DTO 조회를 테스트한다.")
    class FetchCrewRequests {

        @Test
        @DisplayName("Crew 참가신청 목록 DTO 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            LocalDateTime NOW = LocalDateTime.now();
            crewRequestJpaRepository.save(CREW_PARTICIPANT(CREW, REVI, NOW));
            crewRequestJpaRepository.save(CREW_PARTICIPANT(CREW, ANDONG, NOW.plusHours(1)));
            crewRequestJpaRepository.save(CREW_PARTICIPANT(CREW, KWANGWON, NOW.plusHours(2)));

            Page<CrewRequestWithMemberResult> REQUESTS = crewRequestQueryDslRepository
                    .fetchCrewRequests(CREW.getId(), PageRequest.of(0, 10));

            List<CrewRequestWithMemberResult> CONTENTS = REQUESTS.getContent();
            assertAll(() -> {
                assertThat(CONTENTS).hasSize(3);
                assertThat(CONTENTS.get(0).memberInfo().id()).isEqualTo(KWANGWON.getId());
                assertThat(CONTENTS.get(0).memberInfo().nickname()).isEqualTo(KWANGWON.getNickname());
                assertThat(CONTENTS.get(0).memberInfo().mbti()).isSameAs(KWANGWON.getMbti());

                assertThat(CONTENTS.get(0).request().id()).isEqualTo(3L);
            });
        }
    }
}

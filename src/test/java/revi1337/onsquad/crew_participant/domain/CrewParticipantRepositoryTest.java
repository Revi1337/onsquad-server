package revi1337.onsquad.crew_participant.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_3;
import static revi1337.onsquad.common.fixture.MemberFixtures.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixtures.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_participant.domain.dto.CrewParticipantRequest;
import revi1337.onsquad.crew_participant.domain.dto.SimpleCrewParticipantRequest;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

@Import({
        CrewParticipantRepositoryImpl.class,
        CrewParticipantJdbcRepository.class,
        CrewParticipantQueryDslRepository.class
})
class CrewParticipantRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewParticipantRepository crewParticipantRepository;

    @SpyBean
    private CrewParticipantJpaRepository crewParticipantJpaRepository;

    @Nested
    @DisplayName("Crew 참가신청 save 를 테스트한다.")
    class Save {

        @Test
        @DisplayName("Crew 참가신청 Save 에 성공한다.")
        void save() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewParticipant CREW_PARTICIPANT = new CrewParticipant(CREW, REVI, LocalDateTime.now());

            CrewParticipant SAVED_PARTICIPANT = crewParticipantRepository.save(CREW_PARTICIPANT);

            assertThat(SAVED_PARTICIPANT.getId()).isNotNull();
        }

        @Test
        @DisplayName("이미 Crew 참가신청을 한적이 있으면 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewParticipant CREW_PARTICIPANT = new CrewParticipant(CREW, REVI, LocalDateTime.now());
            crewParticipantRepository.save(CREW_PARTICIPANT);
            CrewParticipant DUPLICATE = new CrewParticipant(CREW, REVI, LocalDateTime.now());

            assertThatThrownBy(() -> crewParticipantRepository.save(DUPLICATE))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("Crew 참가 신청 조회를 테스트한다.")
    class Find {

        @Test
        @DisplayName("Crew 참가신청 조회에 성공한다.")
        void findByCrewIdAndMemberId() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewParticipant CREW_PARTICIPANT = new CrewParticipant(CREW, REVI, LocalDateTime.now());
            crewParticipantRepository.save(CREW_PARTICIPANT);

            assertThat(crewParticipantRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId())).isPresent();
        }

        @Test
        @DisplayName("Crew 참가신청를 한적이 없으면 예외를 던진다.")
        void getByCrewIdAndMemberId() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));

            assertThatThrownBy(() -> crewParticipantRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId()))
                    .isExactlyInstanceOf(CrewParticipantBusinessException.NeverRequested.class);
        }
    }

    @Nested
    @DisplayName("Crew 참사 신청 delete 를 테스트한다.")
    class Delete {

        @Test
        @DisplayName("Crew 참가신청 delete 에 성공한다.")
        void deleteById() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewParticipant CREW_PARTICIPANT = new CrewParticipant(CREW, REVI, LocalDateTime.now());
            CrewParticipant SAVED_PARTICIPANT = crewParticipantRepository.save(CREW_PARTICIPANT);

            crewParticipantRepository.deleteById(SAVED_PARTICIPANT.getId());

            assertThat(crewParticipantRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId())).isEmpty();
        }
    }

    @Nested
    @DisplayName("Crew 참가신청 Upsert 를 테스트한다.")
    class Upsert {

        @Test
        @DisplayName("Crew 에 신청해 본적이 없다면, insert 쿼리가 나간다.")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            LocalDateTime now = LocalDateTime.now();

            crewParticipantRepository.upsertCrewParticipant(CREW, REVI, now);

            Optional<CrewParticipant> PARTICIPANT = crewParticipantRepository
                    .findByCrewIdAndMemberId(CREW.getId(), REVI.getId());
            assertAll(() -> {
                assertThat(PARTICIPANT).isPresent();
                verify(crewParticipantJpaRepository).save(any(CrewParticipant.class));
            });
        }

        @Test
        @DisplayName("Crew 에 이미 신청을 했다면, Update 쿼리가 나간다.")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            crewParticipantRepository.save(new CrewParticipant(CREW, REVI, LocalDateTime.now()));
            LocalDateTime now = LocalDateTime.now();

            crewParticipantRepository.upsertCrewParticipant(CREW, REVI, now);

            CrewParticipant PARTICIPANT = crewParticipantRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId());
            assertAll(() -> {
                assertThat(PARTICIPANT.getRequestAt()).isEqualTo(now);
                verify(crewParticipantJpaRepository).saveAndFlush(any(CrewParticipant.class));
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
            crewParticipantRepository.save(new CrewParticipant(CREW, REVI, NOW));
            crewParticipantRepository.save(new CrewParticipant(CREW, ANDONG, NOW.plusHours(1)));
            crewParticipantRepository.save(new CrewParticipant(CREW, KWANGWON, NOW.plusHours(2)));

            Page<SimpleCrewParticipantRequest> REQUESTS = crewParticipantRepository
                    .fetchCrewRequests(CREW.getId(), PageRequest.of(0, 10));

            List<SimpleCrewParticipantRequest> CONTENTS = REQUESTS.getContent();
            assertAll(() -> {
                assertThat(CONTENTS).hasSize(3);
                assertThat(CONTENTS.get(0).memberInfo().id()).isEqualTo(KWANGWON.getId());
                assertThat(CONTENTS.get(0).memberInfo().nickname()).isEqualTo(KWANGWON.getNickname());
                assertThat(CONTENTS.get(0).memberInfo().mbti()).isSameAs(KWANGWON.getMbti());

                assertThat(CONTENTS.get(0).request().id()).isEqualTo(3L);
                assertThat(CONTENTS.get(0).request().requestAt()).isEqualTo(NOW.plusHours(2));
            });
        }
    }

    @Nested
    @DisplayName("자신이 신청한 Crew 참가신청 목록 조회를 테스트한다.")
    class FetchAllCrewRequestsByMemberId {

        @Test
        @DisplayName("자신이 신청한 Crew 참가신청 목록 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(REVI));
            Crew CREW2 = crewJpaRepository.save(CREW_2(REVI));
            Crew CREW3 = crewJpaRepository.save(CREW_3(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            LocalDateTime NOW = LocalDateTime.now();
            crewParticipantRepository.save(new CrewParticipant(CREW1, ANDONG, NOW));
            crewParticipantRepository.save(new CrewParticipant(CREW2, ANDONG, NOW.plusHours(1)));
            crewParticipantRepository.save(new CrewParticipant(CREW3, ANDONG, NOW.plusHours(2)));

            List<CrewParticipantRequest> REQUESTS = crewParticipantRepository
                    .fetchAllCrewRequestsByMemberId(ANDONG.getId());

            assertAll(() -> {
                assertThat(REQUESTS).hasSize(3);
                assertThat(REQUESTS.get(0).crewId()).isEqualTo(3);
                assertThat(REQUESTS.get(0).crewName()).isEqualTo(CREW3.getName());
                assertThat(REQUESTS.get(0).imageUrl()).isEqualTo(CREW3.getImageUrl());

                assertThat(REQUESTS.get(0).crewOwner().id()).isEqualTo(1);
                assertThat(REQUESTS.get(0).crewOwner().nickname()).isEqualTo(REVI.getNickname());
                assertThat(REQUESTS.get(0).crewOwner().mbti()).isSameAs(REVI.getMbti());

                assertThat(REQUESTS.get(0).request().id()).isEqualTo(3);
                assertThat(REQUESTS.get(0).request().requestAt()).isEqualTo(NOW.plusHours(2));
            });
        }
    }
}

package revi1337.onsquad.announce.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE_1;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE_2;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE_3;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE_4;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE_5;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_1;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_2;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_3;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_4;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_5;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.announce.domain.dto.AnnounceWithModifyStateDomainDto;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

@Import({AnnounceQueryDslRepository.class})
class AnnounceQueryDslRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private AnnounceJpaRepository announceJpaRepository;

    @Autowired
    private AnnounceQueryDslRepository announceQueryDslRepository;

    @Nested
    @DisplayName("Announce 들 조회를 테스트한다.")
    class Fetch {

        @Test
        @DisplayName("Announce 들 조회에 성공한다. (Projection)")
        void success1() {
            PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_MEMBER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            announceJpaRepository.saveAll(List.of(
                    ANNOUNCE_1(CREW, CREW_MEMBER),
                    ANNOUNCE_2(CREW, CREW_MEMBER),
                    ANNOUNCE_3(CREW, CREW_MEMBER),
                    ANNOUNCE_4(CREW, CREW_MEMBER),
                    ANNOUNCE_5(CREW, CREW_MEMBER)
            ));

            List<AnnounceDomainDto> DOMAIN_DTO = announceQueryDslRepository
                    .fetchAllByCrewId(CREW.getId(), PAGE_REQUEST)
                    .getContent();

            assertAll(() -> {
                assertThat(DOMAIN_DTO).hasSize(5);
                assertThat(DOMAIN_DTO.get(0).title()).isEqualTo(ANNOUNCE_TITLE_5);
                assertThat(DOMAIN_DTO.get(1).title()).isEqualTo(ANNOUNCE_TITLE_4);
                assertThat(DOMAIN_DTO.get(2).title()).isEqualTo(ANNOUNCE_TITLE_3);
                assertThat(DOMAIN_DTO.get(3).title()).isEqualTo(ANNOUNCE_TITLE_2);
                assertThat(DOMAIN_DTO.get(4).title()).isEqualTo(ANNOUNCE_TITLE_1);
            });
        }
    }

    @Nested
    @DisplayName("크루 메인 Announces 조회를 테스트한다.")
    class FetchMain {

        @Test
        @DisplayName("크루 메인 Announces 조회에 성공한다. (Projection) 1")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_MEMBER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            announceJpaRepository.saveAll(List.of(
                    ANNOUNCE_1(CREW, CREW_MEMBER),
                    ANNOUNCE_2(CREW, CREW_MEMBER),
                    ANNOUNCE_3(CREW, CREW_MEMBER),
                    ANNOUNCE_4(CREW, CREW_MEMBER),
                    ANNOUNCE_5(CREW, CREW_MEMBER)
            ));

            List<AnnounceDomainDto> DOMAIN_DTO = announceQueryDslRepository
                    .fetchAllInDefaultByCrewId(CREW.getId());

            assertAll(() -> {
                assertThat(DOMAIN_DTO).hasSize(4);
                assertThat(DOMAIN_DTO.get(0).title()).isEqualTo(ANNOUNCE_TITLE_5);
                assertThat(DOMAIN_DTO.get(1).title()).isEqualTo(ANNOUNCE_TITLE_4);
                assertThat(DOMAIN_DTO.get(2).title()).isEqualTo(ANNOUNCE_TITLE_3);
                assertThat(DOMAIN_DTO.get(3).title()).isEqualTo(ANNOUNCE_TITLE_2);
            });
        }

        @Test
        @DisplayName("크루 메인 Announces 조회에 성공한다. (Projection) 2")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_MEMBER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            LocalDateTime NOW = LocalDateTime.now();
            Announce ANNOUNCE_1 = ANNOUNCE_1(CREW, CREW_MEMBER);
            Announce ANNOUNCE_2 = ANNOUNCE_2(CREW, CREW_MEMBER);
            Announce ANNOUNCE_3 = ANNOUNCE_3(CREW, CREW_MEMBER);
            Announce ANNOUNCE_4 = ANNOUNCE_4(CREW, CREW_MEMBER);
            Announce ANNOUNCE_5 = ANNOUNCE_5(CREW, CREW_MEMBER);
            ANNOUNCE_2.fix(NOW);
            ANNOUNCE_3.fix(NOW.plusHours(1));
            ANNOUNCE_4.fix(NOW.plusHours(1));
            announceJpaRepository.saveAll(List.of(ANNOUNCE_1, ANNOUNCE_2, ANNOUNCE_3, ANNOUNCE_4, ANNOUNCE_5));

            List<AnnounceDomainDto> DOMAIN_DTO = announceQueryDslRepository.fetchAllInDefaultByCrewId(CREW.getId());

            assertAll(() -> {
                assertThat(DOMAIN_DTO).hasSize(4);
                assertThat(DOMAIN_DTO.get(0).title()).isEqualTo(ANNOUNCE_TITLE_4);
                assertThat(DOMAIN_DTO.get(1).title()).isEqualTo(ANNOUNCE_TITLE_3);
                assertThat(DOMAIN_DTO.get(2).title()).isEqualTo(ANNOUNCE_TITLE_2);
                assertThat(DOMAIN_DTO.get(3).title()).isEqualTo(ANNOUNCE_TITLE_5);
            });
        }
    }

    @Deprecated
    @Nested
    @DisplayName("상단 고정할 수 있는 상태와 함께 Announce 단일 조회를 테스트한다.")
    class FetchByCrewIdAndIdAndMemberId {

        @Test
        @DisplayName("Announce 가 존재하지 않으면 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Long DUMMY_ANNOUNCE_ID = 1L;

            assertThat(announceQueryDslRepository
                    .fetchByCrewIdAndIdAndMemberId(CREW.getId(), DUMMY_ANNOUNCE_ID, REVI.getId()))
                    .isEmpty();
        }

        @Test
        @DisplayName("Announce 작성자 정보와 일치하면 상단 고정할 수 있는 상태가 true 이다.")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_MEMBER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_MEMBER));

            Optional<AnnounceWithModifyStateDomainDto> DOMAIN_DTO = announceQueryDslRepository
                    .fetchByCrewIdAndIdAndMemberId(CREW.getId(), ANNOUNCE.getId(), REVI.getId());

            assertAll(() -> {
                assertThat(DOMAIN_DTO).isPresent();
                assertThat(DOMAIN_DTO.get().canModify()).isTrue();
            });
        }

        @Test
        @DisplayName("Announce 작성자 정보와 일차하지 않으면 상단 고정할 수 있는 상태가 false 이다.")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_MEMBER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_MEMBER));
            Long DUMMY_MEMBER_ID = 2L;

            Optional<AnnounceWithModifyStateDomainDto> DOMAIN_DTO = announceQueryDslRepository
                    .fetchByCrewIdAndIdAndMemberId(CREW.getId(), ANNOUNCE.getId(), DUMMY_MEMBER_ID);

            assertAll(() -> {
                assertThat(DOMAIN_DTO).isPresent();
                assertThat(DOMAIN_DTO.get().canModify()).isFalse();
            });
        }
    }

    @Nested
    @DisplayName("단일 Announce 조회를 테스트한다.")
    class FetchByCrewIdAndId {

        @Test
        @DisplayName("단일 Announce 조회에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember CREW_MEMBER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_MEMBER));

            Optional<AnnounceDomainDto> DTO = announceQueryDslRepository
                    .fetchByCrewIdAndId(CREW.getId(), ANNOUNCE.getId());

            assertThat(DTO).isPresent();
        }

        @Test
        @DisplayName("단일 Announce 조회에 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Long DUMMY_ANNOUNCE_ID = 1L;

            Optional<AnnounceDomainDto> DTO = announceQueryDslRepository
                    .fetchByCrewIdAndId(CREW.getId(), DUMMY_ANNOUNCE_ID);

            assertThat(DTO).isEmpty();
        }
    }
}

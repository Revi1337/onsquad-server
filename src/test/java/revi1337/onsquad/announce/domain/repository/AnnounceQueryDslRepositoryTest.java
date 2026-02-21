package revi1337.onsquad.announce.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Import(AnnounceQueryDslRepository.class)
class AnnounceQueryDslRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private AnnounceJpaRepository announceRepository;

    @Autowired
    private AnnounceQueryDslRepository announceQueryDslRepository;

    @Test
    @DisplayName("크루 ID로 공지 목록 조회 시 고정(Pin)된 공지가 최상단에, 그 안에서는 최신순으로 정렬되어야 한다")
    void fetchAllByCrewId() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        LocalDateTime now = LocalDate.of(2026, 1, 4).atStartOfDay();
        Announce announce1 = createCrewAnnounce(crew, revi);
        Announce announce2 = createCrewAnnounce(crew, revi);
        Announce announce3 = createCrewAnnounce(crew, revi);
        announce1.pin(now.plusHours(1));
        announce2.pin(now.plusHours(2));
        announceRepository.saveAll(List.of(announce1, announce2, announce3));

        List<Announce> announces = announceQueryDslRepository.fetchAllByCrewId(revi.getId());

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(announces).hasSize(3);

            softly.assertThat(announces.get(0).getId()).isEqualTo(announce2.getId());
            softly.assertThat(announces.get(0).isPinned()).isTrue();

            softly.assertThat(announces.get(1).getId()).isEqualTo(announce1.getId());
            softly.assertThat(announces.get(1).isPinned()).isTrue();

            softly.assertThat(announces.get(2).getId()).isEqualTo(announce3.getId());
            softly.assertThat(announces.get(2).isPinned()).isFalse();
        });
    }

    @Test
    @DisplayName("기본 공지 목록 조회 시 지정된 제한(Limit) 개수만큼만 데이터를 반환해야 한다")
    void fetchAllInDefaultByCrewId() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        LocalDateTime now = LocalDate.of(2026, 1, 4).atStartOfDay();
        Announce announce1 = createCrewAnnounce(crew, revi);
        Announce announce2 = createCrewAnnounce(crew, revi);
        Announce announce3 = createCrewAnnounce(crew, revi);
        announce1.pin(now.plusHours(1));
        announce2.pin(now.plusHours(2));
        announceRepository.saveAll(List.of(announce1, announce2, announce3));

        List<Announce> announces = announceQueryDslRepository.fetchAllInDefaultByCrewId(crew.getId(), 2);

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(announces).hasSize(2);

            softly.assertThat(announces.get(0).getId()).isEqualTo(announce2.getId());
            softly.assertThat(announces.get(0).isPinned()).isTrue();

            softly.assertThat(announces.get(1).getId()).isEqualTo(announce1.getId());
            softly.assertThat(announces.get(1).isPinned()).isTrue();
        });
    }

    @Test
    @DisplayName("공지 ID와 크루 ID가 모두 일치하는 특정 공지사항의 상세 정보를 조회한다")
    void fetchByCrewIdAndId() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        LocalDateTime now = LocalDate.of(2026, 1, 4).atStartOfDay();
        Announce announce1 = createCrewAnnounce(crew, revi);
        Announce announce2 = createCrewAnnounce(crew, revi);
        Announce announce3 = createCrewAnnounce(crew, revi);
        announce1.pin(now.plusHours(1));
        announce2.pin(now.plusHours(2));
        announceRepository.saveAll(List.of(announce1, announce2, announce3));

        Optional<Announce> announce = announceQueryDslRepository.fetchByIdAndCrewId(announce2.getId(), crew.getId());

        clearPersistenceContext();
        assertThat(announce).isPresent();
    }

    private Announce createCrewAnnounce(Crew crew, Member revi) {
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        return new Announce(uuid, uuid, crew, revi);
    }
}

package revi1337.onsquad.crew_member.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewActivityScore;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Import({CrewActivityScoreRepositoryImpl.class, CrewActivityScoreJdbcRepository.class})
class CrewActivityScoreRepositoryImplTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewActivityScoreRepository crewActivityScoreRepository;

    @Autowired
    private CrewActivityScoreJpaRepository crewActivityScoreJpaRepository;

    @Test
    @DisplayName("크루 활동을 UPSERT하면 최초 호출은 새 행을 생성한다")
    void upsertScore_createsNewRow_whenFirstCall() {
        // given
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));
        LocalDateTime lastActivityAt = LocalDateTime.of(2026, 1, 6, 12, 0);

        // when
        crewActivityScoreRepository.upsertScore(
                crew.getId(), member.getId(), CrewActivity.SQUAD_CREATE.getWeight(), lastActivityAt
        );

        // then
        List<CrewActivityScore> found = crewActivityScoreJpaRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(found).hasSize(1);
            softly.assertThat(found.get(0).getCrewId()).isEqualTo(crew.getId());
            softly.assertThat(found.get(0).getMemberId()).isEqualTo(member.getId());
            softly.assertThat(found.get(0).getWeight()).isEqualTo(CrewActivity.SQUAD_CREATE.getWeight());
        });
    }

    @Test
    @DisplayName("동일 크루/멤버에 반복 UPSERT하면 별도 행이 아니라 기존 행의 weight가 누적된다")
    void upsertScore_accumulatesWeightOnExistingRow_whenRepeatedForSameCrewAndMember() {
        // given
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));

        // when
        crewActivityScoreRepository.upsertScore(
                crew.getId(), member.getId(), CrewActivity.SQUAD_COMMENT.getWeight(), LocalDateTime.now()
        );
        crewActivityScoreRepository.upsertScore(
                crew.getId(), member.getId(), CrewActivity.SQUAD_COMMENT.getWeight(), LocalDateTime.now()
        );

        // then
        List<CrewActivityScore> found = crewActivityScoreJpaRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(found).hasSize(1);
            softly.assertThat(found.get(0).getWeight()).isEqualTo(CrewActivity.SQUAD_COMMENT.getWeight() * 2);
        });
    }
}

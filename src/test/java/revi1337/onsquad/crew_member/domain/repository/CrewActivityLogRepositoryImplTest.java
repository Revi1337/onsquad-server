package revi1337.onsquad.crew_member.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewActivityLog;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Import({CrewActivityLogRepositoryImpl.class, CrewActivityLogJdbcRepository.class})
class CrewActivityLogRepositoryImplTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewActivityLogRepository crewActivityLogRepository;

    @Autowired
    private CrewActivityLogJpaRepository crewActivityLogJpaRepository;

    @Test
    @DisplayName("크루 활동 로그를 저장하면 식별자가 채번되고, 저장한 값 그대로 조회된다")
    void save() {
        // given
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 6, 12, 0);

        // when
        CrewActivityLog saved = crewActivityLogRepository.save(new CrewActivityLog(
                crew.getId(), member.getId(), CrewActivity.SQUAD_CREATE, CrewActivity.SQUAD_CREATE.getWeight(), createdAt
        ));

        // then
        assertSoftly(softly -> {
            softly.assertThat(saved.getId()).isNotNull();

            CrewActivityLog found = crewActivityLogJpaRepository.findById(saved.getId()).orElseThrow();
            softly.assertThat(found.getCrewId()).isEqualTo(crew.getId());
            softly.assertThat(found.getMemberId()).isEqualTo(member.getId());
            softly.assertThat(found.getActivityType()).isSameAs(CrewActivity.SQUAD_CREATE);
            softly.assertThat(found.getWeight()).isEqualTo(CrewActivity.SQUAD_CREATE.getWeight());
            softly.assertThat(found.getCreatedAt()).isEqualTo(createdAt);
        });
    }

    @Test
    @DisplayName("동일 크루/멤버라도 활동이 발생할 때마다 append-only 로 별도의 로그 행이 각각 저장된다")
    void save_appendsSeparateRowsForRepeatedActivities() {
        // given
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));

        // when
        CrewActivityLog first = crewActivityLogRepository.save(new CrewActivityLog(
                crew.getId(), member.getId(), CrewActivity.SQUAD_COMMENT, CrewActivity.SQUAD_COMMENT.getWeight(), LocalDateTime.now()
        ));
        CrewActivityLog second = crewActivityLogRepository.save(new CrewActivityLog(
                crew.getId(), member.getId(), CrewActivity.SQUAD_COMMENT, CrewActivity.SQUAD_COMMENT.getWeight(), LocalDateTime.now()
        ));

        // then
        assertSoftly(softly -> {
            softly.assertThat(first.getId()).isNotEqualTo(second.getId());
            softly.assertThat(crewActivityLogJpaRepository.findAll()).hasSize(2);
        });
    }
}

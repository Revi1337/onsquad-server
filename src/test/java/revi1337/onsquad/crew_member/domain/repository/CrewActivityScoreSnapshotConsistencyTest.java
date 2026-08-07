package revi1337.onsquad.crew_member.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.config.PersistenceLayerConfiguration;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewActivityScore;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

/**
 * 4-1 단계(UPSERT 사전 집계) 시점의 리더보드 배치 정리 로직은
 * {@link CrewActivityScoreRepository#deleteByLastActivityAtBetween(LocalDateTime, LocalDateTime)} 로
 * {@code crew_activity_score} 행을 "시간 범위" 기준으로 통째로 삭제한다.
 * <p>
 * 배치가 {@code fetchAggregatedRankedMembers}로 랭킹을 읽은 뒤, 정리를 위해
 * {@code deleteByLastActivityAtBetween}을 호출하기까지의 사이에 실시간 이벤트로 같은
 * (crew_id, member_id) 행에 새 UPSERT가 끼어들면, 그 행은 현재 weight 값과 무관하게
 * 시간 범위에 걸렸다는 이유만으로 통째로 삭제되어 새로 들어온 활동 점수가 유실된다.
 * <p>
 * 이 클래스의 첫 번째 테스트는 그 유실을 실제로 재현하고, "이 시점엔 이게 실제 동작이다"는 것을
 * assertion으로 그대로 기록한다 — 즉 버그가 있는 채로 통과(GREEN)한다.
 */
@Sql({"/mysql-truncate.sql"})
@Import({PersistenceLayerConfiguration.class, CrewActivityScoreRepositoryImpl.class, CrewActivityScoreJdbcRepository.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest(showSql = false)
class CrewActivityScoreSnapshotConsistencyTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewActivityScoreRepository crewActivityScoreRepository;

    @Autowired
    private CrewActivityScoreJpaRepository crewActivityScoreJpaRepository;

    /**
     * 4-1 시점의 {@code deleteByLastActivityAtBetween(from, to)}는 행의 "현재 weight 값"을 보지 않고
     * "시간 범위"만으로 삭제하기 때문에, 배치가 랭킹을 읽은 이후 정리를 실행하기 전 사이에 새로
     * UPSERT된 활동(weight 10)까지 통째로 사라진다.
     * <p>
     * 아래 assertion은 "이 시점에 실제로 일어나는 동작"을 그대로 표현한다 — 즉 새로 들어온 10점이
     * 유실되어 행 자체가 사라진다는 것을 기대값으로 명시하고, 그 기대대로 동작하므로 이 테스트는
     * 통과(GREEN)한다. 이건 "버그가 없다"는 뜻이 아니라 "이 시점엔 이게 알려진, 문서화된 버그였다"는
     * 뜻이다.
     */
    @Test
    @DisplayName("[알려진 버그] 랭킹 조회와 삭제 사이에 새 UPSERT가 끼어들면, deleteByLastActivityAtBetween이 새로 들어온 활동까지 통째로 삭제해 유실시킨다")
    void deleteByLastActivityAtBetween_losesRaceInsertedActivity_whenNewUpsertHappensBetweenReadAndDelete() {
        // given: 특정 (crew_id, member_id) 행을 weight=5 로 만들어둔다 (activityTime 은 [from, to] 범위 안)
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));
        LocalDateTime activityTime = LocalDateTime.of(2026, 1, 6, 12, 0);
        LocalDateTime from = activityTime.minusDays(1);
        LocalDateTime to = activityTime.plusDays(1);

        crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 5, activityTime);
        clearPersistenceContext();

        // when: 배치가 먼저 랭킹 집계를 읽는다 (프로덕션 흐름상 정리보다 먼저 일어남)
        crewActivityScoreRepository.fetchAggregatedRankedMembers(from, to, 10);

        // 그 사이 실시간 이벤트로 같은 행에 새 활동이 들어온다 (weight 5 -> 15, last_activity_at 도 갱신되어 여전히 [from, to] 범위 안)
        crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 10, activityTime.plusMinutes(1));
        clearPersistenceContext();

        // 배치가 뒤늦게 정리 단계로 시간 범위 기준 통째 삭제를 수행한다
        crewActivityScoreRepository.deleteByLastActivityAtBetween(from, to);
        clearPersistenceContext();

        // then: 4-1 시점엔 새로 들어온 10점까지 deleteByLastActivityAtBetween에 의해 행째로 유실되는 게 "실제 동작"이다 (알려진 버그를 그대로 기록)
        Optional<CrewActivityScore> found = crewActivityScoreJpaRepository.findAll().stream()
                .filter(row -> row.getCrewId().equals(crew.getId()) && row.getMemberId().equals(member.getId()))
                .findFirst();

        assertSoftly(softly -> softly.assertThat(found)
                .as("그 사이 새로 들어온 활동(weight 10)까지 deleteByLastActivityAtBetween에 의해 행째로 유실된다 (알려진 버그)")
                .isEmpty());
    }

    @Test
    @DisplayName("[대조군] 레이스 없이 정리하면 deleteByLastActivityAtBetween은 범위 내 행을 정상적으로 삭제한다")
    void deleteByLastActivityAtBetween_removesRow_whenNoRaceHappensBeforeDelete() {
        // given: weight=5, 이후 추가 활동 없음
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));
        LocalDateTime activityTime = LocalDateTime.of(2026, 1, 6, 12, 0);
        LocalDateTime from = activityTime.minusDays(1);
        LocalDateTime to = activityTime.plusDays(1);

        crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 5, activityTime);
        clearPersistenceContext();

        // when: 레이스 없이 바로 정리 단계를 수행한다
        crewActivityScoreRepository.deleteByLastActivityAtBetween(from, to);
        clearPersistenceContext();

        // then: 레이스가 없었으므로 이 삭제 방식 자체는 문제없이 행이 삭제된다
        List<CrewActivityScore> found = crewActivityScoreJpaRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(found)
                    .as("레이스가 없는 정상 케이스에서는 시간 범위 삭제가 의도대로 동작해야 한다")
                    .isEmpty();
        });
    }

    private void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }
}

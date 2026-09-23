package revi1337.onsquad.crew_member.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
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
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewActivityScoreSnapshot;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerJdbcRepository;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

/**
 * 리더보드 배치가 crew_activity_score 스냅샷을 읽은 시점과, 그 스냅샷을 근거로
 * subtractCountedWeight/deleteZeroWeightRows 를 실행하는 시점 사이에 새로운 UPSERT 가
 * 끼어드는 레이스를 단일 스레드에서 "의도적으로 나쁜 순서"로 메서드를 호출해 결정론적으로 재현한다.
 * <p>
 * 검증 대상 버그는 (crew_id, member_id) 단위의 호출 순서 문제이므로 대량 시드 데이터나
 * 진짜 멀티스레드(ExecutorService/CountDownLatch)는 필요하지 않다.
 * 진짜 동시 UPSERT 경합 자체의 정합성은 {@code CrewActivityScoreUpsertConcurrencyTest} 가 별도로 검증한다.
 */
@Sql({"/mysql-truncate.sql"})
@Import({PersistenceLayerConfiguration.class, CrewActivityScoreRepositoryImpl.class, CrewActivityScoreJdbcRepository.class, CrewRankerJdbcRepository.class})
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

    @Autowired
    private CrewRankerJdbcRepository crewRankerJdbcRepository;

    @Autowired
    private CrewRankerJpaRepository crewRankerJpaRepository;

    @Test
    @DisplayName("스냅샷을 읽은 직후 같은 (crew_id, member_id)에 새 UPSERT가 들어와도, 배치는 스냅샷에 담긴 만큼만 정확히 차감하여 새로 들어온 활동을 유실시키지 않는다")
    void subtractCountedWeight_preservesNewlyUpsertedWeight_whenUpsertHappensAfterSnapshotIsFetched() {
        // given: 특정 (crew_id, member_id) 행을 weight=5 로 만들어둔다
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));
        LocalDateTime activityTime = LocalDateTime.of(2026, 1, 6, 12, 0);
        crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 5, activityTime);
        clearPersistenceContext();

        // when: 배치가 스냅샷을 읽는다 (이 시점 weight=5 가 스냅샷에 담긴다)
        List<CrewActivityScoreSnapshot> snapshot = crewActivityScoreRepository
                .fetchSnapshot(activityTime.minusDays(1), activityTime.plusDays(1));

        // 스냅샷을 얻은 직후, 배치가 아직 삭제/차감을 실행하기 전에 같은 행에 새 활동이 UPSERT 된다
        // (이벤트리스너가 실시간으로 호출하는 경로를 시뮬레이션. 이 시점 실제 DB weight 는 15)
        crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 10, activityTime.plusMinutes(1));
        clearPersistenceContext();

        // 배치가 뒤늦게 스냅샷 기준으로 차감/정리를 수행한다
        crewActivityScoreRepository.subtractCountedWeight(snapshot);
        crewActivityScoreRepository.deleteZeroWeightRows();
        clearPersistenceContext();

        // then: 행이 삭제되지 않고 남아있어야 하며, weight 는 정확히 10(15-5) 이어야 한다
        List<CrewActivityScore> found = crewActivityScoreJpaRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(found)
                    .as("새로 들어온 활동은 deleteZeroWeightRows 에 의해 삭제되면 안 된다")
                    .hasSize(1);
            softly.assertThat(found.get(0).getWeight())
                    .as("기존 5점만 정확히 차감되고, 그 사이 들어온 10점은 유실 없이 보존되어야 한다 (15-5=10)")
                    .isEqualTo(10);
        });
    }

    @Test
    @DisplayName("스냅샷을 읽은 이후 새로운 활동이 전혀 없으면, 차감된 weight는 정확히 0이 되어 해당 행이 삭제된다")
    void subtractCountedWeightAndDeleteZeroWeightRows_removesRow_whenNoNewActivityAfterSnapshot() {
        // given
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));
        LocalDateTime activityTime = LocalDateTime.of(2026, 1, 6, 12, 0);
        crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 5, activityTime);
        clearPersistenceContext();

        List<CrewActivityScoreSnapshot> snapshot = crewActivityScoreRepository
                .fetchSnapshot(activityTime.minusDays(1), activityTime.plusDays(1));

        // when: 스냅샷을 읽은 후 레이스 없이(추가 활동 없이) 그대로 정리를 수행한다
        crewActivityScoreRepository.subtractCountedWeight(snapshot);
        crewActivityScoreRepository.deleteZeroWeightRows();
        clearPersistenceContext();

        // then: weight 가 정확히 0이 되어 행 자체가 삭제된다
        List<CrewActivityScore> found = crewActivityScoreJpaRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(found)
                    .as("레이스가 없었으므로 weight 는 정확히 0이 되고, 0-weight 행은 삭제되어야 한다")
                    .isEmpty();
        });
    }

    @Test
    @DisplayName("빈 스냅샷으로 subtractCountedWeight를 호출해도 기존 행에는 아무 영향을 주지 않는다")
    void subtractCountedWeight_doesNothing_whenSnapshotIsEmpty() {
        // given
        Member member = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(member));
        crewActivityScoreRepository.upsertScore(crew.getId(), member.getId(), 7, LocalDateTime.now());
        clearPersistenceContext();

        // when: 빈 스냅샷으로 호출한다 (가드에 의해 아무 것도 하지 않아야 한다)
        crewActivityScoreRepository.subtractCountedWeight(List.of());
        clearPersistenceContext();

        // then: 기존 행은 그대로 남아있고 weight 도 변하지 않는다
        List<CrewActivityScore> found = crewActivityScoreJpaRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(found).hasSize(1);
            softly.assertThat(found.get(0).getWeight()).isEqualTo(7);
        });
    }

    @Test
    @DisplayName("swapSnapshot 호출 전후로 crew_ranker 조회 결과가 신규 스냅샷으로 원자적으로 교체된다")
    void swapSnapshot_atomicallyReplacesCrewRankerTableContents() {
        // given: 스왑 전 crew_ranker 에는 지난 배치의 구(舊) 데이터가 들어있다
        Member owner = memberJpaRepository.save(createRevi());
        Crew crew = crewJpaRepository.save(createCrew(owner));

        CrewRankerCandidate oldCandidate = new CrewRankerCandidate(
                crew.getId(), 1, 999L, owner.getId(), owner.getNickname().getValue(), owner.getMbti().name(), LocalDateTime.now()
        );
        crewRankerJdbcRepository.insertBatch(List.of(oldCandidate));
        clearPersistenceContext();

        assertSoftly(softly -> softly.assertThat(crewRankerJpaRepository.findAll())
                .as("스왑 전에는 구 데이터가 조회되어야 한다")
                .extracting(CrewRanker::getScore)
                .containsExactly(999L));

        Member newMember = memberJpaRepository.save(createAndong());
        CrewRankerCandidate newCandidate = new CrewRankerCandidate(
                crew.getId(), 1, 777L, newMember.getId(), newMember.getNickname().getValue(), newMember.getMbti().name(), LocalDateTime.now()
        );

        // when: 신규 후보로 스냅샷 스왑을 수행한다
        crewRankerJdbcRepository.swapSnapshot(List.of(newCandidate));
        clearPersistenceContext();

        // then: 스왑 후에는 신 데이터만 조회되고, 구 데이터는 완전히 사라진다
        assertSoftly(softly -> {
            List<CrewRanker> rankers = crewRankerJpaRepository.findAll();
            softly.assertThat(rankers).hasSize(1);
            softly.assertThat(rankers.get(0).getScore()).isEqualTo(777L);
            softly.assertThat(rankers.get(0).getMemberId()).isEqualTo(newMember.getId());
        });
    }

    private void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }
}

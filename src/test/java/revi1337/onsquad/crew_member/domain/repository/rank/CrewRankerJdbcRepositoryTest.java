package revi1337.onsquad.crew_member.domain.repository.rank;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.MySqlTestContainerSupport;
import revi1337.onsquad.common.config.PersistenceLayerConfiguration;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.member.domain.entity.Member;

@Sql({"/mysql-truncate.sql"})
@Import({PersistenceLayerConfiguration.class, CrewRankerJdbcRepository.class})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest(showSql = false)
class CrewRankerJdbcRepositoryTest implements MySqlTestContainerSupport {

    @Autowired
    private CrewRankerJpaRepository jpaRepository;

    @Autowired
    private CrewRankerJdbcRepository jdbcRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("JDBC 배치 삽입 시, 지정된 순위(Rank)와 크루 ID 조건에 맞는 데이터만 정렬되어 조회된다")
    void insertBatch() {
        CrewRankerCandidate ranked1 = createCrewRankerCandidate(1L, 3, 5, createRevi(1L));
        CrewRankerCandidate ranked2 = createCrewRankerCandidate(1L, 2, 10, createAndong(2L));
        CrewRankerCandidate ranked3 = createCrewRankerCandidate(1L, 1, 15, createKwangwon(3L));

        jdbcRepository.insertBatch(List.of(ranked1, ranked2, ranked3));

        assertSoftly(softly -> {
            List<CrewRanker> rankers = jpaRepository.findAllByCrewIdAndRankLessThanEqualOrderByRankAsc(1L, 2);
            softly.assertThat(rankers).hasSize(2);
            softly.assertThat(rankers.get(0).getMemberId()).isEqualTo(3L);
            softly.assertThat(rankers.get(1).getMemberId()).isEqualTo(2L);
        });
    }

    @Test
    @DisplayName("기존 테이블의 스키마를 복제하여 새로운 그림자 테이블을 물리적으로 생성한다")
    void prepareShadowTable() {
        jdbcRepository.prepareShadowTable();

        assertThatCode(() -> jdbcTemplate.execute("DESCRIBE crew_ranker_shadow"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("switchTables 실행 시 원본 테이블과 그림자 테이블의 데이터가 서로 교체된다")
    void switchTables() {
        CrewRankerCandidate ranked1 = createCrewRankerCandidate(1L, 3, 5, createRevi(1L));
        CrewRankerCandidate ranked2 = createCrewRankerCandidate(1L, 2, 10, createAndong(2L));
        CrewRankerCandidate ranked3 = createCrewRankerCandidate(1L, 1, 15, createKwangwon(3L));
        jdbcRepository.insertBatch(List.of(ranked1, ranked2, ranked3));
        jdbcRepository.prepareShadowTable();

        jdbcRepository.switchTables();

        assertSoftly(softly -> {
            softly.assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM crew_ranker", Integer.class))
                    .as("스위칭 후 원본 테이블은 비어있어야 함")
                    .isEqualTo(0);
            softly.assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM crew_ranker_shadow", Integer.class))
                    .as("스위칭 후 그림자 테이블에 기존 데이터 3건이 존재해야 함")
                    .isEqualTo(3);
        });
    }

    @Test
    @DisplayName("테이블 초기화(Truncate) 실행 시 기존의 모든 랭킹 데이터는 삭제되고, Sequence index 는 초기화된다.")
    void truncate() {
        CrewRankerCandidate ranked1 = createCrewRankerCandidate(1L, 3, 5, createRevi(1L));
        CrewRankerCandidate ranked2 = createCrewRankerCandidate(1L, 2, 10, createAndong(2L));
        CrewRankerCandidate ranked3 = createCrewRankerCandidate(1L, 1, 15, createKwangwon(3L));
        jdbcRepository.insertBatch(List.of(ranked1, ranked2, ranked3));

        jdbcRepository.truncate();

        assertSoftly(softly -> {
            List<CrewRanker> rankers = jpaRepository.findAllByCrewIdAndRankLessThanEqualOrderByRankAsc(1L, 3);
            softly.assertThat(rankers).isEmpty();
            clearPersistenceContext();

            jdbcRepository.insertBatch(List.of(ranked1));
            clearPersistenceContext();
            softly.assertThat(jpaRepository.findAll().get(0).getId()).isEqualTo(1L);
        });
    }

    private static CrewRankerCandidate createCrewRankerCandidate(Long crewId, int rank, long score, Member member) {
        return new CrewRankerCandidate(
                crewId,
                rank,
                score,
                member.getId(),
                member.getNickname().getValue(),
                member.getMbti().name(),
                LocalDateTime.now()
        );
    }

    private void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }
}

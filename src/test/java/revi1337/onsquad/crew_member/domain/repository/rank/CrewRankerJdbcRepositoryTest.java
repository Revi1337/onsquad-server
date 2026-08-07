package revi1337.onsquad.crew_member.domain.repository.rank;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

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
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.member.domain.entity.Member;

@Sql({"/mysql-truncate.sql"})
@Import({PersistenceLayerConfiguration.class, CrewRankerJdbcRepository.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest(showSql = false)
class CrewRankerJdbcRepositoryTest {

    @Autowired
    private CrewRankerJpaRepository jpaRepository;

    @Autowired
    private CrewRankerJdbcRepository jdbcRepository;

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
}

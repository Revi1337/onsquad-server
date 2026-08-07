package revi1337.onsquad.crew_member.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.config.PersistenceLayerConfiguration;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.common.fixture.CrewFixture;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Sql({"/mysql-truncate.sql"})
@Import({PersistenceLayerConfiguration.class, CrewActivityScoreJdbcRepository.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest(showSql = false)
class CrewActivityScoreJdbcRepositoryTest {

    private static final LocalDateTime WINDOW_FROM = LocalDateTime.of(2026, 1, 5, 0, 0);
    private static final LocalDateTime WINDOW_TO = LocalDateTime.of(2026, 1, 12, 0, 0);

    @Autowired
    private CrewActivityScoreJdbcRepository jdbcRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("crew_activity_score 에 쌓인 여러 활동 타입의 가중치를 멤버별로 합산하여 점수/순위/최근활동시각을 집계한다")
    void aggregateRankedMembersGivenActivityWeight() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member joiner = memberJpaRepository.save(createAndong());
        Member creator = memberJpaRepository.save(createKwangwon());
        Member participant = memberJpaRepository.save(createMember(4));
        Member commenter = memberJpaRepository.save(createMember(5));
        Member outsider = memberJpaRepository.save(createMember(6));

        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));

        // 가입(+5): 윈도우 내 활동이므로 집계 대상
        saveActivityLog(crew.getId(), joiner.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_FROM.plusDays(1));
        // 가입(+5): 윈도우 밖 활동이므로 집계에서 제외
        saveActivityLog(crew.getId(), outsider.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_FROM.minusDays(1));

        // 스쿼드생성(+10) + 리더로서 스쿼드참여(+3) = 13점. 가장 최근 활동시각은 스쿼드참여 시각이어야 한다.
        LocalDateTime creatorFirstActivity = WINDOW_FROM.plusDays(2);
        LocalDateTime creatorLastActivity = creatorFirstActivity.plusHours(1);
        saveActivityLog(crew.getId(), creator.getId(), CrewActivity.SQUAD_CREATE, creatorFirstActivity);
        saveActivityLog(crew.getId(), creator.getId(), CrewActivity.SQUAD_PARTICIPANT, creatorLastActivity);

        // 스쿼드참여(+3): 일반 참여자
        saveActivityLog(crew.getId(), participant.getId(), CrewActivity.SQUAD_PARTICIPANT, WINDOW_FROM.plusDays(3));

        // 댓글(+1)
        saveActivityLog(crew.getId(), commenter.getId(), CrewActivity.SQUAD_COMMENT, WINDOW_FROM.plusDays(4));

        clearPersistenceContext();

        // when
        List<CrewRankerCandidate> candidates = jdbcRepository.aggregateRankedMembersGivenActivityWeight(WINDOW_FROM, WINDOW_TO, 10);

        // then
        assertSoftly(softly -> {
            softly.assertThat(candidates).hasSize(4);
            softly.assertThat(candidates).extracting(CrewRankerCandidate::memberId)
                    .doesNotContain(outsider.getId());

            CrewRankerCandidate first = candidates.get(0);
            softly.assertThat(first.memberId()).isEqualTo(creator.getId());
            softly.assertThat(first.score()).isEqualTo(13L);
            softly.assertThat(first.rank()).isEqualTo(1);
            softly.assertThat(first.lastActivityTime()).isEqualTo(creatorLastActivity);

            CrewRankerCandidate second = candidates.get(1);
            softly.assertThat(second.memberId()).isEqualTo(joiner.getId());
            softly.assertThat(second.score()).isEqualTo(5L);
            softly.assertThat(second.rank()).isEqualTo(2);

            CrewRankerCandidate third = candidates.get(2);
            softly.assertThat(third.memberId()).isEqualTo(participant.getId());
            softly.assertThat(third.score()).isEqualTo(3L);
            softly.assertThat(third.rank()).isEqualTo(3);

            CrewRankerCandidate fourth = candidates.get(3);
            softly.assertThat(fourth.memberId()).isEqualTo(commenter.getId());
            softly.assertThat(fourth.score()).isEqualTo(1L);
            softly.assertThat(fourth.rank()).isEqualTo(4);
        });

        // when rankLimit 을 2로 좁히면 상위 2명만 반환된다
        List<CrewRankerCandidate> topTwo = jdbcRepository.aggregateRankedMembersGivenActivityWeight(WINDOW_FROM, WINDOW_TO, 2);

        // then
        assertSoftly(softly -> {
            softly.assertThat(topTwo).hasSize(2);
            softly.assertThat(topTwo).extracting(CrewRankerCandidate::memberId)
                    .containsExactly(creator.getId(), joiner.getId());
        });
    }

    @Test
    @DisplayName("점수와 최근활동시각이 완전히 동일한 멤버는 DENSE_RANK 에 의해 같은 순위를 부여받고, 다음 순위는 건너뛰지 않는다")
    void aggregateRankedMembersGivenActivityWeight_denseRanksTiedScores() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member tiedA = memberJpaRepository.save(createAndong());
        Member tiedB = memberJpaRepository.save(createKwangwon());
        Member lower = memberJpaRepository.save(createMember(4));
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));

        LocalDateTime tiedActivityTime = WINDOW_FROM.plusDays(1);
        saveActivityLog(crew.getId(), tiedA.getId(), CrewActivity.SQUAD_PARTICIPANT, tiedActivityTime);
        saveActivityLog(crew.getId(), tiedB.getId(), CrewActivity.SQUAD_PARTICIPANT, tiedActivityTime);
        saveActivityLog(crew.getId(), lower.getId(), CrewActivity.SQUAD_COMMENT, WINDOW_FROM.plusDays(2));

        clearPersistenceContext();

        // when
        List<CrewRankerCandidate> candidates = jdbcRepository.aggregateRankedMembersGivenActivityWeight(WINDOW_FROM, WINDOW_TO, 10);

        // then
        assertSoftly(softly -> {
            softly.assertThat(candidates).hasSize(3);
            softly.assertThat(candidates)
                    .filteredOn(candidate -> candidate.memberId().equals(tiedA.getId()) || candidate.memberId().equals(tiedB.getId()))
                    .extracting(CrewRankerCandidate::rank)
                    .containsExactly(1, 1);

            CrewRankerCandidate lowerCandidate = candidates.stream()
                    .filter(candidate -> candidate.memberId().equals(lower.getId()))
                    .findFirst()
                    .orElseThrow();
            softly.assertThat(lowerCandidate.rank())
                    .as("동점자가 공동 1위이므로 다음 순위는 2위여야 한다(3위로 건너뛰지 않음)")
                    .isEqualTo(2);
        });
    }

    @Test
    @DisplayName("활동 시각이 [from, to] 구간을 벗어나면 집계 대상에서 제외된다")
    void aggregateRankedMembersGivenActivityWeight_excludesActivitiesOutsideWindow() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member beforeWindow = memberJpaRepository.save(createAndong());
        Member afterWindow = memberJpaRepository.save(createKwangwon());
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));

        saveActivityLog(crew.getId(), beforeWindow.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_FROM.minusSeconds(1));
        saveActivityLog(crew.getId(), afterWindow.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_TO.plusSeconds(1));

        clearPersistenceContext();

        // when
        List<CrewRankerCandidate> candidates = jdbcRepository.aggregateRankedMembersGivenActivityWeight(WINDOW_FROM, WINDOW_TO, 5);

        // then
        assertSoftly(softly -> {
            softly.assertThat(candidates).isEmpty();
        });
    }

    private void saveActivityLog(Long crewId, Long memberId, CrewActivity activityType, LocalDateTime lastActivityAt) {
        jdbcTemplate.update(
                """
                INSERT INTO crew_activity_score (crew_id, member_id, weight, last_activity_at) VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE weight = weight + VALUES(weight), last_activity_at = GREATEST(last_activity_at, VALUES(last_activity_at))
                """,
                crewId, memberId, activityType.getWeight(), lastActivityAt
        );
    }

    private void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }
}

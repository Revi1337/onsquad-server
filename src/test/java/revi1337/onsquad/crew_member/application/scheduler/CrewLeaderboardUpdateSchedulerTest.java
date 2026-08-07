package revi1337.onsquad.crew_member.application.scheduler;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.common.fixture.CrewFixture;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Sql("/mysql-truncate.sql")
@Import({ApplicationLayerConfiguration.class})
@ContextConfiguration(initializers = {MySqlTestContainerInitializer.class, RedisTestContainerInitializer.class})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CrewLeaderboardUpdateSchedulerTest {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private CrewRankerRepository crewRankerRepository;

    @Autowired
    private CrewLeaderboardUpdateScheduler leaderboardRefreshScheduler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("스케줄러 실행 시 기존 랭킹은 사라지고, 지난 한 주간의 활동을 집계한 새로운 랭킹이 DB에 반영된다")
    void refreshLeaderboard() {
        // given
        Member revi = createRevi();
        Member andong = createAndong();
        Member kwangwon = createKwangwon();
        memberRepository.saveAll(List.of(revi, andong, kwangwon));
        // 크루 소유주의 가입 시점은 집계 기간(최근 1주)보다 훨씬 과거이므로 이번 집계에서 제외되어야 한다.
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(revi, LocalDate.now().minusDays(60).atStartOfDay()));

        // 스케줄러 실행 전 남아있던 지난 주차 랭킹 데이터 (실행 후 사라져야 한다)
        crewRankerRepository.insertBatch(List.of(createCrewRankerCandidate(crew.getId(), 1, 999, revi)));

        // 스케줄러가 집계하는 "최근 1주" 범위 안쪽 시각 (오늘로부터 3일 전)
        LocalDateTime withinLastWeek = LocalDate.now().minusDays(3).atTime(10, 0);
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, andong, withinLastWeek));
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, kwangwon, withinLastWeek.plusHours(1)));
        saveActivityLog(crew.getId(), andong.getId(), CrewActivity.CREW_PARTICIPANT, withinLastWeek);
        saveActivityLog(crew.getId(), kwangwon.getId(), CrewActivity.CREW_PARTICIPANT, withinLastWeek.plusHours(1));

        // when
        leaderboardRefreshScheduler.updateLeaderboards();

        // then
        assertSoftly(softly -> {
            List<CrewRanker> currentRankedMembers = crewRankerRepository.findAllByCrewId(crew.getId());
            softly.assertThat(currentRankedMembers).hasSize(2);
            softly.assertThat(currentRankedMembers.get(0).getMemberId())
                    .as("가입 가중치가 동점일 때는 가장 최근에 활동한 멤버가 상위 순위가 된다")
                    .isEqualTo(kwangwon.getId());
            softly.assertThat(currentRankedMembers.get(1).getMemberId()).isEqualTo(andong.getId());
            softly.assertThat(currentRankedMembers).extracting(CrewRanker::getMemberId)
                    .doesNotContain(revi.getId());
        });
    }

    private void saveActivityLog(Long crewId, Long memberId, CrewActivity activityType, LocalDateTime createdAt) {
        jdbcTemplate.update(
                "INSERT INTO crew_activity_log (crew_id, member_id, activity_type, weight, created_at) VALUES (?, ?, ?, ?, ?)",
                crewId, memberId, activityType.name(), activityType.getWeight(), createdAt
        );
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

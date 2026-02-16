package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.container.RedisTestContainerSupport;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.domain.model.CrewLeaderboard;
import revi1337.onsquad.crew_member.domain.model.CrewLeaderboards;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;

@ImportAutoConfiguration({RedisAutoConfiguration.class, JacksonAutoConfiguration.class})
@ContextConfiguration(classes = CrewLeaderboardManager.class)
@ExtendWith(SpringExtension.class)
class CrewLeaderboardManagerTest implements RedisTestContainerSupport {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CrewLeaderboardManager leaderboardManager;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Nested
    @DisplayName("특정 크루의 리더보드 순위 조회")
    class getLeaderboard {

        @Test
        @DisplayName("상위 1명 조회(rankLimit=1): 가장 높은 점수를 가진 멤버가 1위로 반환되어야 한다")
        void test1() {
            Long crewId = 1L;
            List<Long> memberIds = List.of(1L, 2L, 3L);
            Instant firstActivity = CompositeScore.BASE_DATE.toInstant();
            Instant secondActivity = firstActivity.plusSeconds(10);
            Instant thirdActivity = firstActivity.plusSeconds(10);
            leaderboardManager.applyActivity(crewId, memberIds.get(0), firstActivity, CrewActivity.CREW_PARTICIPANT);
            leaderboardManager.applyActivity(crewId, memberIds.get(1), secondActivity, CrewActivity.SQUAD_CREATE);
            leaderboardManager.applyActivity(crewId, memberIds.get(2), thirdActivity, CrewActivity.SQUAD_COMMENT);
            int rankLimit = 1;

            CrewLeaderboard leaderboard = leaderboardManager.getLeaderboard(crewId, rankLimit);

            assertSoftly(softly -> {
                softly.assertThat(leaderboard.candidateStream()).hasSize(rankLimit);
                List<CrewRankerCandidate> candidates = leaderboard.candidateStream().toList();
                softly.assertThat(candidates.get(0).memberId()).isEqualTo(memberIds.get(1));
                softly.assertThat(candidates.get(0).rank()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("다수 멤버 조회(rankLimit=2): 지정된 제한 수만큼만 점수 및 시간 순서대로 반환되어야 한다")
        void test2() {
            Long crewId = 1L;
            List<Long> memberIds = List.of(1L, 2L, 3L);
            Instant firstActivity = CompositeScore.BASE_DATE.toInstant();
            Instant secondActivity = firstActivity.plusSeconds(10);
            Instant thirdActivity = firstActivity.plusSeconds(10);
            leaderboardManager.applyActivity(crewId, memberIds.get(0), firstActivity, CrewActivity.CREW_PARTICIPANT);
            leaderboardManager.applyActivity(crewId, memberIds.get(1), secondActivity, CrewActivity.SQUAD_CREATE);
            leaderboardManager.applyActivity(crewId, memberIds.get(2), thirdActivity, CrewActivity.SQUAD_COMMENT);
            int rankLimit = 2;

            CrewLeaderboard leaderboard = leaderboardManager.getLeaderboard(crewId, rankLimit);

            assertSoftly(softly -> {
                softly.assertThat(leaderboard.candidateStream()).hasSize(rankLimit);
                List<CrewRankerCandidate> candidates = leaderboard.candidateStream().toList();
                softly.assertThat(candidates.get(0).memberId()).isEqualTo(memberIds.get(1));
                softly.assertThat(candidates.get(0).rank()).isEqualTo(1);
                softly.assertThat(candidates.get(1).memberId()).isEqualTo(memberIds.get(0));
                softly.assertThat(candidates.get(1).rank()).isEqualTo(2);
            });
        }

        @Test
        @DisplayName("전체 인원보다 큰 제한(rankLimit=4): 존재하는 모든 멤버를 순위대로 반환하며 예외가 발생하지 않는다")
        void test3() {
            Long crewId = 1L;
            List<Long> memberIds = List.of(1L, 2L, 3L);
            Instant firstActivity = CompositeScore.BASE_DATE.toInstant();
            Instant secondActivity = firstActivity.plusSeconds(10);
            Instant thirdActivity = firstActivity.plusSeconds(10);
            leaderboardManager.applyActivity(crewId, memberIds.get(0), firstActivity, CrewActivity.CREW_PARTICIPANT);
            leaderboardManager.applyActivity(crewId, memberIds.get(1), secondActivity, CrewActivity.SQUAD_CREATE);
            leaderboardManager.applyActivity(crewId, memberIds.get(2), thirdActivity, CrewActivity.SQUAD_COMMENT);
            int rankLimit = 4;

            CrewLeaderboard leaderboard = leaderboardManager.getLeaderboard(crewId, rankLimit);

            assertSoftly(softly -> {
                softly.assertThat(leaderboard.candidateStream()).hasSize(memberIds.size());
                List<CrewRankerCandidate> candidates = leaderboard.candidateStream().toList();
                softly.assertThat(candidates.get(0).memberId()).isEqualTo(memberIds.get(1));
                softly.assertThat(candidates.get(0).rank()).isEqualTo(1);
                softly.assertThat(candidates.get(1).memberId()).isEqualTo(memberIds.get(0));
                softly.assertThat(candidates.get(1).rank()).isEqualTo(2);
                softly.assertThat(candidates.get(2).memberId()).isEqualTo(memberIds.get(2));
                softly.assertThat(candidates.get(2).rank()).isEqualTo(3);
            });
        }

        @Test
        @DisplayName("제한 없음(rankLimit=-1): 해당 크루의 모든 멤버를 순위별로 전체 조회한다")
        void test4() {
            Long crewId = 1L;
            List<Long> memberIds = List.of(1L, 2L, 3L);
            Instant firstActivity = CompositeScore.BASE_DATE.toInstant();
            Instant secondActivity = firstActivity.plusSeconds(10);
            Instant thirdActivity = firstActivity.plusSeconds(10);
            leaderboardManager.applyActivity(crewId, memberIds.get(0), firstActivity, CrewActivity.CREW_PARTICIPANT);
            leaderboardManager.applyActivity(crewId, memberIds.get(1), secondActivity, CrewActivity.SQUAD_CREATE);
            leaderboardManager.applyActivity(crewId, memberIds.get(2), thirdActivity, CrewActivity.SQUAD_COMMENT);
            int rankLimit = -1;

            CrewLeaderboard leaderboard = leaderboardManager.getLeaderboard(crewId, rankLimit);

            assertSoftly(softly -> {
                softly.assertThat(leaderboard.candidateStream()).hasSize(memberIds.size());
                List<CrewRankerCandidate> candidates = leaderboard.candidateStream().toList();
                softly.assertThat(candidates.get(0).memberId()).isEqualTo(memberIds.get(1));
                softly.assertThat(candidates.get(0).rank()).isEqualTo(1);
                softly.assertThat(candidates.get(1).memberId()).isEqualTo(memberIds.get(0));
                softly.assertThat(candidates.get(1).rank()).isEqualTo(2);
                softly.assertThat(candidates.get(2).memberId()).isEqualTo(memberIds.get(2));
                softly.assertThat(candidates.get(2).rank()).isEqualTo(3);
            });
        }

        @Test
        @DisplayName("데이터 부재(invalidCrewId): 활동 내역이 없는 크루 조회 시 빈 리스트를 반환한다")
        void test5() {
            Long invalidCrewId = 999L;
            int rankLimit = 3;

            CrewLeaderboard leaderboard = leaderboardManager.getLeaderboard(invalidCrewId, rankLimit);

            assertThat(leaderboard.size()).isZero();
        }

        @Test
        @DisplayName("동일 점수 발생 시, 더 나중(최신)에 활동한 멤버가 더 높은 순위를 차지한다")
        void test6() {
            Long crewId = 1L;
            Long memberId1 = 1L;
            Long memberId2 = 2L;
            Instant firstActivity = CompositeScore.BASE_DATE.toInstant();
            Instant secondActivity = firstActivity.plusSeconds(10);
            CrewActivity activity = CrewActivity.SQUAD_CREATE;
            leaderboardManager.applyActivity(crewId, memberId1, firstActivity, activity);
            leaderboardManager.applyActivity(crewId, memberId2, secondActivity, activity);

            CrewLeaderboards leaderboards = leaderboardManager.getAllLeaderboards(3);

            assertSoftly(softly -> {
                softly.assertThat(leaderboards.size()).isEqualTo(1);
                CrewLeaderboard leaderboard = leaderboards.getLeaderboard(crewId);
                List<CrewRankerCandidate> candidates = leaderboard.candidateStream().toList();
                softly.assertThat(candidates).hasSize(2);
                softly.assertThat(candidates.get(0).memberId()).isEqualTo(memberId2);
                softly.assertThat(candidates.get(0).rank()).isEqualTo(1);
                softly.assertThat(candidates.get(0).score()).isEqualTo(activity.getScore());
                softly.assertThat(candidates.get(0).lastActivityTime()).isEqualTo(LocalDateTime.ofInstant(secondActivity, CompositeScore.KST));
                softly.assertThat(candidates.get(1).memberId()).isEqualTo(memberId1);
                softly.assertThat(candidates.get(1).rank()).isEqualTo(2);
                softly.assertThat(candidates.get(1).score()).isEqualTo(activity.getScore());
                softly.assertThat(candidates.get(1).lastActivityTime()).isEqualTo(LocalDateTime.ofInstant(firstActivity, CompositeScore.KST));
            });
        }

        @Test
        @DisplayName("동일 점수 & 최신 활동 날짜가 같을 시, 사전 편찬순으로 적용된다.")
        void test7() {
            Long crewId = 1L;
            Instant activityTime = CompositeScore.BASE_DATE.toInstant();
            CrewActivity activity = CrewActivity.SQUAD_CREATE;
            leaderboardManager.applyActivity(crewId, 2L, activityTime, activity);
            leaderboardManager.applyActivity(crewId, 1L, activityTime, activity);

            CrewLeaderboards leaderboards = leaderboardManager.getAllLeaderboards(3);

            assertSoftly(softly -> {
                List<CrewRankerCandidate> candidates = leaderboards.getLeaderboard(crewId).candidateStream().toList();
                softly.assertThat(candidates.get(0).memberId()).isEqualTo(2L);
                softly.assertThat(candidates.get(0).rank()).isEqualTo(1);
                softly.assertThat(candidates.get(1).memberId()).isEqualTo(1L);
                softly.assertThat(candidates.get(1).rank()).isEqualTo(2);
            });
        }
    }

    @Nested
    @DisplayName("리더보드 관리 기능 메서드 테스트")
    class MethodTest {

        @Test
        @DisplayName("상위 순위의 크루 멤버들을 점수 및 활동 시간 순서에 맞춰 정확히 추출한다")
        void getLeaderboard() {
            Long crewId = 1L;
            Instant activityTime = CompositeScore.BASE_DATE.plusSeconds(10).toInstant();
            leaderboardManager.applyActivity(crewId, 1L, activityTime, CrewActivity.SQUAD_CREATE);
            leaderboardManager.applyActivity(crewId, 2L, activityTime, CrewActivity.SQUAD_COMMENT);
            leaderboardManager.applyActivity(crewId, 3L, activityTime, CrewActivity.CREW_PARTICIPANT);

            CrewLeaderboard leaderboard = leaderboardManager.getLeaderboard(crewId, 3);

            assertSoftly(softly -> {
                List<CrewRankerCandidate> candidates = leaderboard.candidateStream().toList();
                softly.assertThat(candidates).hasSize(3);
                softly.assertThat(candidates.get(0).memberId()).isEqualTo(1L);
                softly.assertThat(candidates.get(0).rank()).isEqualTo(1);
                softly.assertThat(candidates.get(1).memberId()).isEqualTo(3L);
                softly.assertThat(candidates.get(1).rank()).isEqualTo(2);
                softly.assertThat(candidates.get(2).memberId()).isEqualTo(2L);
                softly.assertThat(candidates.get(2).rank()).isEqualTo(3);
            });
        }

        @Test
        @DisplayName("특정 순위 구간(예: 2위~3위)에 속한 멤버들만 선택적으로 조회한다")
        void getLeaderboardRank() {
            Long crewId = 1L;
            Instant activityTime = CompositeScore.BASE_DATE.plusSeconds(10).toInstant();
            leaderboardManager.applyActivity(crewId, 1L, activityTime, CrewActivity.SQUAD_CREATE);
            leaderboardManager.applyActivity(crewId, 2L, activityTime, CrewActivity.SQUAD_COMMENT);
            leaderboardManager.applyActivity(crewId, 3L, activityTime, CrewActivity.CREW_PARTICIPANT);

            CrewLeaderboard leaderboard = leaderboardManager.getLeaderboard(crewId, 2, 3);

            assertSoftly(softly -> {
                List<CrewRankerCandidate> candidates = leaderboard.candidateStream().toList();
                softly.assertThat(candidates).hasSize(2);
                softly.assertThat(candidates.get(0).memberId()).isEqualTo(3L);
                softly.assertThat(candidates.get(0).rank()).isEqualTo(2);
                softly.assertThat(candidates.get(1).memberId()).isEqualTo(2L);
                softly.assertThat(candidates.get(1).rank()).isEqualTo(3);
            });
        }

        @Test
        @DisplayName("시스템에 존재하는 모든 크루의 리더보드 데이터를 빠짐없이 통합 조회한다")
        void getAllLeaderboards() {
            List<Long> crewIds = List.of(1L, 2L, 3L);
            Long memberId = 1L;
            Instant activityTime = CompositeScore.BASE_DATE.plusSeconds(10).toInstant();
            CrewActivity activity = CrewActivity.SQUAD_CREATE;
            leaderboardManager.applyActivity(crewIds.get(0), memberId, activityTime, activity);
            leaderboardManager.applyActivity(crewIds.get(1), memberId, activityTime, activity);
            leaderboardManager.applyActivity(crewIds.get(2), memberId, activityTime, activity);

            leaderboardManager.removeAllLeaderboards();

            assertThat(leaderboardManager.getAllLeaderboards(3).size()).isZero();
        }

        @Test
        @DisplayName("현재 저장된 모든 크루 리더보드 정보를 메모리에서 일괄 제거한다")
        void removeAllLeaderboards() {
            List<Long> crewIds = List.of(1L, 2L, 3L);
            Long memberId = 1L;
            Instant activityTime = CompositeScore.BASE_DATE.plusSeconds(10).toInstant();
            CrewActivity activity = CrewActivity.SQUAD_CREATE;
            leaderboardManager.applyActivity(crewIds.get(0), memberId, activityTime, activity);
            leaderboardManager.applyActivity(crewIds.get(1), memberId, activityTime, activity);
            leaderboardManager.applyActivity(crewIds.get(2), memberId, activityTime, activity);

            leaderboardManager.removeAllLeaderboards();

            assertThat(leaderboardManager.getAllLeaderboards(3).size()).isZero();
        }

        @Test
        @DisplayName("전달된 식별자 목록에 해당하는 특정 크루들의 리더보드만 선택하여 삭제한다")
        void removeLeaderboards() {
            List<Long> crewIds = List.of(1L, 2L, 3L);
            Long memberId = 1L;
            Instant activityTime = CompositeScore.BASE_DATE.plusSeconds(10).toInstant();
            CrewActivity activity = CrewActivity.SQUAD_CREATE;
            leaderboardManager.applyActivity(crewIds.get(0), memberId, activityTime, activity);
            leaderboardManager.applyActivity(crewIds.get(1), memberId, activityTime, activity);
            leaderboardManager.applyActivity(crewIds.get(2), memberId, activityTime, activity);

            leaderboardManager.removeLeaderboards(crewIds);

            assertThat(leaderboardManager.getAllLeaderboards(3).size()).isZero();
        }

        @Test
        @DisplayName("합성 점수 포맷에서 시간 가중치를 제외한 순수 활동 점수만을 계산하여 반환한다")
        void getScore() {
            Long crewId = 1L;
            Long memberId = 2L;
            Instant activityTime = CompositeScore.BASE_DATE.plusSeconds(10).toInstant();
            CrewActivity activity = CrewActivity.SQUAD_CREATE;
            leaderboardManager.applyActivity(crewId, memberId, activityTime, activity);
            leaderboardManager.applyActivity(crewId, memberId, activityTime, activity);
            leaderboardManager.applyActivity(crewId, memberId, activityTime, activity);
            long expectScore = 30;

            long score = leaderboardManager.getScore(crewId, memberId);

            assertThat(score).isEqualTo(expectScore);
        }
    }
}

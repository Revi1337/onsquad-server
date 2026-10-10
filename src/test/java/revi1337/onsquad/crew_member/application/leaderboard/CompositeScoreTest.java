package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 2⁵³(9,007,199,254,740,992) 마지노선
 */
class CompositeScoreTest {

    @Nested
    class of {

        @Test
        @DisplayName("동일 점수일 때 시간이 흐를수록 더 높은 rawValue를 생성해야 한다")
        void test1() {
            Instant time1 = CompositeScore.BASE_DATE.plusSeconds(10).toInstant();
            Instant time2 = CompositeScore.BASE_DATE.plusSeconds(11).toInstant();

            CompositeScore score1 = CompositeScore.of(100L, time1);
            CompositeScore score2 = CompositeScore.of(100L, time2);

            assertThat(score2.getRawValue()).isGreaterThan(score1.getRawValue());
        }

        @Test
        @DisplayName("점수가 0점이어도 시간 정보는 유실되지 않고 rawValue에 반영되어야 한다")
        void test2() {
            Instant time = CompositeScore.BASE_DATE.plusHours(1).toInstant();

            CompositeScore score = CompositeScore.of(0L, time);

            assertThat(score.getActualScore()).isEqualTo(0L);
            assertThat(score.getRawValue()).isEqualTo(3600L);
        }
    }

    @Nested
    class from {

        @Test
        @DisplayName("소수점이 없는 깨끗한 정수 값이 들어오면 그대로 복원해야 한다")
        void test1() {
            double redisScore = 100_000_000_050.0;

            CompositeScore result = CompositeScore.from(redisScore);

            assertThat(result.getRawValue()).isEqualTo(100_000_000_050L);
        }

        @Test
        @DisplayName("부동소수점 오차로 인해 값이 미세하게 작게(Epsilon) 들어와도 반올림하여 원래 정수를 찾아야 한다")
        void test2() {
            double redisScore = 100_000_000_049.999999999;

            CompositeScore result = CompositeScore.from(redisScore);

            assertThat(result.getRawValue()).isEqualTo(100_000_000_050L);
        }

        @Test
        @DisplayName("부동소수점 오차로 인해 값이 미세하게 크게 들어와도 반올림하여 원래 정수를 유지해야 한다")
        void test3() {
            double redisScore = 100_000_000_050.000000001;

            CompositeScore result = CompositeScore.from(redisScore);

            assertThat(result.getRawValue()).isEqualTo(100_000_000_050L);
        }

        @Test
        @DisplayName("현실적인 범위(10만 점) 내에서는 정밀도 손실 없이 복원되어야 한다")
        void test4() {
            long originalRaw = (100_000L * CompositeScore.MULTIPLIER) + 9_999_999_999L;
            double redisScore = (double) originalRaw;

            CompositeScore result = CompositeScore.from(redisScore);

            assertThat(result.getRawValue()).as("9,007,199,254,740,992").isEqualTo(originalRaw);
        }

        @Test
        @DisplayName("score: 900,719, relativeTime: 9,254,740,992 --> 9,007,199,254,740,992 최대 점수 기반 경계값")
        void maxScoreBoundary() {
            long originalRaw = (900_719L * CompositeScore.MULTIPLIER) + 9_254_740_992L;
            double redisScore = (double) originalRaw;

            CompositeScore result = CompositeScore.from(redisScore);

            assertThat(result.getActualScore())
                    .isEqualTo(900_719L);
        }

        @Test
        @DisplayName("최대 점수 기반 경계값 1-1: score=900,719 고정, relativeTime이 9,254,740,992를 넘으면 시간만 어긋난다")
        void maxScoreBoundary_1() {
            long originalRaw = (900_719L * CompositeScore.MULTIPLIER) + 9_254_740_993L;
            double redisScore = (double) originalRaw;

            CompositeScore result = CompositeScore.from(redisScore);

            assertThat(result.getActualScore())
                    .as("점수는 캐리 없이 900,719 그대로 유지됨")
                    .isEqualTo(900_719L);
            assertThat(result.getRawValue())
                    .as("원래 9,254,740,993초여야 하는데 1초 어긋난 9,254,740,992초로 깨짐")
                    .isEqualTo((900_719L * CompositeScore.MULTIPLIER) + 9_254_740_992L);
        }

        @Test
        @DisplayName("최대 점수 기반 경계값 1-2: relativeTime=9,254,740,992 고정, score가 900,719를 넘어도 relativeTime이 짝수라 당분간 안 깨진다")
        void maxScoreBoundary_2() {
            long originalRaw = (900_720L * CompositeScore.MULTIPLIER) + 9_254_740_992L;
            double redisScore = (double) originalRaw;

            CompositeScore result = CompositeScore.from(redisScore);

            assertThat(result.getRawValue())
                    .as("relativeTime이 짝수라 score*MULTIPLIER+relativeTime도 짝수가 되어, 2^54 전까지는 정확히 보존됨")
                    .isEqualTo(originalRaw);
        }

        @Test
        @DisplayName("score: 900,718, relativeTime: 9,999,999,999 --> 9,007,189,999,999,999 최대 활동시간 기반 경계값")
        void maxActivityTimeBoundary() {
            long originalRaw = (900_718L * CompositeScore.MULTIPLIER) + 9_999_999_999L;
            double redisScore = (double) originalRaw;

            CompositeScore result = CompositeScore.from(redisScore);

            assertThat(result.getActualScore())
                    .isEqualTo(900_718L);
        }

        @Test
        @DisplayName("최대 활동시간 기반 경계값 2-1: score=900,718 고정, relativeTime이 설계 한도(MULTIPLIER) 자체를 넘으면 점수까지 확정적으로 캐리된다")
        void maxActivityTimeBoundary_1() {
            long originalRaw = (900_718L * CompositeScore.MULTIPLIER) + CompositeScore.MULTIPLIER;
            double redisScore = (double) originalRaw;

            CompositeScore result = CompositeScore.from(redisScore);

            assertThat(result.getActualScore())
                    .as("double 정밀도 문제가 아니라, 자릿수 설계 자체를 넘어서 확정적으로 900,719로 캐리됨")
                    .isEqualTo(900_719L);
            assertThat(result.getActivityTime())
                    .as("relativeTime이 통째로 0으로 리셋됨")
                    .isEqualTo(LocalDateTime.of(2026, 1, 1, 0, 0, 0));
        }

        @Test
        @DisplayName("최대 활동시간 기반 경계값 2-2: relativeTime=9,999,999,999 고정, score가 900,718을 넘으면 완충 없이 즉시 전면 붕괴한다")
        void maxActivityTimeBoundary_2() {
            long originalRaw = (900_719L * CompositeScore.MULTIPLIER) + 9_999_999_999L;
            double redisScore = (double) originalRaw;

            CompositeScore result = CompositeScore.from(redisScore);

            assertThat(result.getActualScore())
                    .as("점수와 시간이 동시에 깨짐: 900,720으로 캐리")
                    .isEqualTo(900_720L);
            assertThat(result.getActivityTime())
                    .as("relativeTime도 동시에 0으로 리셋됨")
                    .isEqualTo(LocalDateTime.of(2026, 1, 1, 0, 0, 0));
        }
    }

    @Nested
    class toRedisScore {

        @Test
        @DisplayName("90만 점(한계치 이내)일 때, double로 변환해도 원본 long 값이 완벽히 보존된다")
        void test1() {
            long score = 900_000L;
            Instant activityTime = CompositeScore.BASE_DATE.toInstant();
            CompositeScore compositeScore = CompositeScore.of(score, activityTime); // 900,000점 + 0초 (9,000,000,000,000,000)
            long originalRaw = compositeScore.getRawValue();
            System.out.println(originalRaw);

            double redisScore = compositeScore.toRedisScore();

            assertThat((long) redisScore).isEqualTo(originalRaw);
        }

        @Test
        @DisplayName("100만 점(한계치 초과)일 때, double 변환 시 정밀도 손실로 인해 원본과 달라질 수 있음을 확인")
        void test2() {
            long score = 1_000_000L;
            Instant activityTime = CompositeScore.BASE_DATE.plusSeconds(1).toInstant();
            CompositeScore compositeScore = CompositeScore.of(score, activityTime); // 1,000,000점 + 1초 (10,000,000,000,001)
            long originalRaw = compositeScore.getRawValue(); // 이 값은 2^53을 넘어가서 double이 1의 자리를 뭉갬

            double redisScore = compositeScore.toRedisScore();

            assertThat((long) redisScore).isNotEqualTo(originalRaw);
        }
    }

    @Nested
    class getActualScore {

        @Test
        @DisplayName("점수와 시간이 섞인 rawValue에서 점수 파트만 정확히 추출해야 한다")
        void test1() {
            long score = 12_345L;
            Instant activityTime = CompositeScore.BASE_DATE.plusSeconds(5_000_000_000L).toInstant();
            CompositeScore compositeScore = CompositeScore.of(score, activityTime);

            long actualScore = compositeScore.getActualScore();

            assertThat(actualScore).isEqualTo(12_345L);
        }

        @Test
        @DisplayName("시간(relativeSeconds)이 0일 때도 점수를 정확히 반환해야 한다")
        void test2() {
            long score = 7_777L;
            Instant activityTime = CompositeScore.BASE_DATE.toInstant();
            CompositeScore compositeScore = CompositeScore.of(score, activityTime);

            long actualScore = compositeScore.getActualScore();

            assertThat(actualScore).isEqualTo(7_777L);
        }

        @Test
        @DisplayName("시간이 MULTIPLIER 직전(9,999,999,999초)까지 커져도 점수에 영향을 주지 않아야 한다")
        void test3() {
            long score = 100L;
            long maxSeconds = 9_999_999_999L;
            Instant maxTime = CompositeScore.BASE_DATE.plusSeconds(maxSeconds).toInstant();
            CompositeScore compositeScore = CompositeScore.of(score, maxTime);

            long actualScore = compositeScore.getActualScore();

            assertThat(actualScore).isEqualTo(100L);
        }
    }

    @Nested
    class getActivityTime {

        @Test
        @DisplayName("저장된 상대적(UTC) 초 단위 값을 실제 LocalDateTime(KST)으로 정확히 복원한다.")
        void test() {
            Instant activityTime = LocalDateTime.of(2026, 1, 1, 1, 0).toInstant(ZoneOffset.UTC);
            CompositeScore compositeScore = CompositeScore.of(10, activityTime);

            LocalDateTime recoveredTime = compositeScore.getActivityTime();

            assertThat(recoveredTime.getYear()).isEqualTo(2026);
            assertThat(recoveredTime.getMonthValue()).isEqualTo(1);
            assertThat(recoveredTime.getDayOfMonth()).isEqualTo(1);
            assertThat(recoveredTime.getHour()).as("UTC와 KST는 9시간 차이").isEqualTo(10);
            assertThat(recoveredTime.getMinute()).isEqualTo(0);
            assertThat(recoveredTime.getSecond()).isEqualTo(0);
        }
    }
}

package revi1337.onsquad.crew_member.application.leaderboard;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class CompositeScore {

    public static final ZoneId KST = ZoneId.of("Asia/Seoul");
    public static final long MULTIPLIER = 10_000_000_000L;
    public static final ZonedDateTime BASE_DATE = LocalDate.of(2026, 1, 1).atStartOfDay(KST);
    public static final long BASE_EPOCH_TIME = BASE_DATE.toEpochSecond();

    private final long value;

    private CompositeScore(long value) {
        this.value = value;
    }

    public static CompositeScore of(long score, Instant activityTime) {
        long relativeSeconds = activityTime.getEpochSecond() - BASE_EPOCH_TIME;
        return new CompositeScore((score * MULTIPLIER) + relativeSeconds);
    }

    public static CompositeScore from(double redisScore) {
        return new CompositeScore(Math.round(redisScore));
    }

    public double toRedisScore() {
        return (double) value;
    }

    public long getActualScore() {
        return value / MULTIPLIER;
    }

    public LocalDateTime getActivityTime() {
        long relativeSeconds = value % MULTIPLIER;
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(relativeSeconds + BASE_EPOCH_TIME),
                KST
        );
    }

    public long getRawValue() {
        return value;
    }
}

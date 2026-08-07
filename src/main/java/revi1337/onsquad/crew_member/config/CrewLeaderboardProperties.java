package revi1337.onsquad.crew_member.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import revi1337.onsquad.common.config.system.properties.SchedulingProperty;

@ConfigurationProperties(prefix = "onsquad.api.crew-leaderboard")
public record CrewLeaderboardProperties(
        Duration during,
        Integer rankLimit,
        @NestedConfigurationProperty SchedulingProperty schedule
) {

    /**
     * 탈퇴 유저 필터링을 위해 최종 노출 순위({@link #rankLimit})보다 넉넉하게 조회하는 후보 상한.
     * over-fetch 된 후보 중 유효 멤버십만 남긴 뒤 앞에서부터 rankLimit 개수만큼 다시 순위를 매긴다.
     */
    public static final int OVER_FETCH_LIMIT = 50;

    public CrewLeaderboardProperties(
            @DefaultValue("7d") Duration during,
            @DefaultValue("4") Integer rankLimit,
            SchedulingProperty schedule
    ) {
        this.during = during;
        this.rankLimit = rankLimit;
        this.schedule = schedule;
    }
}

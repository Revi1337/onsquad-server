package revi1337.onsquad.infrastructure.caffeine;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.constant.CacheConst;

@Getter
@RequiredArgsConstructor
public enum CaffeineConst {

    CREW_ANNOUNCES(CacheConst.CREW_ANNOUNCES, Duration.ofHours(1)),
    CREW_ANNOUNCE(CacheConst.CREW_ANNOUNCE, Duration.ofHours(1)),
    CREW_STATISTIC(CacheConst.CREW_STATISTIC, Duration.ofHours(1)),
    CREW_RANK_MEMBERS(CacheConst.CREW_RANK_MEMBERS, Duration.ofHours(1));

    private final String cacheName;
    private final Duration expired;

    public static Stream<CaffeineConst> stream() {
        return Arrays.stream(CaffeineConst.values());
    }
}

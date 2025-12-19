package revi1337.onsquad.crew.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.io.Serializable;

public record CrewStatisticResult(
        long requestCnt,
        long squadCnt,
        long memberCnt
) implements Serializable {

    @QueryProjection
    public CrewStatisticResult(long requestCnt, long squadCnt, long memberCnt) {
        this.requestCnt = requestCnt;
        this.squadCnt = squadCnt;
        this.memberCnt = memberCnt;
    }
}

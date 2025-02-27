package revi1337.onsquad.crew.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.io.Serializable;

public record CrewStatisticDomainDto(
        long requestCnt,
        long squadCnt,
        long memberCnt
) implements Serializable {

    @QueryProjection
    public CrewStatisticDomainDto(long requestCnt, long squadCnt, long memberCnt) {
        this.requestCnt = requestCnt;
        this.squadCnt = squadCnt;
        this.memberCnt = memberCnt;
    }
}

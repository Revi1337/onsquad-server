package revi1337.onsquad.squad.domain.result;

import com.querydsl.core.annotations.QueryProjection;

public record SquadWithLeaderStateResult(
        Boolean isLeader,
        SimpleSquadResult squad
) {

    @QueryProjection
    public SquadWithLeaderStateResult(Boolean isLeader, SimpleSquadResult squad) {
        this.isLeader = isLeader;
        this.squad = squad;
    }
}

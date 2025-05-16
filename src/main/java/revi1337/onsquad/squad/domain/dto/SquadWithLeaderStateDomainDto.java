package revi1337.onsquad.squad.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

public record SquadWithLeaderStateDomainDto(
        Boolean isLeader,
        SquadDomainDto squad
) {
    @QueryProjection
    public SquadWithLeaderStateDomainDto(Boolean isLeader, SquadDomainDto squad) {
        this.isLeader = isLeader;
        this.squad = squad;
    }
}

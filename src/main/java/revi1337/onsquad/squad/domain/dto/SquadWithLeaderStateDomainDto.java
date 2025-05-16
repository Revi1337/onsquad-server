package revi1337.onsquad.squad.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

public record SquadWithLeaderStateDomainDto(
        Boolean isLeader,
        SimpleSquadDomainDto squad
) {
    @QueryProjection
    public SquadWithLeaderStateDomainDto(Boolean isLeader, SimpleSquadDomainDto squad) {
        this.isLeader = isLeader;
        this.squad = squad;
    }
}

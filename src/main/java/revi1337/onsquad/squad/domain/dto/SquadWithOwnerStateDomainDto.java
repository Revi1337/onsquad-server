package revi1337.onsquad.squad.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

public record SquadWithOwnerStateDomainDto(
        Boolean isOwner,
        SquadDomainDto squad
) {
    @QueryProjection
    public SquadWithOwnerStateDomainDto(Boolean isOwner, SquadDomainDto squad) {
        this.isOwner = isOwner;
        this.squad = squad;
    }
}

package revi1337.onsquad.squad.application.dto;

import revi1337.onsquad.squad.domain.dto.SquadDomainDto;

public record SquadWithParticipantAndLeaderAndViewStateDto(
        boolean alreadyParticipant,
        boolean isLeader,
        boolean canSeeMembers,
        SquadDto squad
) {
    public static SquadWithParticipantAndLeaderAndViewStateDto from(boolean alreadyParticipant,
                                                                    boolean canManage,
                                                                    boolean isLeader,
                                                                    SquadDomainDto squadDomainDto) {
        return new SquadWithParticipantAndLeaderAndViewStateDto(
                alreadyParticipant,
                isLeader,
                canManage,
                SquadDto.from(squadDomainDto)
        );
    }
}

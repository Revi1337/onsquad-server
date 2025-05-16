package revi1337.onsquad.squad.application.dto;

import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;

public record SquadWithParticipantAndLeaderAndViewStateDto(
        boolean alreadyParticipant,
        boolean isLeader,
        boolean canSeeMembers,
        SquadInfoDto squad
) {
    public static SquadWithParticipantAndLeaderAndViewStateDto from(boolean alreadyParticipant,
                                                                    boolean canManage,
                                                                    boolean isLeader,
                                                                    SquadInfoDomainDto squadInfoDomainDto) {
        return new SquadWithParticipantAndLeaderAndViewStateDto(
                alreadyParticipant,
                isLeader,
                canManage,
                SquadInfoDto.from(squadInfoDomainDto)
        );
    }
}

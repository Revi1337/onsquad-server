package revi1337.onsquad.squad.presentation.dto.response;

import revi1337.onsquad.squad.application.dto.SquadWithParticipantAndLeaderAndViewStateDto;

public record SquadWithParticipantAndLeaderAndViewStateResponse(
        boolean alreadyParticipant,
        boolean isLeader,
        boolean canSeeMembers,
        SquadInfoResponse squad
) {
    public static SquadWithParticipantAndLeaderAndViewStateResponse from(
            SquadWithParticipantAndLeaderAndViewStateDto squadWithParticipantAndLeaderAndViewStateDto
    ) {
        return new SquadWithParticipantAndLeaderAndViewStateResponse(
                squadWithParticipantAndLeaderAndViewStateDto.alreadyParticipant(),
                squadWithParticipantAndLeaderAndViewStateDto.isLeader(),
                squadWithParticipantAndLeaderAndViewStateDto.canSeeMembers(),
                SquadInfoResponse.from(squadWithParticipantAndLeaderAndViewStateDto.squad())
        );
    }
}

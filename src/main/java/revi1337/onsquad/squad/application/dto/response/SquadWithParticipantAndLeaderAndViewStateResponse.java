package revi1337.onsquad.squad.application.dto.response;

import revi1337.onsquad.squad.domain.result.SquadResult;

public record SquadWithParticipantAndLeaderAndViewStateResponse(
        boolean alreadyParticipant,
        boolean isLeader,
        boolean canSeeMembers,
        SquadResponse squad
) {

    public static SquadWithParticipantAndLeaderAndViewStateResponse from(
            boolean alreadyParticipant,
            boolean canManage,
            boolean isLeader,
            SquadResult squadResult
    ) {
        return new SquadWithParticipantAndLeaderAndViewStateResponse(
                alreadyParticipant,
                isLeader,
                canManage,
                SquadResponse.from(squadResult)
        );
    }
}

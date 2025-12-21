package revi1337.onsquad.squad.application.dto.response;

import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.result.SquadResult;

public record SquadWithStatesResponse(
        boolean alreadyParticipant,
        boolean isLeader,
        boolean canSeeMembers,
        SquadResponse squad
) {

    public static SquadWithStatesResponse from(boolean alreadyParticipant, boolean canManage, boolean isLeader, SquadResult squadResult) {
        return new SquadWithStatesResponse(
                alreadyParticipant,
                isLeader,
                canManage,
                SquadResponse.from(squadResult)
        );
    }

    public static SquadWithStatesResponse from(boolean alreadyParticipant, boolean canManage, boolean isLeader, Squad squad) {
        return new SquadWithStatesResponse(
                alreadyParticipant,
                isLeader,
                canManage,
                SquadResponse.from(squad)
        );
    }
}

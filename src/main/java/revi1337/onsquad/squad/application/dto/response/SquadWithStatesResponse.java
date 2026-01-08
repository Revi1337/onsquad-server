package revi1337.onsquad.squad.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.squad.domain.entity.Squad;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadWithStatesResponse(
        boolean alreadyParticipant,
        Boolean isLeader,
        boolean canSeeParticipants,
        Boolean canLeave,
        boolean canDelete,
        SquadResponse squad
) {

    public static SquadWithStatesResponse from(
            boolean alreadyParticipant,
            Boolean isLeader,
            boolean canSeeParticipants,
            Boolean canLeave,
            boolean canDelete,
            Squad squad
    ) {
        return new SquadWithStatesResponse(
                alreadyParticipant,
                isLeader,
                canSeeParticipants,
                canLeave,
                canDelete,
                SquadResponse.from(squad)
        );
    }
}

package revi1337.onsquad.squad.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadStates(
        Boolean alreadyParticipant,
        Boolean isLeader,
        Boolean canSeeParticipants,
        Boolean canLeave,
        Boolean canDelete
) {

    public static SquadStates of(
            boolean alreadyParticipant,
            Boolean isLeader,
            boolean canSeeParticipants,
            Boolean canLeave,
            boolean canDelete
    ) {
        return new SquadStates(alreadyParticipant, isLeader, canSeeParticipants, canLeave, canDelete);
    }

    public static SquadStates of(boolean isLeader) {
        return new SquadStates(null, isLeader, null, null, null);
    }
}

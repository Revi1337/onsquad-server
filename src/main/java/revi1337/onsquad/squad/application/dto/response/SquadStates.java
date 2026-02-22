package revi1337.onsquad.squad.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadStates(
        Boolean alreadyParticipant,
        Boolean isLeader,
        Boolean canSeeParticipants,
        Boolean canLeave,
        Boolean canDelete,
        Boolean canDestroy
) {

    public static SquadStates of(
            boolean alreadyParticipant,
            Boolean isLeader,
            boolean canSeeParticipants,
            Boolean canLeave,
            boolean canDelete
    ) {
        return new SquadStates(alreadyParticipant, isLeader, canSeeParticipants, canLeave, canDelete, null);
    }

    public static SquadStates of(boolean isLeader, boolean canDestroy) {
        return new SquadStates(null, isLeader, null, null, null, canDestroy);
    }
}

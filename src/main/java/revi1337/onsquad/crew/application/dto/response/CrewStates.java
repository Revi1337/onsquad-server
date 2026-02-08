package revi1337.onsquad.crew.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewStates(
        Boolean alreadyParticipant,
        Boolean canModify,
        Boolean canDelete,
        Boolean canManage
) {

    public CrewStates(Boolean alreadyParticipant) {
        this(alreadyParticipant, null, null, null);
    }

    public CrewStates(boolean canModify, boolean canDelete) {
        this(null, canModify, canDelete, null);
    }

    public CrewStates(boolean canManage) {
        this(null, null, null, canManage);
    }
}

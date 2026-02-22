package revi1337.onsquad.crew.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewStates(
        Boolean alreadyRequest,
        Boolean alreadyParticipant,
        Boolean canModify,
        Boolean canDelete,
        Boolean canManage
) {

    public CrewStates(Boolean alreadyRequest, Boolean alreadyParticipant) {
        this(alreadyRequest, alreadyParticipant, null, null, null);
    }

    public CrewStates(boolean canModify, boolean canDelete) {
        this(null, null, canModify, canDelete, null);
    }

    public CrewStates(boolean canManage) {
        this(null, null, null, null, canManage);
    }
}
